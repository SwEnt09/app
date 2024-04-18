package com.github.swent.echo.data.repository

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.RemoteDataSource

class RepositoryImpl(private val remoteDataSource: RemoteDataSource) : Repository {
    override suspend fun getAssociation(associationId: String): Association {
        return remoteDataSource.getAssociation(associationId)
    }

    override suspend fun getAllAssociations(): List<Association> {
        return remoteDataSource.getAllAssociations()
    }

    override suspend fun getEvent(eventId: String): Event {
        return remoteDataSource.getEvent(eventId)
    }

    override suspend fun setEvent(event: Event) {
        return remoteDataSource.setEvent(event)
    }

    override suspend fun getAllEvents(): List<Event> {
        return remoteDataSource.getAllEvents()
    }

    override suspend fun getTag(tagId: String): Tag {
        return remoteDataSource.getTag(tagId)
    }

    override suspend fun getAllTags(): List<Tag> {
        return remoteDataSource.getAllTags()
    }

    override suspend fun getUserProfile(userId: String): UserProfile {
        return remoteDataSource.getUserProfile(userId)
    }

    override suspend fun setUserProfile(userProfile: UserProfile) {
        return remoteDataSource.setUserProfile(userProfile)
    }
}
