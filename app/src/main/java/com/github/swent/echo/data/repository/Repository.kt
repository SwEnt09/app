package com.github.swent.echo.data.repository

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile

interface Repository {

    companion object {
        /** The root tag of the tag tree. Note the sub-tags of this tag are the top-level tags. */
        const val ROOT_TAG_ID = "1d253a7e-eb8c-4546-bc98-1d3adadcffe8"
    }

    /**
     * Gets the association info details. If the association does not exist, returns null.
     *
     * @param associationId The id of the association.
     * @return The association with the given associationId or null if the association does not
     *   exists.
     */
    suspend fun getAssociation(associationId: String): Association?

    suspend fun getAllAssociations(): List<Association>

    /**
     * Gets the event info details. If the event does not exist, returns null.
     *
     * @param eventId The id of the event.
     * @return The event with the given eventId or null if the event does not exists.
     */
    suspend fun getEvent(eventId: String): Event?

    /**
     * Creates a new event. The [Event.eventId] will be generated by the server and returned.
     *
     * @param event The id of the created event.
     */
    suspend fun createEvent(event: Event): String

    suspend fun setEvent(event: Event)

    suspend fun getAllEvents(): List<Event>

    suspend fun joinEvent(userId: String, event: Event): Boolean

    suspend fun leaveEvent(userId: String, event: Event): Boolean

    suspend fun getJoinedEvents(userId: String): List<Event>

    /**
     * Gets the tag info details. If the tag does not exist, returns null.
     *
     * @param tagId The id of the tag.
     * @return The tag of the tagId or null if the tag does not exists.
     */
    suspend fun getTag(tagId: String): Tag?

    /**
     * Get all sub-tags of a given tag.
     *
     * @param tagId The id of the tag.
     * @return The list of sub-tags.
     */
    suspend fun getSubTags(tagId: String): List<Tag>

    suspend fun getAllTags(): List<Tag>

    /**
     * Gets the profile of a user. If the user does not exist, returns null.
     *
     * @param userId The id of the user.
     * @return The profile of the user or null if the user has no profile associated.
     */
    suspend fun getUserProfile(userId: String): UserProfile?

    suspend fun setUserProfile(userProfile: UserProfile)
}
