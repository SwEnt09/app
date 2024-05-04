package com.github.swent.echo.data.room

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.LocalDataSource
import com.github.swent.echo.data.room.entity.AssociationRoom
import com.github.swent.echo.data.room.entity.EventRoom
import com.github.swent.echo.data.room.entity.EventTagCrossRef
import com.github.swent.echo.data.room.entity.TagRoom
import com.github.swent.echo.data.room.entity.UserProfileAssociationSubscriptionCrossRef
import com.github.swent.echo.data.room.entity.UserProfileCommitteeMemberCrossRef
import com.github.swent.echo.data.room.entity.UserProfileRoom
import com.github.swent.echo.data.room.entity.UserProfileTagCrossRef
import com.github.swent.echo.data.room.entity.toAssociationRoomList
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
        associationDao.insert(AssociationRoom(association))
    }

    override suspend fun getAllAssociations(syncedSecondsAgo: Long): List<Association> {
        val after = computeTimestamp(syncedSecondsAgo)
        return associationDao.getAll(after).map { it.toAssociation() }
    }

    override suspend fun setAssociations(associations: List<Association>) {
        associationDao.insertAll(associations.map { AssociationRoom(it) })
    }

    override suspend fun getEvent(eventId: String, syncedSecondsAgo: Long): Event? {
        val after = computeTimestamp(syncedSecondsAgo)
        return eventDao.get(eventId, after)?.toEvent()
    }

    override suspend fun setEvent(event: Event) {
        val crossRefs = event.tags.map { EventTagCrossRef(event.eventId, it.tagId) }

        event.organizer?.let { associationDao.insert(AssociationRoom(it)) }
        tagDao.insertAll(event.tags.toTagRoomList())
        eventDao.insert(EventRoom(event))
        eventDao.insertEventTagCrossRegs(crossRefs)
    }

    override suspend fun getAllEvents(syncedSecondsAgo: Long): List<Event> {
        val after = computeTimestamp(syncedSecondsAgo)
        return eventDao.getAll(after).map { it.toEvent() }
    }

    override suspend fun setEvents(events: List<Event>) {
        val associations = events.mapNotNull { it.organizer }
        val tags = events.flatMap { it.tags }.toSet()
        val crossRefs =
            events.flatMap { event -> event.tags.map { EventTagCrossRef(event.eventId, it.tagId) } }

        associationDao.insertAll(associations.map { AssociationRoom(it) })
        tagDao.insertAll(tags.toTagRoomList())
        eventDao.insertAll(events.map { EventRoom(it) })
        eventDao.insertEventTagCrossRegs(crossRefs)
    }

    override suspend fun getTag(tagId: String, syncedSecondsAgo: Long): Tag? {
        val after = computeTimestamp(syncedSecondsAgo)
        return tagDao.get(tagId, after)?.toTag()
    }

    override suspend fun setTag(tag: Tag) {
        tagDao.insert(TagRoom(tag))
    }

    override suspend fun getSubTags(tagId: String, syncedSecondsAgo: Long): List<Tag> {
        val after = computeTimestamp(syncedSecondsAgo)
        return tagDao.getSubTags(tagId, after).toTagList()
    }

    override suspend fun getAllTags(syncedSecondsAgo: Long): List<Tag> {
        val after = computeTimestamp(syncedSecondsAgo)
        return tagDao.getAll(after).map { it.toTag() }
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

        val committeeMemberAssociations = userProfile.committeeMember.toAssociationRoomList()
        val committeeMemberCrossRefs =
            committeeMemberAssociations.map {
                UserProfileCommitteeMemberCrossRef(userProfile.userId, it.associationId)
            }

        val associationSubscriptions = userProfile.associationsSubscriptions.toAssociationRoomList()
        val associationSubscriptionCrossRefs =
            associationSubscriptions.map {
                UserProfileAssociationSubscriptionCrossRef(userProfile.userId, it.associationId)
            }

        tagDao.insertAll(tags)
        associationDao.insertAll(committeeMemberAssociations)
        associationDao.insertAll(associationSubscriptions)
        userProfileDao.insert(UserProfileRoom(userProfile))

        userProfileDao.insertUserProfileTagCrossRefs(tagCrossRefs)
        userProfileDao.insertUserProfileCommitteeMemberCrossRefs(committeeMemberCrossRefs)
        userProfileDao.insertUserProfileAssociationSubscriptionCrossRefs(
            associationSubscriptionCrossRefs
        )
    }
}
