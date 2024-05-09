package com.github.swent.echo.data.repository.datasources

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile

/**
 * The local data source.
 *
 * Note that all the getters return null when the data is not found locally.
 *
 * The `syncedSecondsAgo` parameter is used to filter out the data that has been synced before a
 * certain amount of seconds.
 */
interface LocalDataSource {

    suspend fun getAssociation(
        associationId: String,
        syncedSecondsAgo: Long,
    ): Association?

    suspend fun setAssociation(association: Association)

    suspend fun getAllAssociations(syncedSecondsAgo: Long): List<Association>

    suspend fun getAllAssociationsSyncedBefore(secondsAgo: Long): List<String>

    suspend fun setAssociations(associations: List<Association>)

    suspend fun getEvent(eventId: String, syncedSecondsAgo: Long): Event?

    suspend fun setEvent(event: Event)

    suspend fun getAllEvents(syncedSecondsAgo: Long): List<Event>

    suspend fun getAllEventsSyncedBefore(secondsAgo: Long): List<String>

    suspend fun setEvents(events: List<Event>)

    suspend fun getTag(tagId: String, syncedSecondsAgo: Long): Tag?

    suspend fun setTag(tag: Tag)

    /**
     * Get all sub-tags of a given tag.
     *
     * @param tagId The id of the tag.
     * @return The list of sub-tags.
     */
    suspend fun getSubTags(tagId: String, syncedSecondsAgo: Long): List<Tag>

    suspend fun getAllTags(syncedSecondsAgo: Long): List<Tag>

    suspend fun getAllTagsSyncedBefore(secondsAgo: Long): List<String>

    suspend fun setTags(tags: List<Tag>)

    suspend fun getUserProfile(
        userId: String,
        syncedSecondsAgo: Long,
    ): UserProfile?

    suspend fun setUserProfile(userProfile: UserProfile)

    suspend fun getAllUserProfilesSyncedBefore(secondsAgo: Long): List<String>

    suspend fun joinEvent(userId: String, eventId: String)

    suspend fun leaveEvent(userId: String, eventId: String)

    suspend fun getJoinedEvents(userId: String): List<Event>

    suspend fun joinAssociation(userId: String, associationId: String)

    suspend fun leaveAssociation(userId: String, associationId: String)
}
