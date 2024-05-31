package com.github.swent.echo.data.repository

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile

/**
 * The repository is the single point of access for all data consumed by the app. It communicates
 * with the RemoteDataSource and manages synchronisation/caching with the LocalDataSource.
 */
interface Repository {

    /**
     * Gets the association info details. If the association does not exist, returns null.
     *
     * @param associationId The id of the association.
     * @return The association with the given associationId or null if the association does not
     *   exists.
     */
    suspend fun getAssociation(associationId: String): Association?

    /**
     * Gets the associations info details. If the associations do not exist, returns an emptyList().
     *
     * @param associationIds The ids of the associations to query.
     * @return The Associations with the given associationIds or emptyList() if the associations do
     *   not exist
     */
    suspend fun getAssociations(associationIds: List<String>): List<Association>

    /**
     * Gets all associations info details.
     *
     * @return The Associations or emptyList() if no association exists
     */
    suspend fun getAllAssociations(): List<Association>

    /**
     * Gets the event info details. If the event does not exist, returns null.
     *
     * @param eventId The id of the event.
     * @return The event with the given eventId or null if the event does not exists.
     */
    suspend fun getEvent(eventId: String): Event?

    /**
     * Creates a new event. The [Event.eventId] will be generated by the server and returned. The
     * creatorId of the event needs to correspond to the userId of the currently authenticated user.
     * If set on the event, the organizerId needs to be a valid associationId the creatorId is a
     * committeeMember of.
     *
     * @param event The event to save.
     * @return eventId The id of the created event.
     * @throws RepositoryStoreWhileNoInternetException in case of no internet or a network issue.
     */
    suspend fun createEvent(event: Event): String

    /**
     * Sets the attributes of an existing event. The [Event.eventId] will be generated by the server
     * and returned. The creatorId of the event needs to correspond to the userId of the currently
     * authenticated user. If set on the event, the organizerId needs to be a valid associationId
     * the creatorId is a committeeMember of.
     *
     * @param event The event to be updated.
     * @throws RepositoryStoreWhileNoInternetException in case of no internet or a network issue.
     */
    suspend fun setEvent(event: Event)

    /**
     * Deletes an event. The creatorId of the event needs to correspond to the userId of the
     * currently authenticated user.
     *
     * @param event The event to be deleted.
     * @throws RepositoryStoreWhileNoInternetException in case of no internet or a network issue.
     */
    suspend fun deleteEvent(event: Event)

    /**
     * Gets all event info details.
     *
     * @return The Events or emptyList() if no event exists.
     */
    suspend fun getAllEvents(): List<Event>

    /**
     * Registers a user as a participant on a given event. The userId needs to correspond to the
     * currently authenticated user.
     *
     * @param userId of the user to register as participant.
     * @param event the event the user wants to participate in.
     * @return Boolean whether the user has been successfully subscribed.
     * @throws RepositoryStoreWhileNoInternetException in case of no internet or a network issue.
     */
    suspend fun joinEvent(userId: String, event: Event): Boolean

    /**
     * Registers a user as not being a participant on a given event anymore. The userId needs to
     * correspond to the currently authenticated user.
     *
     * @param userId of the user to unregister as participant.
     * @param event the event the user wants not to participate in anymore.
     * @return Boolean whether the user has been successfully unsubscribed.
     * @throws RepositoryStoreWhileNoInternetException in case of no internet or a network issue.
     */
    suspend fun leaveEvent(userId: String, event: Event): Boolean

    /**
     * Get all events a given user is registered as a participant of. The userId needs to correspond
     * to the currently authenticated user.
     *
     * @param userId the user for which to query joined events
     * @return List of joined Events
     */
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

    /**
     * Get all tags.
     *
     * @return All the existing tags.
     */
    suspend fun getAllTags(): List<Tag>

    /**
     * Gets the profile of a user. If the user does not exist, returns null.
     *
     * @param userId The id of the user.
     * @return The profile of the user or null if the user has no profile associated.
     */
    suspend fun getUserProfile(userId: String): UserProfile?

    /**
     * Changes or creates the profile for a given user. The [userProfile.userId] needs to correspond
     * to the authenticated user.
     *
     * @param userProfile the modified profile of the user
     * @throws RepositoryStoreWhileNoInternetException if there is no internet or a network issue
     */
    suspend fun setUserProfile(userProfile: UserProfile)

    /**
     * Deletes the profile for a given user. The [userProfile.userId] needs to correspond to the
     * authenticated user.
     *
     * @param userProfile the modified profile of the user
     * @throws RepositoryStoreWhileNoInternetException if there is no internet or a network issue
     */
    suspend fun deleteUserProfile(userProfile: UserProfile)

    /**
     * Get the picture of a user's profile, retruns null if none exist.
     *
     * @param userId the id of the user
     * @return a picture as a file.
     */
    suspend fun getUserProfilePicture(userId: String): ByteArray?

    /**
     * Set the picture of a user's profile. The userId needs to correspond to the currently
     * authenticated user.
     *
     * @param userId, the id of the user for which to set the profile picture
     * @param picture a ByteArray of the data of the image.
     * @throws RepositoryStoreWhileNoInternetException in case of no internet or a network issue
     */
    suspend fun setUserProfilePicture(userId: String, picture: ByteArray)

    /**
     * Delete the picture of a user's profile. The userId needs to correspond to the currently
     * authenticated user.
     *
     * @param userId, the id of the user for which to set the profile picture
     * @throws RepositoryStoreWhileNoInternetException in case of no internet or a network issue
     */
    suspend fun deleteUserProfilePicture(userId: String)
}

/**
 * Exception class in case a store operation fails due to network conditions.
 *
 * @param objectTryingToStore The object type that has been tried to be stored.
 */
class RepositoryStoreWhileNoInternetException(objectTryingToStore: String) :
    Exception(
        "Storing/Deleting a " + objectTryingToStore + " while the App is offline is not supported"
    )
