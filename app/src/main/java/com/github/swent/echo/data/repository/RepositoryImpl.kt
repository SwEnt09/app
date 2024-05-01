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

    override suspend fun getAssociation(associationId: String): Association? {
        if (isOnline()) {
            val association = remoteDataSource.getAssociation(associationId)
            localDataSource.setAssociation(association)
            return association
        } else {
            return localDataSource.getAssociation(associationId)
        }
    }

    override suspend fun getAllAssociations(): List<Association> {
        if (isOnline()) {
            val associations = remoteDataSource.getAllAssociations()
            localDataSource.setAssociations(associations)
            return associations
        } else {
            return localDataSource.getAllAssociations()
        }
    }

    override suspend fun getEvent(eventId: String): Event? {
        if (isOnline()) {
            val event = remoteDataSource.getEvent(eventId)
            localDataSource.setEvent(event)
            return event
        } else {
            return localDataSource.getEvent(eventId)
        }
    }

    override suspend fun createEvent(event: Event): String {
        return remoteDataSource.createEvent(event)
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
