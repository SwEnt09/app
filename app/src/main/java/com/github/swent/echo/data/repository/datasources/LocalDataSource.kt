package com.github.swent.echo.data.repository.datasources

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile

/**
 * The local data source.
 *
 * Note that all the getters return null when the data is not found locally.
 */
interface LocalDataSource {

    /** Initialize the local data source. */
    suspend fun initialize()

    suspend fun getAssociation(associationId: String): Association?

    suspend fun setAssociation(association: Association)

    suspend fun getAllAssociations(): List<Association>

    suspend fun setAssociations(associations: List<Association>)

    suspend fun getEvent(eventId: String): Event?

    suspend fun setEvent(event: Event)

    suspend fun getAllEvents(): List<Event>

    suspend fun setEvents(events: List<Event>)

    suspend fun getTag(tagId: String): Tag?

    suspend fun setTag(tag: Tag)

    /**
     * Get all sub-tags of a given tag.
     *
     * @param tagId The id of the tag.
     * @return The list of sub-tags.
     */
    suspend fun getSubTags(tagId: String): List<Tag>

    suspend fun getAllTags(): List<Tag>

    suspend fun setTags(tags: List<Tag>)

    suspend fun getUserProfile(userId: String): UserProfile?

    suspend fun setUserProfile(userProfile: UserProfile)
}
