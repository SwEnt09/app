package com.github.swent.echo.data.repository

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.LocalDataSource
import com.github.swent.echo.data.repository.datasources.RemoteDataSource

class RepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val isOnline: () -> Boolean
) : Repository {

    companion object {
        const val ASSOCIATION_CACHE_TTL: Long = 60 * 60
        const val EVENT_CACHE_TTL: Long = 60 * 5
        const val TAG_CACHE_TTL: Long = 60 * 60 * 24
        const val USERPROFILE_CACHE_TTL: Long = 60 * 15
        const val FETCH_ALL = Long.MAX_VALUE
    }

    override suspend fun getAssociation(associationId: String): Association? {
        if (isOnline()) {
            val localResult = localDataSource.getAssociation(associationId, ASSOCIATION_CACHE_TTL)
            if (localResult == null) {
                val remoteResult = remoteDataSource.getAssociation(associationId)
                localDataSource.setAssociation(remoteResult)
                return remoteResult
            }
            return localResult
        } else {
            return localDataSource.getAssociation(associationId, FETCH_ALL)
        }
    }

    override suspend fun getAllAssociations(): List<Association> {
        if (isOnline()) {
            val expired = localDataSource.getAllAssociationsSyncedBefore(ASSOCIATION_CACHE_TTL)
            if (expired.isNotEmpty()) {

                val remoteUpdate = remoteDataSource.getAssociations(expired)
                localDataSource.setAssociations(remoteUpdate)
            }
            return localDataSource.getAllAssociations(FETCH_ALL)
        } else {
            return localDataSource.getAllAssociations(FETCH_ALL)
        }
    }

    override suspend fun getEvent(eventId: String): Event? {
        if (isOnline()) {
            val localResult = localDataSource.getEvent(eventId, EVENT_CACHE_TTL)
            if (localResult == null) {
                val remoteResult = remoteDataSource.getEvent(eventId)
                localDataSource.setEvent(remoteResult)
                return remoteResult
            }
            return localResult
        } else {
            return localDataSource.getEvent(eventId, FETCH_ALL)
        }
    }

    override suspend fun createEvent(event: Event): String {
        if (isOnline()) {
            val newEventId = remoteDataSource.createEvent(event)
            localDataSource.setEvent(event.copy(eventId = newEventId))
            return newEventId
        } else {
            throw RepositoryStoreWhileNoInternetException("Event")
        }
    }

    override suspend fun setEvent(event: Event) {
        if (isOnline()) {
            remoteDataSource.setEvent(event)
            localDataSource.setEvent(event)
        } else {
            throw RepositoryStoreWhileNoInternetException("Event")
        }
    }

    override suspend fun getAllEvents(): List<Event> {
        if (isOnline()) {
            val events = remoteDataSource.getAllEvents()
            localDataSource.setEvents(events)
            return events
        } else {
            return localDataSource.getAllEvents()
        }
    }

    override suspend fun joinEvent(userId: String, event: Event): Boolean {
        return remoteDataSource.joinEvent(userId, event.eventId)
    }

    override suspend fun leaveEvent(userId: String, event: Event): Boolean {
        return remoteDataSource.leaveEvent(userId, event.eventId)
    }

    override suspend fun getJoinedEvents(userId: String): List<Event> {
        return remoteDataSource.getJoinedEvents(userId)
    }

    override suspend fun getTag(tagId: String): Tag? {
        if (isOnline()) {
            val tag = remoteDataSource.getTag(tagId)
            localDataSource.setTag(tag)
            return tag
        } else {
            return localDataSource.getTag(tagId)
        }
    }

    override suspend fun getSubTags(tagId: String): List<Tag> {
        return remoteDataSource.getSubTags(tagId)
    }

    override suspend fun getAllTags(): List<Tag> {
        if (isOnline()) {
            val tags = remoteDataSource.getAllTags()
            localDataSource.setTags(tags)
            return tags
        } else {
            return localDataSource.getAllTags()
        }
    }

    override suspend fun getUserProfile(userId: String): UserProfile? {
        if (isOnline()) {
            try {
                val userProfile = remoteDataSource.getUserProfile(userId)
                localDataSource.setUserProfile(userProfile)
                return userProfile
            } catch (e: Exception) {
                return null
            }
        } else {
            return localDataSource.getUserProfile(userId)
        }
    }

    override suspend fun setUserProfile(userProfile: UserProfile) {
        if (isOnline()) {
            remoteDataSource.setUserProfile(userProfile)
            localDataSource.setUserProfile(userProfile)
        } else {
            throw RepositoryStoreWhileNoInternetException("UserProfile")
        }
    }
}

class RepositoryStoreWhileNoInternetException(objectTryingToStore: String) :
    Exception("Storing a " + objectTryingToStore + " while the App is offline is not supported")
