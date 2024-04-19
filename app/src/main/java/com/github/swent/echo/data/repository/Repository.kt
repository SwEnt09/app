package com.github.swent.echo.data.repository

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile

interface Repository {
    suspend fun getAssociation(associationId: String): Association

    suspend fun getAllAssociations(): List<Association>

    suspend fun getEvent(eventId: String): Event

    suspend fun setEvent(event: Event)

    suspend fun getAllEvents(): List<Event>

    suspend fun getTag(tagId: String): Tag

    /**
     * Get all sub-tags of a given tag.
     *
     * @param tagId The id of the tag.
     * @return The list of sub-tags.
     */
    suspend fun getSubTags(tagId: String): List<Tag> {
        return emptyList()
    }

    suspend fun getAllTags(): List<Tag>

    suspend fun getUserProfile(userId: String): UserProfile

    suspend fun setUserProfile(userProfile: UserProfile)
}
