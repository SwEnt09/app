package com.github.swent.echo.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.SAMPLE_ASSOCIATIONS
import com.github.swent.echo.data.SAMPLE_EVENTS
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.AssociationHeader
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.model.toAssociationHeader
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.time.ZonedDateTime
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

// As the local data source stores the dates in milliseconds, we need to round the dates to avoid
// precision loss during saving and retrieving from the database.
fun ZonedDateTime.round(): ZonedDateTime {
    return this.withNano(0)
}

@RunWith(AndroidJUnit4::class)
class RoomLocalDataSourceTest {

    private lateinit var localDataSource: RoomLocalDataSource

    private val associations = SAMPLE_ASSOCIATIONS
    private val events =
        SAMPLE_EVENTS.map { event ->
            event.copy(startDate = event.startDate.round(), endDate = event.endDate.round())
        }
    private val tags = SAMPLE_EVENTS.flatMap { it.tags }.distinct()
    private val userProfile =
        UserProfile(
            "0",
            "John Doe",
            SemesterEPFL.BA1,
            SectionEPFL.IN,
            setOf(tags.first()),
            setOf(AssociationHeader.fromAssociation(associations.first())!!),
            setOf(AssociationHeader.fromAssociation(associations.first())!!),
        )

    private val time = ZonedDateTime.parse("2024-01-01T00:00:00Z")
    private val syncedSecondsAgo = 60L

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        localDataSource = RoomLocalDataSource(db)

        mockkStatic(ZonedDateTime::class)
    }

    @After
    fun tearDown() {
        unmockkStatic(ZonedDateTime::class)
    }

    @Test
    fun testGetAndSetAssociation() = runBlocking {
        assertTrue(associations.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        val expected = associations.first()
        localDataSource.setAssociation(expected)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getAssociation(expected.associationId, syncedSecondsAgo)
        assertEquals(expected, actual)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertNull(localDataSource.getAssociation(expected.associationId, syncedSecondsAgo))
    }

    @Test
    fun testGetAndSetAssociations() = runBlocking {
        assertTrue(associations.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        localDataSource.setAssociations(associations)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getAllAssociations(syncedSecondsAgo)
        val actualSelectiv =
            localDataSource.getAssociations(listOf(associations[0].associationId), syncedSecondsAgo)
        assertEquals(associations.toSet(), actual.toSet())
        assertEquals(listOf(associations[0]), actualSelectiv)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertTrue(localDataSource.getAllAssociations(syncedSecondsAgo).isEmpty())
    }

    @Test
    fun getAssociationIds() = runBlocking {
        assertTrue(associations.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        localDataSource.setAssociations(associations)

        every { ZonedDateTime.now() } returns time
        localDataSource.setAssociations(associations)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertTrue(
            localDataSource
                .getAssociationIds(associations.map { it.associationId }, syncedSecondsAgo)
                .isEmpty()
        )

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual =
            localDataSource.getAssociationIds(
                associations.map { it.associationId },
                syncedSecondsAgo
            )
        assertEquals(associations.map { it.associationId }.toSet(), actual.toSet())
    }

    @Test
    fun testGetAllAssociationIds() = runBlocking {
        assertTrue(associations.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        localDataSource.setAssociations(associations)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertTrue(localDataSource.getAllAssociationIds(syncedSecondsAgo).isEmpty())

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getAllAssociationIds(syncedSecondsAgo)
        assertEquals(associations.map { it.associationId }.toSet(), actual.toSet())
    }

    @Test
    fun testDeleteAssociationsNotIn() = runBlocking {
        assertTrue(associations.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        localDataSource.setAssociations(associations)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        localDataSource.deleteAssociationsNotIn(associations.map { it.associationId })
        val actualNothingDeleted = localDataSource.getAllAssociations(syncedSecondsAgo)
        assertEquals(associations, actualNothingDeleted)

        localDataSource.deleteAssociationsNotIn(associations.map { it.associationId }.drop(1))
        val actualDroppedFirst = localDataSource.getAllAssociations(syncedSecondsAgo)
        assertEquals(associations.drop(1), actualDroppedFirst)

        localDataSource.deleteAssociationsNotIn(listOf())
        val actual = localDataSource.getAllAssociations(syncedSecondsAgo)
        assertEquals(listOf<Association>(), actual)
    }

    @Test
    fun testGetAndSetAndDeleteEvent() = runBlocking {
        assertTrue(events.isNotEmpty())
        assertTrue(events[1].tags.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        val expected = events.first()
        localDataSource.setEvent(expected)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getEvent(expected.eventId, syncedSecondsAgo)
        assertEquals(expected, actual)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertNull(localDataSource.getEvent(expected.eventId, syncedSecondsAgo))

        every { ZonedDateTime.now() } returns time
        val expectedWithoutTags = expected.copy(tags = setOf())
        localDataSource.setEvent(expectedWithoutTags)
        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        assertEquals(
            expectedWithoutTags,
            localDataSource.getEvent(expectedWithoutTags.eventId, syncedSecondsAgo)
        )

        localDataSource.deleteEvent(expected.eventId)
        val shouldBeDeleted = localDataSource.getEvent(expected.eventId, syncedSecondsAgo)
        assertNull(shouldBeDeleted)
    }

    @Test
    fun testGetAndSetEvents() = runBlocking {
        assertTrue(events.isNotEmpty())
        assertTrue(events[1].tags.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        localDataSource.setEvents(events)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getAllEvents(syncedSecondsAgo)
        assertEquals(events.toSet(), actual.toSet())

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertTrue(localDataSource.getAllEvents(syncedSecondsAgo).isEmpty())

        every { ZonedDateTime.now() } returns time
        val eventsWithoutTags = events.map { it.copy(tags = setOf()) }
        localDataSource.setEvents(eventsWithoutTags)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        assertEquals(eventsWithoutTags, localDataSource.getAllEvents(syncedSecondsAgo))
    }

    @Test
    fun testGetAllEventIds() = runBlocking {
        assertTrue(events.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        localDataSource.setEvents(events)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertTrue(localDataSource.getAllEventIds(syncedSecondsAgo).isEmpty())

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getAllEventIds(syncedSecondsAgo)
        assertEquals(events.map { it.eventId }.toSet(), actual.toSet())
    }

    @Test
    fun testDeleteEventsNotIn() = runBlocking {
        assertTrue(events.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        localDataSource.setEvents(events)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        localDataSource.deleteEventsNotIn(events.map { it.eventId })
        val actualNothingDeleted = localDataSource.getAllEvents(syncedSecondsAgo)
        assertEquals(events, actualNothingDeleted)

        localDataSource.deleteEventsNotIn(events.map { it.eventId }.drop(1))
        val actualDroppedFirst = localDataSource.getAllEvents(syncedSecondsAgo)
        assertEquals(events.drop(1), actualDroppedFirst)

        localDataSource.deleteEventsNotIn(listOf())
        val actual = localDataSource.getAllEvents(syncedSecondsAgo)
        assertEquals(listOf<Event>(), actual)
    }

    @Test
    fun testGetAndSetTag() = runBlocking {
        assertTrue(tags.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        val expected = tags.first()
        localDataSource.setTag(expected)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getTag(expected.tagId, syncedSecondsAgo)
        assertEquals(expected, actual)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertNull(localDataSource.getTag(expected.tagId, syncedSecondsAgo))
    }

    @Test
    fun testGetAndSetTags() = runBlocking {
        assertTrue(tags.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        localDataSource.setTags(tags)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getAllTags(syncedSecondsAgo)
        assertEquals(tags.toSet(), actual.toSet())

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertTrue(localDataSource.getAllTags(syncedSecondsAgo).isEmpty())
    }

    @Ignore("This test is sometimes failing in our CI pipeline")
    @Test
    fun testGetAllTagIds() = runBlocking {
        assertTrue(tags.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        localDataSource.setTags(tags)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertTrue(localDataSource.getAllTagIds(syncedSecondsAgo).isEmpty())

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getAllTagIds(syncedSecondsAgo)
        assertEquals(tags.map { it.tagId }.toSet(), actual.toSet())
    }

    @Test
    fun testDeleteAllTagsNotIn() = runBlocking {
        assertTrue(events.size >= 2)

        every { ZonedDateTime.now() } returns time
        localDataSource.setTags(tags)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        localDataSource.deleteAllTagsNotIn(tags.map { it.tagId })
        val actualNothingDeleted = localDataSource.getAllTags(syncedSecondsAgo)
        assertEquals(tags, actualNothingDeleted)

        localDataSource.deleteAllTagsNotIn(tags.map { it.tagId }.drop(1))
        val actualDroppedFirst = localDataSource.getAllTags(syncedSecondsAgo)
        assertEquals(tags.drop(1), actualDroppedFirst)

        localDataSource.deleteAllTagsNotIn(listOf())
        val actual = localDataSource.getAllTags(syncedSecondsAgo)
        assertEquals(listOf<Tag>(), actual)
    }

    @Test
    fun testSubTags() = runBlocking {
        val tags =
            listOf(
                Tag("0", "ROOT", null),
                Tag("1", "SUB1", "0"),
                Tag("2", "SUB2", "0"),
            )

        every { ZonedDateTime.now() } returns time
        localDataSource.setTags(tags)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getSubTags("0", syncedSecondsAgo)
        val expected =
            listOf(
                Tag("1", "SUB1", "0"),
                Tag("2", "SUB2", "0"),
            )
        assertEquals(expected.toSet(), actual.toSet())

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertTrue(localDataSource.getSubTags("0", syncedSecondsAgo).isEmpty())
    }

    @Test
    fun testDeleteSubTagsNotIn() = runBlocking {
        val tags =
            listOf(
                Tag("0", "ROOT", null),
                Tag("1", "SUB1", "0"),
                Tag("2", "SUB2", "0"),
            )

        every { ZonedDateTime.now() } returns time
        localDataSource.setTags(tags)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        localDataSource.deleteSubTagsNotIn(tags[0].tagId, tags.drop(1).map { it.tagId })
        val actualNothingDeleted = localDataSource.getSubTags(tags[0].tagId, syncedSecondsAgo)
        assertEquals(tags.drop(1), actualNothingDeleted)

        localDataSource.deleteSubTagsNotIn(tags[0].tagId, tags.map { it.tagId }.drop(2))
        val actualDroppedFirst = localDataSource.getSubTags(tags[0].tagId, syncedSecondsAgo)
        assertEquals(tags.drop(2), actualDroppedFirst)

        localDataSource.deleteSubTagsNotIn(tags[0].tagId, listOf())
        val actualAllDeleted = localDataSource.getSubTags(tags[0].tagId, syncedSecondsAgo)
        assertEquals(listOf<Tag>(), actualAllDeleted)

        val actualRemainingParent = localDataSource.getAllTags(syncedSecondsAgo)
        assertEquals(listOf(tags[0]), actualRemainingParent)
    }

    @Test
    fun testGetAndSetAndDeleteUserProfile() = runBlocking {
        assertTrue(tags.isNotEmpty())
        assertTrue(associations.isNotEmpty())
        assertTrue(userProfile.tags.isNotEmpty())
        assertTrue(userProfile.committeeMember.isNotEmpty())
        assertTrue(userProfile.associationsSubscriptions.isNotEmpty())

        val expected = userProfile

        every { ZonedDateTime.now() } returns time
        localDataSource.setAssociations(associations)
        localDataSource.setUserProfile(expected)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getUserProfile(expected.userId, syncedSecondsAgo)
        assertEquals(expected, actual)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertNull(localDataSource.getUserProfile(expected.userId, syncedSecondsAgo))

        every { ZonedDateTime.now() } returns time
        val userProfileDeletedRelationalAttributes =
            userProfile.copy(
                tags = setOf(),
                committeeMember = setOf(),
                associationsSubscriptions = setOf()
            )
        localDataSource.setUserProfile(userProfileDeletedRelationalAttributes)
        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        assertEquals(
            userProfileDeletedRelationalAttributes,
            localDataSource.getUserProfile(userProfile.userId, syncedSecondsAgo)
        )
    }

    @Test
    fun testJoinAndLeaveEvent() {
        assertTrue(events.size >= 2)
        val event1 = events[0]
        val event2 = events[1]
        assertTrue(event1.eventId != event2.eventId)

        val userId = userProfile.userId

        runBlocking {
            localDataSource.setAssociations(associations)
            localDataSource.setUserProfile(userProfile)
            localDataSource.setEvent(event1)
            localDataSource.setEvent(event2)
            localDataSource.joinEvent(userId, event1.eventId)
            localDataSource.joinEvent(userId, event2.eventId)
        }

        val joinedEvents = runBlocking { localDataSource.getJoinedEvents(userId, 0) }
        assertEquals(2, joinedEvents.size)
        assertEquals(setOf(event1, event2), joinedEvents.toSet())

        runBlocking { localDataSource.leaveEvent(userId, event1.eventId) }

        val joinedEventsAfterLeaving = runBlocking { localDataSource.getJoinedEvents(userId, 0) }
        assertEquals(1, joinedEventsAfterLeaving.size)
        assertTrue(joinedEventsAfterLeaving.contains(event2))
    }

    @Test
    fun testJoinEventsAndLeaveEventsNotIn() {
        assertTrue(events.size >= 2)
        val event1 = events[0]
        val event2 = events[1]
        assertTrue(event1.eventId != event2.eventId)

        val userId = userProfile.userId

        runBlocking {
            localDataSource.setAssociations(associations)
            localDataSource.setUserProfile(userProfile)
            localDataSource.setEvents(listOf(event1, event2))
            localDataSource.joinEvents(userId, listOf(event1.eventId, event2.eventId))
        }

        val joinedEvents = runBlocking { localDataSource.getJoinedEvents(userId, 0) }
        assertEquals(2, joinedEvents.size)
        assertEquals(setOf(event1, event2), joinedEvents.toSet())

        runBlocking { localDataSource.leaveEventsNotIn(userId, listOf(event2.eventId)) }

        val joinedEventsAfterLeaving = runBlocking { localDataSource.getJoinedEvents(userId, 0) }
        assertEquals(1, joinedEventsAfterLeaving.size)
        assertTrue(joinedEventsAfterLeaving.contains(event2))
    }

    @Test
    fun testJoinAndLeaveAssociation() {
        assertTrue(associations.size >= 2)
        val association1 = associations[0]
        val association2 = associations[1]
        assertTrue(association1.associationId != association2.associationId)

        val userId = userProfile.userId

        runBlocking {
            localDataSource.setAssociations(associations)
            localDataSource.setUserProfile(userProfile)
            localDataSource.setAssociation(association1)
            localDataSource.setAssociation(association2)
            localDataSource.joinAssociation(userId, association1.associationId)
            localDataSource.joinAssociation(userId, association2.associationId)
        }

        val newUserProfile = runBlocking {
            localDataSource.getUserProfile(userId, syncedSecondsAgo)
        }
        assertNotNull(newUserProfile)

        assertEquals(2, newUserProfile!!.associationsSubscriptions.size)
        assertEquals(
            listOf(association1, association2).toAssociationHeader().toSet(),
            newUserProfile.associationsSubscriptions
        )

        runBlocking { localDataSource.leaveAssociation(userId, association1.associationId) }

        val newUserProfileAfterLeaving = runBlocking {
            localDataSource.getUserProfile(userId, syncedSecondsAgo)
        }
        assertNotNull(newUserProfileAfterLeaving)

        assertEquals(1, newUserProfileAfterLeaving!!.associationsSubscriptions.size)
        assertTrue(
            newUserProfileAfterLeaving.associationsSubscriptions.contains(
                AssociationHeader.fromAssociation(association2)
            )
        )
    }
}
