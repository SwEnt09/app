package com.github.swent.echo.data.repository

import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.LocalDataSource
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import java.time.ZonedDateTime

class RepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val networkService: NetworkService
) : Repository {

    private val isOffline: () -> Boolean = { !networkService.isOnlineNow() }
    private val currentTimeStamp: () -> Long = { ZonedDateTime.now().toEpochSecond() }

    private fun notExpired(cachedTimeStamp: Long, ttl: Long): Boolean {
        return currentTimeStamp() - cachedTimeStamp <= ttl
    }

    companion object {
        const val ASSOCIATION_CACHE_TTL: Long = 60 * 60
        const val EVENT_CACHE_TTL: Long = 60 * 5
        const val TAG_CACHE_TTL: Long = 60 * 60 * 24
        const val USERPROFILE_CACHE_TTL: Long = 60 * 15
        const val FETCH_ALL: Long =
            1715885732 // Epoch seconds of ~16.05.2024 so we fetch everything up to ~50 years ago

        var associations_last_cached_all: Long = 0
        var events_last_cached_all: Long = 0
        var events_last_cached_joined: MutableMap<String, Long> = mutableMapOf()
        var tags_last_cached_all: Long = 0
        var tags_last_cached_subs: MutableMap<String, Long> = mutableMapOf()
    }

    override suspend fun getAssociation(associationId: String): Association? {
        if (isOffline()) {
            return localDataSource.getAssociation(associationId, FETCH_ALL)
        }
        val localResult = localDataSource.getAssociation(associationId, ASSOCIATION_CACHE_TTL)
        if (localResult == null) {
            val remoteResult = remoteDataSource.getAssociation(associationId)
            if (remoteResult != null) localDataSource.setAssociation(remoteResult)
            return remoteResult
        }
        return localResult
    }

    override suspend fun getAssociations(associationIds: List<String>): List<Association> {
        if (isOffline()) {
            return localDataSource.getAssociations(associationIds, FETCH_ALL)
        }
        val localResult = localDataSource.getAssociations(associationIds, ASSOCIATION_CACHE_TTL)
        val associationIdsNotInLocalResult = associationIds - localResult.map { it.associationId }
        if (associationIdsNotInLocalResult != emptyList<String>()) {
            val remoteResult = remoteDataSource.getAssociations(associationIdsNotInLocalResult)
            localDataSource.setAssociations(remoteResult)
            return localResult + remoteResult
        }
        return localResult
    }

    override suspend fun getAllAssociations(): List<Association> {
        if (isOffline() || notExpired(associations_last_cached_all, ASSOCIATION_CACHE_TTL)) {
            return localDataSource.getAllAssociations(FETCH_ALL)
        }
        val nonExpired = localDataSource.getAllAssociations(ASSOCIATION_CACHE_TTL)
        val nonExpiredIds = nonExpired.map { it.associationId }
        val remoteUpdate = remoteDataSource.getAssociationsNotIn(nonExpiredIds)
        localDataSource.deleteAssociationsNotIn(nonExpiredIds)
        localDataSource.setAssociations(remoteUpdate)
        associations_last_cached_all = currentTimeStamp()
        return nonExpired + remoteUpdate
    }

    override suspend fun getEvent(eventId: String): Event? {
        if (isOffline()) {
            return localDataSource.getEvent(eventId, FETCH_ALL)
        }
        val localResult = localDataSource.getEvent(eventId, EVENT_CACHE_TTL)
        if (localResult == null) {
            val remoteResult = remoteDataSource.getEvent(eventId)
            if (remoteResult == null) {
                localDataSource.deleteEvent(eventId)
            } else {
                localDataSource.setEvent(remoteResult)
            }
            return remoteResult
        }
        return localResult
    }

    override suspend fun createEvent(event: Event): String {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("Event")
        }
        val newEventId = remoteDataSource.createEvent(event)
        localDataSource.setEvent(event.copy(eventId = newEventId))
        return newEventId
    }

    override suspend fun setEvent(event: Event) {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("Event")
        }
        remoteDataSource.setEvent(event)
        localDataSource.setEvent(event)
    }

    override suspend fun deleteEvent(event: Event) {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("Event")
        }
        remoteDataSource.deleteEvent(event)
        localDataSource.deleteEvent(event.eventId)
    }

    override suspend fun getAllEvents(): List<Event> {
        if (isOffline() || notExpired(events_last_cached_all, EVENT_CACHE_TTL)) {
            return localDataSource.getAllEvents(FETCH_ALL)
        }
        val nonExpired = localDataSource.getAllEvents(EVENT_CACHE_TTL)
        val nonExpiredIds = nonExpired.map { it.eventId }
        val remoteUpdate = remoteDataSource.getEventsNotIn(nonExpiredIds)
        localDataSource.deleteEventsNotIn(nonExpiredIds)
        localDataSource.setEvents(remoteUpdate)
        events_last_cached_all = currentTimeStamp()
        return nonExpired + remoteUpdate
    }

    override suspend fun joinEvent(userId: String, event: Event): Boolean {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("Event Join")
        }
        val remoteResult = remoteDataSource.joinEvent(userId, event.eventId)
        localDataSource.joinEvent(userId, event.eventId)
        forceRefetchEvent(event.eventId)
        return remoteResult
    }

    override suspend fun leaveEvent(userId: String, event: Event): Boolean {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("Event Join")
        }
        val remoteResult = remoteDataSource.leaveEvent(userId, event.eventId)
        localDataSource.leaveEvent(userId, event.eventId)
        forceRefetchEvent(event.eventId)
        return remoteResult
    }

    private suspend fun forceRefetchEvent(eventId: String) {
        val eventUpdate = remoteDataSource.getEvent(eventId)
        if (eventUpdate != null) {
            localDataSource.setEvent(eventUpdate)
        }
    }

    override suspend fun getJoinedEvents(userId: String): List<Event> {
        if (
            isOffline() ||
                notExpired(events_last_cached_joined.getOrDefault(userId, 0), EVENT_CACHE_TTL)
        ) {
            return localDataSource.getJoinedEvents(userId, FETCH_ALL)
        }
        val nonExpired = localDataSource.getJoinedEvents(userId, EVENT_CACHE_TTL)
        val nonExpiredIds = nonExpired.map { it.eventId }
        val remoteUpdate = remoteDataSource.getJoinedEventsNotIn(userId, nonExpiredIds)
        localDataSource.leaveEventsNotIn(userId, nonExpiredIds)
        localDataSource.joinEvents(userId, remoteUpdate.map { it.eventId })
        events_last_cached_joined.put(userId, currentTimeStamp())
        return nonExpired + remoteUpdate
    }

    override suspend fun getTag(tagId: String): Tag? {
        if (isOffline()) {
            return localDataSource.getTag(tagId, FETCH_ALL)
        }
        val localResult = localDataSource.getTag(tagId, TAG_CACHE_TTL)
        if (localResult == null) {
            val remoteResult = remoteDataSource.getTag(tagId)
            if (remoteResult != null) localDataSource.setTag(remoteResult)
            return remoteResult
        }
        return localResult
    }

    override suspend fun getSubTags(tagId: String): List<Tag> {
        if (
            isOffline() ||
                notExpired(tags_last_cached_all, TAG_CACHE_TTL) ||
                notExpired(tags_last_cached_subs.getOrDefault(tagId, 0), TAG_CACHE_TTL)
        ) {
            return localDataSource.getSubTags(tagId, FETCH_ALL)
        }
        val nonExpired = localDataSource.getSubTags(tagId, TAG_CACHE_TTL)
        val nonExpiredIds = nonExpired.map { it.tagId }
        val remoteUpdate = remoteDataSource.getSubTagsNotIn(tagId, nonExpiredIds)
        localDataSource.deleteSubTagsNotIn(tagId, nonExpiredIds)
        localDataSource.setTags(remoteUpdate)
        tags_last_cached_subs.put(tagId, currentTimeStamp())
        return nonExpired + remoteUpdate
    }

    override suspend fun getAllTags(): List<Tag> {
        if (isOffline() || notExpired(tags_last_cached_all, TAG_CACHE_TTL)) {
            return localDataSource.getAllTags(FETCH_ALL)
        }
        val nonExpired = localDataSource.getAllTags(TAG_CACHE_TTL)
        val nonExpiredIds = nonExpired.map { it.tagId }
        val remoteUpdate = remoteDataSource.getAllTagsNotIn(nonExpiredIds)
        localDataSource.deleteAllTagsNotIn(nonExpiredIds)
        localDataSource.setTags(remoteUpdate)
        tags_last_cached_all = currentTimeStamp()
        return nonExpired + remoteUpdate
    }

    override suspend fun getUserProfile(userId: String): UserProfile? {
        if (isOffline()) {
            return localDataSource.getUserProfile(userId, FETCH_ALL)
        }
        val localResult = localDataSource.getUserProfile(userId, USERPROFILE_CACHE_TTL)
        if (localResult == null) {
            val remoteResult = remoteDataSource.getUserProfile(userId)
            if (remoteResult == null) {
                localDataSource.deleteUserProfile(userId)
            } else {
                // make sure Associations linked in UserProfile are present locally
                val associationIdsLinkedInProfile =
                    (remoteResult.committeeMember + remoteResult.associationsSubscriptions).map {
                        it.associationId
                    }
                val locallyPresentAssociationIds =
                    localDataSource.getAssociationIds(
                        associationIdsLinkedInProfile,
                        ASSOCIATION_CACHE_TTL
                    )
                val remoteAssociationsResult =
                    remoteDataSource.getAssociations(
                        associationIdsLinkedInProfile - locallyPresentAssociationIds
                    )
                localDataSource.setAssociations(remoteAssociationsResult)

                localDataSource.setUserProfile(remoteResult)
            }
            return remoteResult
        }
        return localResult
    }

    override suspend fun setUserProfile(userProfile: UserProfile) {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("UserProfile")
        }
        remoteDataSource.setUserProfile(userProfile)
        localDataSource.setUserProfile(userProfile)
    }

    override suspend fun deleteUserProfile(userProfile: UserProfile) {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("UserProfile")
        }
        remoteDataSource.deleteUserProfile(userProfile)
        localDataSource.deleteUserProfile(userProfile.userId)
    }
}

class RepositoryStoreWhileNoInternetException(objectTryingToStore: String) :
    Exception(
        "Storing/Deleting a " + objectTryingToStore + " while the App is offline is not supported"
    )
