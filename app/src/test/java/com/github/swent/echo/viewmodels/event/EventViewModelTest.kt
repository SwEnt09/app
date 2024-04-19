package com.github.swent.echo.viewmodels.event

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.fakes.FakeAuthenticationService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import java.time.ZonedDateTime
import java.util.concurrent.CompletableFuture
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class EventViewModelTest {

    private val TEST_EVENT =
        Event(
            eventId = "testid",
            creator = UserProfile("testid", "testname", null, null, emptySet()),
            organizer = Association("testid", "testname", "testdesc"),
            title = "test title",
            description = "test description",
            location = Location("test location", 100.0, 100.0),
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            tags = setOf(Tag("1", "tag1")),
            0,
            0,
            0
        )

    private fun mockLog() {
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any() as String) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    fun blockOnSaving() {
        coEvery { mockedRepository.setEvent(any()) } coAnswers
            {
                CompletableFuture<Event>().await()
            }
    }

    private val fakeAuthenticationService = FakeAuthenticationService()
    private val mockedRepository = mockk<Repository>(relaxed = true)
    private lateinit var eventViewModel: EventViewModel
    private val scheduler = TestCoroutineScheduler()
    private val savedEventId = SavedStateHandle(mapOf())

    @Before
    fun init() {
        fakeAuthenticationService.userID = "u0"
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        runBlocking {
            eventViewModel =
                EventViewModel(mockedRepository, fakeAuthenticationService, savedEventId)
        }
        scheduler.runCurrent()
    }

    @Test
    fun modifyNewEventTest() {
        val event = TEST_EVENT
        eventViewModel.setEvent(event)
        assertEquals(eventViewModel.event.value, event)
        assertTrue(eventViewModel.isEventNew.value)
    }

    @Test
    fun addTagToEventTest() {
        val newTag = Tag("2", "tag2")
        coEvery { mockedRepository.getAllTags() } returns listOf(newTag)
        runBlocking {
            eventViewModel =
                EventViewModel(mockedRepository, fakeAuthenticationService, savedEventId)
        }
        scheduler.runCurrent()
        val addedTag = eventViewModel.getAndAddTagFromString(newTag.name)
        assertEquals(addedTag, newTag)
        assertEquals(eventViewModel.event.value.tags, setOf(newTag))
    }

    @Test
    fun deleteTagFromEventTest() {
        val event = TEST_EVENT
        eventViewModel.setEvent(event)
        eventViewModel.deleteTag(Tag("1", "tag1"))
        assertEquals(eventViewModel.event.value.tags, setOf<Tag>())
    }

    @Test
    fun modifyEventWhileSavingLogWarningAndDoesNotChangeEvent() {
        mockLog()
        blockOnSaving()
        val event = TEST_EVENT
        val eventModified = event.copy(title = "change title")
        eventViewModel.setEvent(event)
        eventViewModel.saveEvent()
        eventViewModel.setEvent(eventModified)
        verify { Log.w(any(), any() as String) }
        assertEquals(event, eventViewModel.event.value)
    }

    @Test
    fun saveEventWhileSavingLogWarning() {
        mockLog()
        blockOnSaving()
        eventViewModel.setEvent(TEST_EVENT)
        eventViewModel.saveEvent()
        eventViewModel.saveEvent()
        verify { Log.w(any(), any() as String) }
    }

    @Test
    fun getAndAddTagFromStringReturnNullWithWrongTag() {
        val wrongTag = "not a valid tag"
        val ret = eventViewModel.getAndAddTagFromString(wrongTag)
        assertNull(ret)
    }

    @Test
    fun saveWithEndDateBeforeStartDateChangeStatusToError() {
        val event =
            TEST_EVENT.copy(
                startDate = ZonedDateTime.now(),
                endDate = ZonedDateTime.now().minusDays(1)
            )
        eventViewModel.setEvent(event)
        eventViewModel.saveEvent()
        assertTrue(eventViewModel.status.value is EventStatus.Error)
    }

    @Test
    fun saveWithBlankTitleChangeStatusToError() {
        val event = TEST_EVENT.copy(title = " ")
        eventViewModel.setEvent(event)
        eventViewModel.saveEvent()
        assertTrue(eventViewModel.status.value is EventStatus.Error)
    }

    @Test
    fun getOrganizerListReturnsCorrectUsername() {
        val testUserProfile =
            UserProfile(TEST_EVENT.creator.userId, "testname", null, null, setOf())
        coEvery { mockedRepository.getUserProfile(any()) } returns testUserProfile
        runBlocking {
            eventViewModel =
                EventViewModel(mockedRepository, fakeAuthenticationService, savedEventId)
        }
        scheduler.runCurrent()
        val organizerList = eventViewModel.organizerList
        assertEquals(listOf(testUserProfile.name), organizerList.value)
    }

    @Test
    fun setOrganizerNameAsEventCreatorSetTheCorrectValue() {
        val testUserProfile =
            UserProfile(TEST_EVENT.creator.userId, "testname", null, null, setOf())
        eventViewModel.setEvent(TEST_EVENT)
        coEvery { mockedRepository.getUserProfile(any()) } returns testUserProfile
        eventViewModel.setOrganizer(testUserProfile.name)
        scheduler.runCurrent()
        assertEquals(eventViewModel.event.value.organizer, null)
    }

    @Ignore // not implemented
    @Test
    fun setOrganizerNameAsOtherUserThrowError() {
        mockLog()
        val testUserProfile =
            UserProfile(TEST_EVENT.creator.userId, "testname", null, null, setOf())
        eventViewModel.setEvent(TEST_EVENT)
        coEvery { mockedRepository.getUserProfile(any()) } returns testUserProfile
        eventViewModel.setOrganizer("anothername")
        scheduler.runCurrent()
        verify { Log.e(any(), any()) }
    }

    @Ignore // not implemented
    @Test
    fun setOrganizerNameAsAssociationSetTheCorrectValue() {
        val testAssociation = Association("testAid", "testAname", "testDescription")
        eventViewModel.setEvent(TEST_EVENT)
        coEvery { mockedRepository.getAllAssociations() } returns listOf(testAssociation)
        eventViewModel.setOrganizer(testAssociation.name)
        scheduler.runCurrent()
        assertEquals(testAssociation, eventViewModel.event.value.organizer)
    }

    @Test
    fun modifyExistingEventUpdateItInTheRepository() {
        val existingEvent = TEST_EVENT
        val testEventId = SavedStateHandle(mapOf("eventId" to TEST_EVENT.creator.userId))
        coEvery { mockedRepository.getEvent(any()) } returns existingEvent
        runBlocking {
            eventViewModel =
                EventViewModel(mockedRepository, fakeAuthenticationService, testEventId)
        }
        scheduler.runCurrent()
        assertEquals(EventStatus.Saved, eventViewModel.status.value)
        assertFalse(eventViewModel.isEventNew.value)
        val modifiedExistingEvent = existingEvent.copy(title = "another title")
        eventViewModel.setEvent(modifiedExistingEvent)
        assertEquals(EventStatus.Modified, eventViewModel.status.value)
        eventViewModel.saveEvent()
        assertEquals(EventStatus.Saving, eventViewModel.status.value)
        scheduler.runCurrent()
        assertEquals(EventStatus.Saved, eventViewModel.status.value)
        coVerify { mockedRepository.setEvent(modifiedExistingEvent) }
    }
}
