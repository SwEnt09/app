package com.github.swent.echo.data.repository.datasources

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile

/** Server side, source of truth data source. */
interface RemoteDataSource {
    companion object {
        // default number of retries under failing network conditions
        const val RETRY_MAX: UInt = 3u
    }

    /**
     * Load a specific association.
     *
     * @param associationId the id of the association
     * @param maxRetriesCount the maximum number of retries to be done in the case of network
     *   failures
     * @return the requested Association object
     */
    suspend fun getAssociation(
        associationId: String,
        maxRetriesCount: UInt = RETRY_MAX
    ): Association?

    /**
     * Load a list of specified associations.
     *
     * @param associations a list of ids of the associations to fetch
     * @param maxRetriesCount the maximum number of retries to be done in the case of network
     *   failures
     * @return list of requested Associations
     */
    suspend fun getAssociations(
        associations: List<String>,
        maxRetriesCount: UInt = RETRY_MAX
    ): List<Association>

    /**
     * Get all the associations which are not in a given list of ids
     *
     * @param associationIds the ids to exclude from the fetch
     * @param maxRetriesCount the maximum number of retries to be done in the case of network
     *   failures
     * @return list of all Associations whose id was not in the provided list
     */
    suspend fun getAssociationsNotIn(
        associationIds: List<String>,
        maxRetriesCount: UInt = RETRY_MAX
    ): List<Association>

    /**
     * Get all associations.
     *
     * @param maxRetriesCount the maximum number of retries to be done in the case of network
     *   failures
     * @return list containing all associations
     */
    suspend fun getAllAssociations(maxRetriesCount: UInt = RETRY_MAX): List<Association>

    /**
     * Get a specific Event
     *
     * @param eventId the id of the event which is requested
     * @param maxRetriesCount the maximum number of retries to be done in the case of network
     *   failures
     * @return the requested Event object or null if it does not exist
     */
    suspend fun getEvent(eventId: String, maxRetriesCount: UInt = RETRY_MAX): Event?

    /**
     * Creates an event with the given `event` object.
     *
     * @param event The event to be created.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return The eventId string of the newly created event, or null if creation failed.
     */
    suspend fun createEvent(event: Event, maxRetriesCount: UInt = RETRY_MAX): String?

    /**
     * Sets the properties of an existing event with the given `event` object.
     *
     * @param event The updated event details.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     */
    suspend fun setEvent(event: Event, maxRetriesCount: UInt = RETRY_MAX)

    /**
     * Deletes an given event.
     *
     * @param event The event to be deleted.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     */
    suspend fun deleteEvent(event: Event, maxRetriesCount: UInt = RETRY_MAX)

    /**
     * Retrieves all events that do not have any of the specified `eventIds` as their ID.
     *
     * @param eventIds A list containing the IDs of events to exclude from results.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return A list of `Event` objects matching the query criteria.
     */
    suspend fun getEventsNotIn(
        eventIds: List<String>,
        maxRetriesCount: UInt = RETRY_MAX
    ): List<Event>

    /**
     * Retrieves a complete list of all available events.
     *
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return A list of `Event` objects representing all known events.
     */
    suspend fun getAllEvents(maxRetriesCount: UInt = RETRY_MAX): List<Event>

    /**
     * Joins an event for the user identified by `userId`, and associated with the event identified
     * by `eventId`.
     *
     * @param userId The unique identifier of the user joining the event.
     * @param eventId The unique identifier of the event being joined.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return True if successful; false otherwise.
     */
    suspend fun joinEvent(
        userId: String,
        eventId: String,
        maxRetriesCount: UInt = RETRY_MAX
    ): Boolean

    /**
     * Leaves (withdraws from participation in) an event for the user identified by `userId`, and
     * associated with the event identified by `eventId`.
     *
     * @param userId The unique identifier of the user leaving the event.
     * @param eventId The unique identifier of the event being left.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return True if successful; false otherwise.
     */
    suspend fun leaveEvent(
        userId: String,
        eventId: String,
        maxRetriesCount: UInt = RETRY_MAX
    ): Boolean

    /**
     * Fetches a list of events where the authenticated user is participating.
     *
     * @param userId The unique identifier of the user whose joined events should be retrieved.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return A list of `Event` objects corresponding to the events the user has joined.
     */
    suspend fun getJoinedEvents(userId: String, maxRetriesCount: UInt = RETRY_MAX): List<Event>

    /**
     * Returns a filtered list of events based on those that the authenticated user has joined but
     * which are not in the provided list.
     *
     * @param userId The unique identifier of the user whose joined events should be fetched.
     * @param eventIds A list containing the IDs of events to exclude from results.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return A list of `Event` objects corresponding to the events that meet the filtering
     *   criteria.
     */
    suspend fun getJoinedEventsNotIn(
        userId: String,
        eventIds: List<String>,
        maxRetriesCount: UInt = RETRY_MAX
    ): List<Event>

    /**
     * Gets a specific `Tag` instance by its `tagId`.
     *
     * @param tagId The unique identifier of the desired `Tag`.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return the requested tag; empty if no `Tag` exists with the requested id.
     */
    suspend fun getTag(tagId: String, maxRetriesCount: UInt = RETRY_MAX): Tag?

    /**
     * Obtains direct children tags under the parent node represented by the given `tagId`.
     *
     * @param tagId The unique identifier of the parent tag.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return A list of `Tag` instances directly under the input `tagId`.
     */
    suspend fun getSubTags(tagId: String, maxRetriesCount: UInt = RETRY_MAX): List<Tag>

    /**
     * Lists immediate subtags excluding the ones provided as a list of ids within the hierarchy of
     * a particular tag.
     *
     * @param tagId The unique identifier of the parent tag.
     * @param childTagIds A list containing the IDs of child tags to exclude from results.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return A list of `Tag` instances directly under the input `tagId`, minus those explicitly
     *   excluded.
     */
    suspend fun getSubTagsNotIn(
        tagId: String,
        childTagIds: List<String>,
        maxRetriesCount: UInt = RETRY_MAX
    ): List<Tag>

    /**
     * Gets all available tags.
     *
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return A list containing all availabe tags.
     */
    suspend fun getAllTags(maxRetriesCount: UInt = RETRY_MAX): List<Tag>

    /**
     * Retrieves a list of all tags except the ones specified in `tagIds`.
     *
     * @param tagIds A list of tag strings identifying which tags should NOT be included in the
     *   result set.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return A possibly empty list of `Tag` objects representing the tags excluding the ones
     *   passed in the parameter.
     */
    suspend fun getAllTagsNotIn(tagIds: List<String>, maxRetriesCount: UInt = RETRY_MAX): List<Tag>

    /**
     * Queries the user profile data associated with the given `userId`.
     *
     * @param userId The unique identifier of the target user profile.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return An optional instance of `UserProfile`, encapsulating the queried records or null if
     *   none was found.
     */
    suspend fun getUserProfile(userId: String, maxRetriesCount: UInt = RETRY_MAX): UserProfile?

    /**
     * Updates the provided user profile.
     *
     * @param userProfile Encapsulates the modified fields intended to update the respective user
     *   profile entry.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     */
    suspend fun setUserProfile(userProfile: UserProfile, maxRetriesCount: UInt = RETRY_MAX)

    /**
     * Removes the specified user profile
     *
     * @param userProfile to be deleted
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     */
    suspend fun deleteUserProfile(userProfile: UserProfile, maxRetriesCount: UInt = RETRY_MAX)

    /**
     * Serves as proxy to retrieve raw bytes composing the profile picture of the user.
     *
     * @param userId of the user for which to fetch the profile picture.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     * @return Decoded sequence of bitmaps comprising the users profile picture or null if unable to
     *   locate the resource.
     */
    suspend fun getUserProfilePicture(userId: String, maxRetriesCount: UInt = RETRY_MAX): ByteArray?

    /**
     * Set the profile picture for a user.
     *
     * @param userId for which to set the profile picture
     * @param picture to store.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     */
    suspend fun setUserProfilePicture(
        userId: String,
        picture: ByteArray,
        maxRetriesCount: UInt = RETRY_MAX
    )

    /**
     * Deletes a previously uploaded profile picture for the user.
     *
     * @param userId for which to delete the profile picture.
     * @param maxRetriesCount The maximum number of times to retry the operation if it fails.
     *   Defaults to `RETRY_MAX`.
     */
    suspend fun deleteUserProfilePicture(userId: String, maxRetriesCount: UInt = RETRY_MAX)
}

/** Exception type thrown if the operation still fails after the specified amount of retries. */
class RemoteDataSourceRequestMaxRetryExceededException :
    Exception("Network retries for RemoteDataSource operation exceeded.")
