package com.github.swent.echo.data.repository.datasources

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile

interface RemoteDataSource {
    companion object {
        const val RETRY_MAX: UInt = 3u
    }

    suspend fun getAssociation(
        associationId: String,
        maxRetriesCount: UInt = RETRY_MAX
    ): Association?

    suspend fun getAssociations(
        associations: List<String>,
        maxRetriesCount: UInt = RETRY_MAX
    ): List<Association>

    suspend fun getAssociationsNotIn(
        associationIds: List<String>,
        maxRetriesCount: UInt = RETRY_MAX
    ): List<Association>

    suspend fun getAllAssociations(maxRetriesCount: UInt = RETRY_MAX): List<Association>

    suspend fun getEvent(eventId: String, maxRetriesCount: UInt = RETRY_MAX): Event?

    suspend fun createEvent(event: Event, maxRetriesCount: UInt = RETRY_MAX): String?

    suspend fun setEvent(event: Event, maxRetriesCount: UInt = RETRY_MAX)

    suspend fun deleteEvent(event: Event, maxRetriesCount: UInt = RETRY_MAX)

    suspend fun getEventsNotIn(
        eventIds: List<String>,
        maxRetriesCount: UInt = RETRY_MAX
    ): List<Event>

    suspend fun getAllEvents(maxRetriesCount: UInt = RETRY_MAX): List<Event>

    suspend fun joinEvent(
        userId: String,
        eventId: String,
        maxRetriesCount: UInt = RETRY_MAX
    ): Boolean

    suspend fun leaveEvent(
        userId: String,
        eventId: String,
        maxRetriesCount: UInt = RETRY_MAX
    ): Boolean

    suspend fun getJoinedEvents(userId: String, maxRetriesCount: UInt = RETRY_MAX): List<Event>

    suspend fun getJoinedEventsNotIn(
        userId: String,
        eventIds: List<String>,
        maxRetriesCount: UInt = RETRY_MAX
    ): List<Event>

    suspend fun getTag(tagId: String, maxRetriesCount: UInt = RETRY_MAX): Tag?

    suspend fun getSubTags(tagId: String, maxRetriesCount: UInt = RETRY_MAX): List<Tag>

    suspend fun getSubTagsNotIn(
        tagId: String,
        childTagIds: List<String>,
        maxRetriesCount: UInt = RETRY_MAX
    ): List<Tag>

    suspend fun getAllTags(maxRetriesCount: UInt = RETRY_MAX): List<Tag>

    suspend fun getAllTagsNotIn(tagIds: List<String>, maxRetriesCount: UInt = RETRY_MAX): List<Tag>

    suspend fun getUserProfile(userId: String, maxRetriesCount: UInt = RETRY_MAX): UserProfile?

    suspend fun setUserProfile(userProfile: UserProfile, maxRetriesCount: UInt = RETRY_MAX)

    suspend fun deleteUserProfile(userProfile: UserProfile, maxRetriesCount: UInt = RETRY_MAX)

    suspend fun getUserProfilePicture(userId: String, maxRetriesCount: UInt = RETRY_MAX): ByteArray?

    suspend fun setUserProfilePicture(
        userId: String,
        picture: ByteArray,
        maxRetriesCount: UInt = RETRY_MAX
    )

    suspend fun deleteUserProfilePicture(userId: String, maxRetriesCount: UInt = RETRY_MAX)
}

class RemoteDataSourceRequestMaxRetryExceededException :
    Exception("Network retries for RemoteDataSource operation exceeded.")
