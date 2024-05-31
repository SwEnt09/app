package com.github.swent.echo.data.room

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.LocalDataSource
import com.github.swent.echo.data.room.entity.AssociationRoom
import com.github.swent.echo.data.room.entity.AssociationTagCrossRef
import com.github.swent.echo.data.room.entity.EventRoom
import com.github.swent.echo.data.room.entity.EventTagCrossRef
import com.github.swent.echo.data.room.entity.JoinedEventRoom
import com.github.swent.echo.data.room.entity.TagRoom
import com.github.swent.echo.data.room.entity.UserProfileAssociationSubscriptionCrossRef
import com.github.swent.echo.data.room.entity.UserProfileCommitteeMemberCrossRef
import com.github.swent.echo.data.room.entity.UserProfileRoom
import com.github.swent.echo.data.room.entity.UserProfileTagCrossRef
import com.github.swent.echo.data.room.entity.toAssociations
import com.github.swent.echo.data.room.entity.toTagList
import com.github.swent.echo.data.room.entity.toTagRoomList
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomLocalDataSource @Inject constructor(db: AppDatabase) : LocalDataSource {

    private val associationDao = db.associationDao()
    private val eventDao = db.eventDao()
    private val tagDao = db.tagDao()
    private val userProfileDao = db.userProfileDao()

    private fun computeTimestamp(secondsAgo: Long): Long {
        return ZonedDateTime.now().minusSeconds(secondsAgo).toEpochSecond()
    }

    override suspend fun getAssociation(
        associationId: String,
        syncedSecondsAgo: Long,
    ): Association? {
        val after = computeTimestamp(syncedSecondsAgo)
        return associationDao.get(associationId, after)?.toAssociation()
    }

    override suspend fun setAssociation(association: Association) {
        val crossRefs =
            association.relatedTags.map {
                AssociationTagCrossRef(association.associationId, it.tagId)
            }

        associationDao.deleteAllAssociationTagCrossRefsForAssociation(association.associationId)
        tagDao.insertAll(association.relatedTags.toTagRoomList())
        associationDao.insert(AssociationRoom(association))
        associationDao.insertAssociationTagCrossRefs(crossRefs)
    }

    override suspend fun deleteAssociation(associationId: String) {
        associationDao.delete(associationId)
    }

    override suspend fun getAssociations(
        associationIds: List<String>,
        syncedSecondsAgo: Long
    ): List<Association> {
        val after = computeTimestamp(syncedSecondsAgo)
        return associationDao.get(associationIds, after).toAssociations()
    }

    override suspend fun getAllAssociations(syncedSecondsAgo: Long): List<Association> {
        val after = computeTimestamp(syncedSecondsAgo)
        return associationDao.getAll(after).map { it.toAssociation() }
    }

    override suspend fun getAssociationIds(
        associationIds: List<String>,
        syncedSecondsAgo: Long
    ): List<String> {
        return associationDao.getIdsFrom(associationIds, computeTimestamp(syncedSecondsAgo))
    }

    override suspend fun getAllAssociationIds(secondsAgo: Long): List<String> {
        return associationDao.getAllIds(computeTimestamp(secondsAgo))
    }

    override suspend fun setAssociations(associations: List<Association>) {
        val tags = associations.flatMap { it.relatedTags }.toSet()
        val crossRefs =
            associations.flatMap { association ->
                association.relatedTags.map {
                    AssociationTagCrossRef(association.associationId, it.tagId)
                }
            }

        associationDao.deleteAllAssociationTagCrossRefsForAssociations(
            associations.map { it.associationId }
        )
        tagDao.insertAll(tags.toTagRoomList())
        associationDao.insertAll(associations.map { AssociationRoom(it) })
        associationDao.insertAssociationTagCrossRefs(crossRefs)
    }

    override suspend fun deleteAssociationsNotIn(associationIds: List<String>) {
        associationDao.deleteNotIn(associationIds)
    }

    override suspend fun getEvent(eventId: String, syncedSecondsAgo: Long): Event? {
        val after = computeTimestamp(syncedSecondsAgo)
        return eventDao.get(eventId, after)?.toEvent()
    }

    override suspend fun setEvent(event: Event) {
        val crossRefs = event.tags.map { EventTagCrossRef(event.eventId, it.tagId) }

        eventDao.deleteAllEventTagCrossRefsForEvent(event.eventId)
        tagDao.insertAll(event.tags.toTagRoomList())
        eventDao.insert(EventRoom(event))
        eventDao.insertEventTagCrossRefs(crossRefs)
    }

    override suspend fun deleteEvent(eventId: String) {
        eventDao.delete(eventId)
    }

    override suspend fun getAllEvents(syncedSecondsAgo: Long): List<Event> {
        val after = computeTimestamp(syncedSecondsAgo)
        return eventDao.getAll(after).map { it.toEvent() }
    }

    override suspend fun getAllEventIds(secondsAgo: Long): List<String> {
        return eventDao.getAllIds(computeTimestamp(secondsAgo))
    }

    override suspend fun setEvents(events: List<Event>) {
        val tags = events.flatMap { it.tags }.toSet()
        val crossRefs =
            events.flatMap { event -> event.tags.map { EventTagCrossRef(event.eventId, it.tagId) } }

        eventDao.deleteAllEventTagCrossRefsForEvents(events.map { it.eventId })
        tagDao.insertAll(tags.toTagRoomList())
        eventDao.insertAll(events.map { EventRoom(it) })
        eventDao.insertEventTagCrossRefs(crossRefs)
    }

    override suspend fun deleteEventsNotIn(eventIds: List<String>) {
        return eventDao.deleteNotIn(eventIds)
    }

    override suspend fun getTag(tagId: String, syncedSecondsAgo: Long): Tag? {
        val after = computeTimestamp(syncedSecondsAgo)
        return tagDao.get(tagId, after)?.toTag()
    }

    override suspend fun setTag(tag: Tag) {
        tagDao.insert(TagRoom(tag))
    }

    override suspend fun deleteTag(tagId: String) {
        tagDao.delete(tagId)
    }

    override suspend fun getSubTags(tagId: String, syncedSecondsAgo: Long): List<Tag> {
        val after = computeTimestamp(syncedSecondsAgo)
        return tagDao.getSubTags(tagId, after).toTagList()
    }

    override suspend fun deleteSubTagsNotIn(tagId: String, childTagIds: List<String>) {
        tagDao.deleteSubTagsNotIn(tagId, childTagIds)
    }

    override suspend fun getAllTags(syncedSecondsAgo: Long): List<Tag> {
        val after = computeTimestamp(syncedSecondsAgo)
        return tagDao.getAll(after).map { it.toTag() }
    }

    override suspend fun getAllTagIds(secondsAgo: Long): List<String> {
        return tagDao.getAllIds(computeTimestamp(secondsAgo))
    }

    override suspend fun deleteAllTagsNotIn(tagIds: List<String>) {
        tagDao.deleteNotIn(tagIds)
    }

    override suspend fun setTags(tags: List<Tag>) {
        tagDao.insertAll(tags.map { TagRoom(it) })
    }

    override suspend fun getUserProfile(userId: String, syncedSecondsAgo: Long): UserProfile? {
        val after = computeTimestamp(syncedSecondsAgo)
        return userProfileDao.get(userId, after)?.toUserProfile()
    }

    override suspend fun setUserProfile(userProfile: UserProfile) {
        val tags = userProfile.tags.toTagRoomList()
        val tagCrossRefs = tags.map { UserProfileTagCrossRef(userProfile.userId, it.tagId) }

        val committeeMemberCrossRefs =
            userProfile.committeeMember.map {
                UserProfileCommitteeMemberCrossRef(userProfile.userId, it.associationId)
            }

        val associationSubscriptionCrossRefs =
            userProfile.associationsSubscriptions.map {
                UserProfileAssociationSubscriptionCrossRef(userProfile.userId, it.associationId)
            }

        userProfileDao.deleteAllUserProfileTagCrossRefsForUser(userProfile.userId)
        userProfileDao.deleteAllUserProfileCommitteeMemberCrossRefsForUser(userProfile.userId)
        userProfileDao.deleteAllUserProfileAssociationSubscriptionCrossRefsForUser(
            userProfile.userId
        )
        tagDao.insertAll(tags)
        userProfileDao.insert(UserProfileRoom(userProfile))

        userProfileDao.insertUserProfileTagCrossRefs(tagCrossRefs)
        userProfileDao.insertUserProfileCommitteeMemberCrossRefs(committeeMemberCrossRefs)
        userProfileDao.insertUserProfileAssociationSubscriptionCrossRefs(
            associationSubscriptionCrossRefs
        )
    }

    override suspend fun deleteUserProfile(userId: String) {
        userProfileDao.delete(userId)
    }

    override suspend fun joinEvent(
        userId: String,
        eventId: String,
    ) {
        eventDao.insertJoinedEvent(
            JoinedEventRoom(
                userId,
                eventId,
            ),
        )
    }

    override suspend fun joinEvents(userId: String, eventIds: List<String>) {
        eventDao.insertJoinedEvents(eventIds.map { JoinedEventRoom(userId, it) })
    }

    override suspend fun leaveEvent(
        userId: String,
        eventId: String,
    ) {
        eventDao.deleteJoinedEvent(
            JoinedEventRoom(
                userId,
                eventId,
            ),
        )
    }

    override suspend fun leaveEventsNotIn(userId: String, eventIds: List<String>) {
        eventDao.deleteJoinedEventsNotIn(userId, eventIds)
    }

    override suspend fun getJoinedEvents(userId: String, syncedSecondsAgo: Long): List<Event> {
        val eventIds = eventDao.getJoinedEvents(userId, syncedSecondsAgo)
        return eventDao.getEventsByIds(eventIds).map { it.toEvent() }
    }

    override suspend fun joinAssociation(
        userId: String,
        associationId: String,
    ) {
        userProfileDao.joinAssociation(
            UserProfileAssociationSubscriptionCrossRef(
                userId,
                associationId,
            )
        )
    }

    override suspend fun leaveAssociation(userId: String, associationId: String) {
        userProfileDao.leaveAssociation(
            UserProfileAssociationSubscriptionCrossRef(
                userId,
                associationId,
            )
        )
    }
}
