package com.github.swent.echo.data.repository

import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.FileCache
import com.github.swent.echo.data.repository.datasources.LocalDataSource
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import io.github.jan.supabase.exceptions.HttpRequestException
import java.time.ZonedDateTime

class RepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val networkService: NetworkService,
    private val fileCache: FileCache
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
            val remoteResult =
                try {
                    remoteDataSource.getAssociation(associationId)
                } catch (e: HttpRequestException) {
                    return localDataSource.getAssociation(associationId, FETCH_ALL)
                }
            if (remoteResult == null) {
                localDataSource.deleteAssociation(associationId)
            } else {
                localDataSource.setAssociation(remoteResult)
            }
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
            val remoteResult =
                try {
                    remoteDataSource.getAssociations(associationIdsNotInLocalResult)
                } catch (e: HttpRequestException) {
                    return localDataSource.getAssociations(associationIds, FETCH_ALL)
                }
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
        val remoteUpdate =
            try {
                remoteDataSource.getAssociationsNotIn(nonExpiredIds)
            } catch (e: HttpRequestException) {
                return localDataSource.getAllAssociations(FETCH_ALL)
            }
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
            val remoteResult =
                try {
                    remoteDataSource.getEvent(eventId)
                } catch (e: HttpRequestException) {
                    return localDataSource.getEvent(eventId, FETCH_ALL)
                }
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
        val newEventId =
            try {
                remoteDataSource.createEvent(event)
            } catch (e: HttpRequestException) {
                throw RepositoryStoreWhileNoInternetException("Event")
            }
        localDataSource.setEvent(event.copy(eventId = newEventId))
        return newEventId
    }

    override suspend fun setEvent(event: Event) {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("Event")
        }
        try {
            remoteDataSource.setEvent(event)
        } catch (e: HttpRequestException) {
            throw RepositoryStoreWhileNoInternetException("Event")
        }
        localDataSource.setEvent(event)
    }

    override suspend fun deleteEvent(event: Event) {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("Event")
        }
        try {
            remoteDataSource.deleteEvent(event)
        } catch (e: HttpRequestException) {
            throw RepositoryStoreWhileNoInternetException("Event")
        }
        localDataSource.deleteEvent(event.eventId)
    }

    override suspend fun getAllEvents(): List<Event> {
        if (isOffline() || notExpired(events_last_cached_all, EVENT_CACHE_TTL)) {
            return localDataSource.getAllEvents(FETCH_ALL)
        }
        val nonExpired = localDataSource.getAllEvents(EVENT_CACHE_TTL)
        val nonExpiredIds = nonExpired.map { it.eventId }
        val remoteUpdate =
            try {
                remoteDataSource.getEventsNotIn(nonExpiredIds)
            } catch (e: HttpRequestException) {
                return localDataSource.getAllEvents(FETCH_ALL)
            }
        localDataSource.deleteEventsNotIn(nonExpiredIds)
        localDataSource.setEvents(remoteUpdate)
        events_last_cached_all = currentTimeStamp()
        return nonExpired + remoteUpdate
    }

    override suspend fun joinEvent(userId: String, event: Event): Boolean {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("Event Join")
        }
        val remoteResult =
            try {
                remoteDataSource.joinEvent(userId, event.eventId)
            } catch (e: HttpRequestException) {
                throw RepositoryStoreWhileNoInternetException("Event Join")
            }
        localDataSource.joinEvent(userId, event.eventId)
        forceRefetchEvent(event.eventId, { x, y -> x + y })
        return remoteResult
    }

    override suspend fun leaveEvent(userId: String, event: Event): Boolean {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("Event Leave")
        }
        val remoteResult =
            try {
                remoteDataSource.leaveEvent(userId, event.eventId)
            } catch (e: HttpRequestException) {
                throw RepositoryStoreWhileNoInternetException("Event Leave")
            }
        localDataSource.leaveEvent(userId, event.eventId)
        forceRefetchEvent(event.eventId, { x, y -> x - y })
        return remoteResult
    }

    private suspend fun forceRefetchEvent(eventId: String, failoverAction: (Int, Int) -> Int) {
        val eventUpdate =
            try {
                remoteDataSource.getEvent(eventId, 10u)
            } catch (e: HttpRequestException) {
                val event = localDataSource.getEvent(eventId, FETCH_ALL)
                event?.copy(participantCount = failoverAction(event.participantCount, 1))
            }
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
        val remoteUpdate =
            try {
                remoteDataSource.getJoinedEventsNotIn(userId, nonExpiredIds)
            } catch (e: HttpRequestException) {
                return localDataSource.getJoinedEvents(userId, FETCH_ALL)
            }
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
            val remoteResult =
                try {
                    remoteDataSource.getTag(tagId)
                } catch (e: HttpRequestException) {
                    return localDataSource.getTag(tagId, FETCH_ALL)
                }
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
        val remoteUpdate =
            try {
                remoteDataSource.getSubTagsNotIn(tagId, nonExpiredIds)
            } catch (e: HttpRequestException) {
                return localDataSource.getSubTags(tagId, FETCH_ALL)
            }
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
        val remoteUpdate =
            try {
                remoteDataSource.getAllTagsNotIn(nonExpiredIds)
            } catch (e: HttpRequestException) {
                return localDataSource.getAllTags(FETCH_ALL)
            }
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
            val remoteResult =
                try {
                    remoteDataSource.getUserProfile(userId)
                } catch (e: HttpRequestException) {
                    return localDataSource.getUserProfile(userId, FETCH_ALL)
                }
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
                    try {
                        remoteDataSource.getAssociations(
                            associationIdsLinkedInProfile - locallyPresentAssociationIds
                        )
                    } catch (e: HttpRequestException) {
                        return localDataSource.getUserProfile(userId, FETCH_ALL)
                    }
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
        try {
            remoteDataSource.setUserProfile(userProfile)
        } catch (e: HttpRequestException) {
            throw RepositoryStoreWhileNoInternetException("UserProfile")
        }
        localDataSource.setUserProfile(userProfile)
    }

    override suspend fun deleteUserProfile(userProfile: UserProfile) {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("UserProfile")
        }
        try {
            remoteDataSource.deleteUserProfile(userProfile)
        } catch (e: HttpRequestException) {
            throw RepositoryStoreWhileNoInternetException("UserProfile")
        }
        localDataSource.deleteUserProfile(userProfile.userId)
    }

    override suspend fun getUserProfilePicture(userId: String): ByteArray? {
        val fileName = "$userId.jpeg"
        if (isOffline()) {
            return fileCache.get(fileName)
        }
        var picture = fileCache.get(fileName)
        if (picture == null) {
            picture =
                try {
                    remoteDataSource.getUserProfilePicture(userId)
                } catch (e: HttpRequestException) {
                    return null
                }
            if (picture != null) {
                fileCache.set(fileName, picture)
            }
        }
        return picture
    }

    override suspend fun setUserProfilePicture(userId: String, picture: ByteArray) {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("UserProfilePicture")
        }
        try {
            remoteDataSource.setUserProfilePicture(userId, picture)
        } catch (e: HttpRequestException) {
            throw RepositoryStoreWhileNoInternetException("UserProfilePicture")
        }
        fileCache.set("$userId.jpeg", picture)
    }

    override suspend fun deleteUserProfilePicture(userId: String) {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("UserProfilePicture")
        }
        try {
            remoteDataSource.deleteUserProfilePicture(userId)
        } catch (e: HttpRequestException) {
            throw RepositoryStoreWhileNoInternetException("UserProfilePicture")
        }
        fileCache.delete("$userId.jpeg")
    }
}

class RepositoryStoreWhileNoInternetException(objectTryingToStore: String) :
    Exception(
        "Storing/Deleting a " + objectTryingToStore + " while the App is offline is not supported"
    )
