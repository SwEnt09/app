package com.github.swent.echo.data.repository

import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.DataModel
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.FileCache
import com.github.swent.echo.data.repository.datasources.LocalDataSource
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import com.github.swent.echo.data.repository.datasources.RemoteDataSourceRequestMaxRetryExceededException
import java.time.ZonedDateTime

/**
 * Implementation of [Repository] that uses a [RemoteDataSource], a [LocalDataSource] as well as a
 * [FileCache].
 *
 * @param remoteDataSource The RemoteDataSource implementation used by the repository.
 * @param localDataSource The LocalDataSource implementation used by the repository for
 *   caching/synchronisation of structured data.
 * @param networkService The NetworkService implementation determining whether the device is
 *   connected to the internet.
 * @param fileCache The FileCache implementation used by the repository for caching files/media.
 */
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

        var associations_last_cached_all: MutableList<Long> = mutableListOf(0)
        var events_last_cached_all: MutableList<Long> = mutableListOf(0)
        var events_last_cached_joined: MutableMap<String, Long> = mutableMapOf()
        var tags_last_cached_all: MutableList<Long> = mutableListOf(0)
        var tags_last_cached_subs: MutableMap<String, Long> = mutableMapOf()
    }

    private suspend fun <T : DataModel> getObject(
        objectId: String,
        objectTTL: Long,
        getLocal: suspend (String, Long) -> T?,
        getRemote: suspend (String) -> T?,
        deleteLocal: suspend (String) -> Unit,
        setLocal: suspend (T) -> Unit
    ): T? {
        if (isOffline()) {
            return getLocal(objectId, FETCH_ALL)
        }
        val localResult = getLocal(objectId, objectTTL)
        if (localResult == null) {
            val remoteResult =
                try {
                    getRemote(objectId)
                } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
                    return getLocal(objectId, FETCH_ALL)
                }
            if (remoteResult == null) {
                deleteLocal(objectId)
            } else {
                setLocal(remoteResult)
            }
            return remoteResult
        }
        return localResult
    }

    private suspend fun <T : DataModel> getObjects(
        objectIds: List<String>,
        objectTTL: Long,
        getLocal: suspend (List<String>, Long) -> List<T>,
        getRemote: suspend (List<String>) -> List<T>,
        setLocal: suspend (List<T>) -> Unit
    ): List<T> {
        if (isOffline()) {
            return getLocal(objectIds, FETCH_ALL)
        }
        val localResult = getLocal(objectIds, objectTTL)
        val associationIdsNotInLocalResult = objectIds - localResult.map { it.getId() }
        if (associationIdsNotInLocalResult != emptyList<String>()) {
            val remoteResult =
                try {
                    getRemote(associationIdsNotInLocalResult)
                } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
                    return getLocal(objectIds, FETCH_ALL)
                }
            setLocal(remoteResult)
            return localResult + remoteResult
        }
        return localResult
    }

    private suspend fun <T : DataModel> getAllObjects(
        objectsLastCachedAll: MutableList<Long>,
        objectTTL: Long,
        getAllLocal: suspend (Long) -> List<T>,
        getAllNotInRemote: suspend (List<String>) -> List<T>,
        deleteAllNotInLocal: suspend (List<String>) -> Unit,
        setLocal: suspend (List<T>) -> Unit
    ): List<T> {
        if (isOffline() || notExpired(objectsLastCachedAll[0], objectTTL)) {
            return getAllLocal(FETCH_ALL)
        }
        val nonExpired = getAllLocal(objectTTL)
        val nonExpiredIds = nonExpired.map { it.getId() }
        val remoteUpdate =
            try {
                getAllNotInRemote(nonExpiredIds)
            } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
                return getAllLocal(FETCH_ALL)
            }
        deleteAllNotInLocal(nonExpiredIds)
        setLocal(remoteUpdate)
        objectsLastCachedAll[0] = currentTimeStamp()
        return nonExpired + remoteUpdate
    }

    private suspend fun <T : DataModel> setObject(
        obj: T,
        setLocal: suspend (T) -> Unit,
        setRemote: suspend (T) -> Unit,
        exceptionType: String
    ) {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException(exceptionType)
        }
        try {
            setRemote(obj)
        } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
            throw RepositoryStoreWhileNoInternetException(exceptionType)
        }
        setLocal(obj)
    }

    override suspend fun getAssociation(associationId: String): Association? =
        getObject(
            associationId,
            ASSOCIATION_CACHE_TTL,
            { id, ttl -> localDataSource.getAssociation(id, ttl) },
            { remoteDataSource.getAssociation(it) },
            { localDataSource.deleteAssociation(it) },
            { localDataSource.setAssociation(it) }
        )

    override suspend fun getAssociations(associationIds: List<String>): List<Association> =
        getObjects(
            associationIds,
            ASSOCIATION_CACHE_TTL,
            { ids, ttl -> localDataSource.getAssociations(ids, ttl) },
            { remoteDataSource.getAssociations(it) },
            { localDataSource.setAssociations(it) }
        )

    override suspend fun getAllAssociations(): List<Association> =
        getAllObjects(
            associations_last_cached_all,
            ASSOCIATION_CACHE_TTL,
            { localDataSource.getAllAssociations(it) },
            { remoteDataSource.getAssociationsNotIn(it) },
            { localDataSource.deleteAssociationsNotIn(it) },
            { localDataSource.setAssociations(it) }
        )

    override suspend fun getEvent(eventId: String): Event? =
        getObject(
            eventId,
            EVENT_CACHE_TTL,
            { id, ttl -> localDataSource.getEvent(id, ttl) },
            { remoteDataSource.getEvent(it) },
            { localDataSource.deleteEvent(it) },
            { localDataSource.setEvent(it) }
        )

    override suspend fun createEvent(event: Event): String {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("Event")
        }
        val newEventId =
            try {
                remoteDataSource.createEvent(event)
            } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
                throw RepositoryStoreWhileNoInternetException("Event")
            }
        newEventId ?: throw RepositoryStoreWhileNoInternetException("Event")
        localDataSource.setEvent(event.copy(eventId = newEventId))
        return newEventId
    }

    override suspend fun setEvent(event: Event) {
        setObject(
            event,
            { localDataSource.setEvent(it) },
            { remoteDataSource.setEvent(it) },
            "Event"
        )
    }

    override suspend fun deleteEvent(event: Event) {
        setObject(
            event,
            { localDataSource.deleteEvent(it.getId()) },
            { remoteDataSource.deleteEvent(it) },
            "Event"
        )
    }

    override suspend fun getAllEvents(): List<Event> =
        getAllObjects(
            events_last_cached_all,
            EVENT_CACHE_TTL,
            { localDataSource.getAllEvents(it) },
            { remoteDataSource.getEventsNotIn(it) },
            { localDataSource.deleteEventsNotIn(it) },
            { localDataSource.setEvents(it) }
        )

    override suspend fun joinEvent(userId: String, event: Event): Boolean {
        if (isOffline()) {
            throw RepositoryStoreWhileNoInternetException("Event Join")
        }
        val remoteResult =
            try {
                remoteDataSource.joinEvent(userId, event.eventId)
            } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
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
            } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
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
            } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
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
            } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
                return localDataSource.getJoinedEvents(userId, FETCH_ALL)
            }
        localDataSource.leaveEventsNotIn(userId, nonExpiredIds)
        localDataSource.joinEvents(userId, remoteUpdate.map { it.eventId })
        events_last_cached_joined.put(userId, currentTimeStamp())
        return nonExpired + remoteUpdate
    }

    override suspend fun getTag(tagId: String): Tag? =
        getObject(
            tagId,
            TAG_CACHE_TTL,
            { id, ttl -> localDataSource.getTag(id, ttl) },
            { remoteDataSource.getTag(it) },
            { localDataSource.deleteTag(it) },
            { localDataSource.setTag(it) }
        )

    override suspend fun getSubTags(tagId: String): List<Tag> {
        if (
            isOffline() ||
                notExpired(tags_last_cached_all[0], TAG_CACHE_TTL) ||
                notExpired(tags_last_cached_subs.getOrDefault(tagId, 0), TAG_CACHE_TTL)
        ) {
            return localDataSource.getSubTags(tagId, FETCH_ALL)
        }
        val nonExpired = localDataSource.getSubTags(tagId, TAG_CACHE_TTL)
        val nonExpiredIds = nonExpired.map { it.tagId }
        val remoteUpdate =
            try {
                remoteDataSource.getSubTagsNotIn(tagId, nonExpiredIds)
            } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
                return localDataSource.getSubTags(tagId, FETCH_ALL)
            }
        localDataSource.deleteSubTagsNotIn(tagId, nonExpiredIds)
        localDataSource.setTags(remoteUpdate)
        tags_last_cached_subs.put(tagId, currentTimeStamp())
        return nonExpired + remoteUpdate
    }

    override suspend fun getAllTags(): List<Tag> =
        getAllObjects(
            tags_last_cached_all,
            TAG_CACHE_TTL,
            { localDataSource.getAllTags(it) },
            { remoteDataSource.getAllTagsNotIn(it) },
            { localDataSource.deleteAllTagsNotIn(it) },
            { localDataSource.setTags(it) }
        )

    override suspend fun getUserProfile(userId: String): UserProfile? {
        if (isOffline()) {
            return localDataSource.getUserProfile(userId, FETCH_ALL)
        }
        val localResult = localDataSource.getUserProfile(userId, USERPROFILE_CACHE_TTL)
        if (localResult == null) {
            val remoteResult =
                try {
                    remoteDataSource.getUserProfile(userId)
                } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
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
                    } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
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
        setObject(
            userProfile,
            { localDataSource.setUserProfile(it) },
            { remoteDataSource.setUserProfile(it) },
            "UserProfile"
        )
    }

    override suspend fun deleteUserProfile(userProfile: UserProfile) {
        setObject(
            userProfile,
            { localDataSource.deleteUserProfile(it.getId()) },
            { remoteDataSource.deleteUserProfile(it) },
            "UserProfile"
        )
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
                } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
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
        } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
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
        } catch (e: RemoteDataSourceRequestMaxRetryExceededException) {
            throw RepositoryStoreWhileNoInternetException("UserProfilePicture")
        }
        fileCache.delete("$userId.jpeg")
    }
}
