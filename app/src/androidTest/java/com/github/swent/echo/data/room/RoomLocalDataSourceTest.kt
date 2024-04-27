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
import java.time.ZonedDateTime
import kotlinx.coroutines.runBlocking
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

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        localDataSource = RoomLocalDataSource(db)
    }

    @Test
    fun testGetAndSetAssociation() = runBlocking {
        assertTrue(associations.isNotEmpty())

        val expected = associations.first()
        localDataSource.setAssociation(expected)

        val actual = localDataSource.getAssociation(expected.associationId)
        assertEquals(expected, actual)
    }

    @Test
    fun testGetAndSetAssociations() = runBlocking {
        assertTrue(associations.isNotEmpty())

        localDataSource.setAssociations(associations)

        val actual = localDataSource.getAllAssociations()
        assertEquals(associations, actual)
    }

    @Test
    fun testGetAndSetEvent() = runBlocking {
        assertTrue(events.isNotEmpty())

        val expected = events.first()
        localDataSource.setEvent(expected)

        val actual = localDataSource.getEvent(expected.eventId)
        assertEquals(expected, actual)
    }

    @Test
    fun testGetAndSetEvents() = runBlocking {
        assertTrue(events.isNotEmpty())

        localDataSource.setEvents(events)

        val actual = localDataSource.getAllEvents()
        assertEquals(events, actual)
    }

    @Test
    fun testGetAndSetTag() = runBlocking {
        assertTrue(tags.isNotEmpty())

        val expected = tags.first()
        localDataSource.setTag(expected)

        val actual = localDataSource.getTag(expected.tagId)
        assertEquals(expected, actual)
    }

    @Test
    fun testGetAndSetTags() = runBlocking {
        assertTrue(tags.isNotEmpty())

        localDataSource.setTags(tags)

        val actual = localDataSource.getAllTags()
        assertEquals(tags, actual)
    }

    @Test
    fun testSubTags() = runBlocking {
        val tags =
            listOf(
                Tag("0", "ROOT", null),
                Tag("1", "SUB1", "0"),
                Tag("2", "SUB2", "0"),
            )

        localDataSource.setTags(tags)

        val actual = localDataSource.getSubTags("0")
        val expected =
            listOf(
                Tag("1", "SUB1", "0"),
                Tag("2", "SUB2", "0"),
            )
        assertEquals(expected, actual)
    }

    @Test
    fun testGetAndSetUserProfile() = runBlocking {
        val expected =
            UserProfile(
                "0",
                "John Doe",
                SemesterEPFL.BA1,
                SectionEPFL.IN,
                setOf(tags.first()),
            )

        localDataSource.setUserProfile(expected)

        val actual = localDataSource.getUserProfile(expected.userId)
        assertEquals(expected, actual)
    }
}
