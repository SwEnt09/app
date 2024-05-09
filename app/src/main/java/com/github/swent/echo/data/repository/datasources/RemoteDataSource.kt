package com.github.swent.echo.data.repository.datasources

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile

interface RemoteDataSource {
    suspend fun getAssociation(associationId: String): Association?

    suspend fun getAssociations(associations: List<String>): List<Association>

    suspend fun getAllAssociations(): List<Association>

    suspend fun getEvent(eventId: String): Event?

    suspend fun createEvent(event: Event): String

    suspend fun setEvent(event: Event)

    suspend fun getAllEvents(): List<Event>

    suspend fun joinEvent(userId: String, eventId: String): Boolean

    suspend fun leaveEvent(userId: String, eventId: String): Boolean

    suspend fun getJoinedEvents(userId: String): List<Event>

    suspend fun getTag(tagId: String): Tag?

    suspend fun getSubTags(tagId: String): List<Tag>

    suspend fun getAllTags(): List<Tag>

    suspend fun getUserProfile(userId: String): UserProfile?

    suspend fun setUserProfile(userProfile: UserProfile)
}
