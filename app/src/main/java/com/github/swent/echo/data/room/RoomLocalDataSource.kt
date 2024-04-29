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
import com.github.swent.echo.data.room.entity.UserProfileRoom
import com.github.swent.echo.data.room.entity.UserProfileTagCrossRef
import com.github.swent.echo.data.room.entity.toTagList
import com.github.swent.echo.data.room.entity.toTagRoomList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomLocalDataSource @Inject constructor(db: AppDatabase) : LocalDataSource {

    private val associationDao = db.associationDao()
    private val eventDao = db.eventDao()
    private val tagDao = db.tagDao()
    private val userProfileDao = db.userProfileDao()

    override suspend fun getAssociation(associationId: String): Association? {
        return associationDao.get(associationId)?.toAssociation()
    }

    override suspend fun setAssociation(association: Association) {
        associationDao.insert(AssociationRoom(association))
    }

    override suspend fun getAllAssociations(): List<Association> {
        return associationDao.getAll().map { it.toAssociation() }
    }

    override suspend fun setAssociations(associations: List<Association>) {
        associationDao.insertAll(associations.map { AssociationRoom(it) })
    }

    override suspend fun getEvent(eventId: String): Event? {
        return eventDao.get(eventId)?.toEvent()
    }

    override suspend fun setEvent(event: Event) {
        val crossRefs = event.tags.map { EventTagCrossRef(event.eventId, it.tagId) }

        eventDao.insert(EventRoom(event))
        eventDao.insertEventTagCrossRegs(crossRefs)
        event.organizer?.let { associationDao.insert(AssociationRoom(it)) }
        tagDao.insertAll(event.tags.toTagRoomList())
    }

    override suspend fun getAllEvents(): List<Event> {
        return eventDao.getAll().map { it.toEvent() }
    }

    override suspend fun setEvents(events: List<Event>) {
        val associations = events.mapNotNull { it.organizer }
        val tags = events.flatMap { it.tags }.toSet()
        val crossRefs =
            events.flatMap { event -> event.tags.map { EventTagCrossRef(event.eventId, it.tagId) } }

        eventDao.insertAll(events.map { EventRoom(it) })
        eventDao.insertEventTagCrossRegs(crossRefs)
        associationDao.insertAll(associations.map { AssociationRoom(it) })
        tagDao.insertAll(tags.toTagRoomList())
    }

    override suspend fun getTag(tagId: String): Tag? {
        return tagDao.get(tagId)?.toTag()
    }

    override suspend fun setTag(tag: Tag) {
        tagDao.insert(TagRoom(tag))
    }

    override suspend fun getSubTags(tagId: String): List<Tag> {
        return tagDao.getSubTags(tagId).toTagList()
    }

    override suspend fun getAllTags(): List<Tag> {
        return tagDao.getAll().map { it.toTag() }
    }

    override suspend fun setTags(tags: List<Tag>) {
        tagDao.insertAll(tags.map { TagRoom(it) })
    }

    override suspend fun getUserProfile(userId: String): UserProfile? {
        return userProfileDao.get(userId)?.toUserProfile()
    }

    override suspend fun setUserProfile(userProfile: UserProfile) {
        val tags = userProfile.tags.toTagRoomList()
        val crossRefs = tags.map { UserProfileTagCrossRef(userProfile.userId, it.tagId) }

        userProfileDao.insert(UserProfileRoom(userProfile))
        userProfileDao.insertUserProfileTagCrossRefs(crossRefs)
        tagDao.insertAll(tags)
    }
}
