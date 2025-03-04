package com.github.swent.echo.data.repository

import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.SAMPLE_EVENTS
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.UserProfile
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SimpleRepositoryTest {

    companion object {
        const val USER_ID = "userId"
    }

    private lateinit var authenticationService: AuthenticationService
    private lateinit var simpleRepository: SimpleRepository

    @Before
    fun setUp() {
        authenticationService = mockk { every { getCurrentUserID() } returns USER_ID }
        simpleRepository = SimpleRepository(authenticationService)
    }

    @Test
    fun `should be populated with sample data`() = runBlocking {
        val associations = simpleRepository.getAllAssociations()
        val events = simpleRepository.getAllEvents()
        val tags = simpleRepository.getAllTags()
        val userProfile = simpleRepository.getUserProfile(USER_ID)

        assertEquals(SAMPLE_EVENTS.mapNotNull { it.organizer }.toSet().size, associations.size)
        assertEquals(SAMPLE_EVENTS.size, events.size)
        assertTrue(tags.size >= SimpleRepository.NUM_OF_HARDCODED_TAGS)
        assertNotNull(userProfile)
    }

    @Test
    fun `getAssociation should return the association with the given id`() = runBlocking {
        val associations = simpleRepository.getAllAssociations()
        val expected = associations.first()
        val association = simpleRepository.getAssociation(expected.associationId)
        assertEquals(expected, association)
    }

    @Test
    fun `get Associations should return the associations with the given ids`() = runBlocking {
        val associations = simpleRepository.getAllAssociations()
        val result = simpleRepository.getAssociations(associations.map { it.associationId })
        assertEquals(associations, result)
    }

    @Test
    fun `getEvent should return the event with the given id`() = runBlocking {
        val events = simpleRepository.getAllEvents()
        val expected = events.first()
        val event = simpleRepository.getEvent(expected.eventId)
        assertEquals(expected, event)
    }

    @Test
    fun `setEvent should update the event in the repository`() = runBlocking {
        val events = simpleRepository.getAllEvents()
        val event = events.first()
        val updatedEvent = event.copy(title = "Updated Event")
        simpleRepository.setEvent(updatedEvent)
        assertEquals(updatedEvent, simpleRepository.getEvent(event.eventId))
    }

    @Test
    fun `deleteEvent should delete event in the repository`() = runBlocking {
        val events = simpleRepository.getAllEvents()
        val eventToDelete = events.first()
        val updatedEvents = events.drop(1)
        simpleRepository.deleteEvent(eventToDelete)
        assertEquals(updatedEvents, simpleRepository.getAllEvents())
    }

    @Test
    fun `joinEvent and leaveEvent should add and remove Event to list returned by getJoinedEvents`() =
        runBlocking {
            val events = simpleRepository.getAllEvents()
            val event = events.first()
            val joined = simpleRepository.joinEvent(USER_ID, event)
            assertTrue(joined)
            val joinedEvents = simpleRepository.getJoinedEvents(USER_ID)
            assertEquals(listOf(event), joinedEvents)
            val left = simpleRepository.leaveEvent(USER_ID, event)
            assertTrue(left)
            val joinedEventsAfterLeave = simpleRepository.getJoinedEvents(USER_ID)
            assertEquals(listOf<Event>(), joinedEventsAfterLeave)
        }

    @Test
    fun `getTag should return the tag with the given id`() = runBlocking {
        val tags = simpleRepository.getAllTags()
        val expected = tags.first()
        val tag = simpleRepository.getTag(expected.tagId)
        assertEquals(expected, tag)
    }

    @Test
    fun `getUserProfile should return the user profile with the given id`() = runBlocking {
        val newUserId = "newUserId"
        val newUserProfile =
            UserProfile(
                newUserId,
                "New User",
                SemesterEPFL.BA1,
                SectionEPFL.SC,
                emptySet(),
                emptySet(),
                emptySet()
            )
        simpleRepository.setUserProfile(newUserProfile)

        val userProfile = simpleRepository.getUserProfile(newUserId)
        assertEquals(newUserProfile, userProfile)
    }

    @Test
    fun `setUserProfile should update the user profile in the repository`() = runBlocking {
        val userProfile = simpleRepository.getUserProfile(USER_ID)!!
        val updatedUserProfile = userProfile.copy(name = "Updated Name")
        simpleRepository.setUserProfile(updatedUserProfile)
        assertEquals(updatedUserProfile, simpleRepository.getUserProfile(USER_ID))
    }

    @Test
    fun `deleteUserProfile should delete user profile in the repository`() = runBlocking {
        val userProfile = simpleRepository.getUserProfile(USER_ID)!!
        simpleRepository.deleteUserProfile(userProfile)
        assertNull(simpleRepository.getUserProfile(USER_ID))
    }

    @Test
    fun `createEvent should add the event to the repository and return its id`() = runBlocking {
        val numOfEvents = simpleRepository.getAllEvents().size

        val newEvent = Event.EMPTY.copy(title = "New Event")
        val id = simpleRepository.createEvent(newEvent)
        assertEquals(numOfEvents + 1, simpleRepository.getAllEvents().size)
        assertEquals(id, simpleRepository.getAllEvents().last().eventId)
    }

    @Test
    fun `getSubTags should return top level tags when called with ROOT_TAG_ID`() = runBlocking {
        val subTags = simpleRepository.getSubTags(SimpleRepository.ROOT_TAG_ID)
        assertEquals(SimpleRepository.NUM_OF_TOP_LEVEL_TAGS, subTags.size)
    }

    @Test
    fun setGetAndDeleteUserProfileTest() {
        val picture = ByteArray(50)
        runBlocking {
            simpleRepository.setUserProfilePicture(USER_ID, picture)
            val res = simpleRepository.getUserProfilePicture(USER_ID)
            assertEquals(picture, res)
            simpleRepository.deleteUserProfilePicture(USER_ID)
            val nullRes = simpleRepository.getUserProfilePicture(USER_ID)
            assertEquals(null, nullRes)
        }
    }
}
