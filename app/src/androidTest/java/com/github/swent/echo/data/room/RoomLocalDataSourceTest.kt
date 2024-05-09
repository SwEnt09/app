package com.github.swent.echo.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.SAMPLE_EVENTS
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.time.ZonedDateTime
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
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

    private val associations = SAMPLE_EVENTS.mapNotNull { it.organizer }.distinct()
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
            setOf(associations.first()),
            setOf(associations.first()),
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
        assertEquals(associations.toSet(), actual.toSet())

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertTrue(localDataSource.getAllAssociations(syncedSecondsAgo).isEmpty())
    }

    @Test
    fun testGetAllAssociationsSyncedBefore() = runBlocking {
        assertTrue(associations.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        localDataSource.setAssociations(associations)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        val actual = localDataSource.getAllAssociationsSyncedBefore(syncedSecondsAgo)
        assertEquals(associations.map { it.associationId }.toSet(), actual.toSet())

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        assertTrue(localDataSource.getAllAssociationsSyncedBefore(syncedSecondsAgo).isEmpty())
    }

    @Test
    fun testGetAndSetEvent() = runBlocking {
        assertTrue(events.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        val expected = events.first()
        localDataSource.setEvent(expected)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getEvent(expected.eventId, syncedSecondsAgo)
        assertEquals(expected, actual)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertNull(localDataSource.getEvent(expected.eventId, syncedSecondsAgo))
    }

    @Test
    fun testGetAndSetEvents() = runBlocking {
        assertTrue(events.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        localDataSource.setEvents(events)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getAllEvents(syncedSecondsAgo)
        assertEquals(events.toSet(), actual.toSet())

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertTrue(localDataSource.getAllEvents(syncedSecondsAgo).isEmpty())
    }

    @Test
    fun testGetAllEventsSyncedBefore() = runBlocking {
        assertTrue(events.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        localDataSource.setEvents(events)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        val actual = localDataSource.getAllEventsSyncedBefore(syncedSecondsAgo)
        assertEquals(events.map { it.eventId }.toSet(), actual.toSet())

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        assertTrue(localDataSource.getAllEventsSyncedBefore(syncedSecondsAgo).isEmpty())
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

    @Test
    fun testGetAllTagsSyncedBefore() = runBlocking {
        assertTrue(tags.isNotEmpty())

        every { ZonedDateTime.now() } returns time
        localDataSource.setTags(tags)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        val actual = localDataSource.getAllTagsSyncedBefore(syncedSecondsAgo)
        assertEquals(tags.map { it.tagId }.toSet(), actual.toSet())

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        assertTrue(localDataSource.getAllTagsSyncedBefore(syncedSecondsAgo).isEmpty())
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
    fun testGetAndSetUserProfile() = runBlocking {
        assertTrue(tags.isNotEmpty())
        assertTrue(associations.isNotEmpty())

        val expected = userProfile

        every { ZonedDateTime.now() } returns time
        localDataSource.setUserProfile(expected)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        val actual = localDataSource.getUserProfile(expected.userId, syncedSecondsAgo)
        assertEquals(expected, actual)

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        assertNull(localDataSource.getUserProfile(expected.userId, syncedSecondsAgo))
    }

    @Test
    fun testGetAllUserProfilesSyncedBefore() = runBlocking {
        assertTrue(tags.isNotEmpty())
        assertTrue(associations.isNotEmpty())

        val expected = userProfile

        every { ZonedDateTime.now() } returns time
        localDataSource.setUserProfile(expected)
        localDataSource.setUserProfile(expected.copy(userId = "1"))

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo * 2)
        val actual = localDataSource.getAllUserProfilesSyncedBefore(syncedSecondsAgo)
        assertEquals(listOf("0", "1").toSet(), actual.toSet())

        every { ZonedDateTime.now() } returns time.plusSeconds(syncedSecondsAgo / 2)
        assertTrue(localDataSource.getAllUserProfilesSyncedBefore(syncedSecondsAgo).isEmpty())
    }

    @Test
    fun testJoinAndLeaveEvent() {
        assertTrue(events.size >= 2)
        val event1 = events[0]
        val event2 = events[1]
        assertTrue(event1.eventId != event2.eventId)

        val userId = userProfile.userId

        runBlocking {
            localDataSource.setUserProfile(userProfile)
            localDataSource.setEvent(event1)
            localDataSource.setEvent(event2)
            localDataSource.joinEvent(userId, event1.eventId)
            localDataSource.joinEvent(userId, event2.eventId)
        }

        val joinedEvents = runBlocking { localDataSource.getJoinedEvents(userId) }
        assertEquals(2, joinedEvents.size)
        assertEquals(setOf(event1, event2), joinedEvents.toSet())

        runBlocking { localDataSource.leaveEvent(userId, event1.eventId) }

        val joinedEventsAfterLeaving = runBlocking { localDataSource.getJoinedEvents(userId) }
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
        assertEquals(setOf(association1, association2), newUserProfile.associationsSubscriptions)

        runBlocking { localDataSource.leaveAssociation(userId, association1.associationId) }

        val newUserProfileAfterLeaving = runBlocking {
            localDataSource.getUserProfile(userId, syncedSecondsAgo)
        }
        assertNotNull(newUserProfileAfterLeaving)

        assertEquals(1, newUserProfileAfterLeaving!!.associationsSubscriptions.size)
        assertTrue(newUserProfileAfterLeaving.associationsSubscriptions.contains(association2))
    }
}
