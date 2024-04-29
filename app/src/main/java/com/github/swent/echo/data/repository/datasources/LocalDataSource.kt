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
 * certain amount of seconds. By default it's set to [Long.MAX_VALUE] which means that all the data
 * will be returned. The timestamp of the last sync is updated at every set operation.
 */
interface LocalDataSource {

    suspend fun getAssociation(
        associationId: String,
        syncedSecondsAgo: Long = Long.MAX_VALUE,
    ): Association?

    suspend fun setAssociation(association: Association)

    suspend fun getAllAssociations(syncedSecondsAgo: Long): List<Association>

    /**
     * Deletes all the associations that have been synced before a certain amount of seconds.
     *
     * @param syncedSecondsAgo The amount of seconds before which the associations should be
     *   deleted.
     */
    suspend fun deleteAssociations(syncedSecondsAgo: Long) {
        throw NotImplementedError("This method is not implemented.")
    }

    suspend fun setAssociations(associations: List<Association>)

    suspend fun getEvent(eventId: String, syncedSecondsAgo: Long): Event?

    suspend fun setEvent(event: Event)

    suspend fun getAllEvents(syncedSecondsAgo: Long): List<Event>

    suspend fun setEvents(events: List<Event>)

    /**
     * Deletes all the events that have been synced before a certain amount of seconds.
     *
     * @param syncedSecondsAgo The amount of seconds before which the events should be deleted.
     */
    suspend fun deleteEvents(syncedSecondsAgo: Long) {
        throw NotImplementedError("This method is not implemented.")
    }

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

    suspend fun setTags(tags: List<Tag>)

    /**
     * Deletes all the tags that have been synced before a certain amount of seconds.
     *
     * @param syncedSecondsAgo The amount of seconds before which the tags should be deleted.
     */
    suspend fun deleteTags(syncedSecondsAgo: Long) {
        throw NotImplementedError("This method is not implemented.")
    }

    suspend fun getUserProfile(
        userId: String,
        syncedSecondsAgo: Long,
    ): UserProfile?

    suspend fun setUserProfile(userProfile: UserProfile)

    /**
     * Deletes all the user profiles that have been synced before a certain amount of seconds.
     *
     * @param syncedSecondsAgo The amount of seconds before which the user profiles should be
     *   deleted.
     */
    suspend fun deleteUserProfiles(syncedSecondsAgo: Long) {
        throw NotImplementedError("This method is not implemented.")
    }
}
