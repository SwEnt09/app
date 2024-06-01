package com.github.swent.echo.viewmodels.event

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.AssociationHeader
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.data.repository.RepositoryStoreWhileNoInternetException
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
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class EventViewModelTest {

    private val TEST_EVENT =
        Event(
            eventId = "testid",
            creator = EventCreator("testid", "testname"),
            organizer = AssociationHeader("testid", "testname"),
            title = "test title",
            description = "test description",
            location = Location("test location", 10.0, 10.0),
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            tags = setOf(Tag("1", "tag1")),
            0,
            15,
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
    private val mockedNetworkService = mockk<NetworkService>()

    @Before
    fun init() {
        every { mockedNetworkService.isOnline } returns MutableStateFlow(true)
        fakeAuthenticationService.userID = "u0"
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        runBlocking {
            eventViewModel =
                EventViewModel(
                    mockedRepository,
                    fakeAuthenticationService,
                    savedEventId,
                    mockedNetworkService
                )
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
        scheduler.runCurrent()
        verify { Log.w(any(), any() as String) }
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
        scheduler.runCurrent()
        assertTrue(eventViewModel.status.value is EventStatus.Error)
    }

    @Test
    fun saveWithBlankTitleChangeStatusToError() {
        val event = TEST_EVENT.copy(title = " ")
        eventViewModel.setEvent(event)
        eventViewModel.saveEvent()
        scheduler.runCurrent()
        assertTrue(eventViewModel.status.value is EventStatus.Error)
    }

    @Test
    fun saveWhileNetworkErrorChangesStatusToError() {
        coEvery { mockedRepository.createEvent(TEST_EVENT) } throws
            RepositoryStoreWhileNoInternetException("test")
        coEvery { mockedRepository.setEvent(TEST_EVENT) } throws
            RepositoryStoreWhileNoInternetException("test")
        eventViewModel.setEvent(TEST_EVENT)
        eventViewModel.saveEvent()
        scheduler.runCurrent()
        assertTrue(eventViewModel.status.value is EventStatus.Error)
    }

    @Test
    fun saveWhileNotLoggedInChangeStatusToError() {
        mockLog()
        val mockedAuthService = mockk<AuthenticationService>()
        every { mockedAuthService.getCurrentUserID() } returns null
        runBlocking {
            eventViewModel =
                EventViewModel(
                    mockedRepository,
                    mockedAuthService,
                    savedEventId,
                    mockedNetworkService
                )
        }
        scheduler.runCurrent()
        assertTrue(eventViewModel.status.value is EventStatus.Error)
        eventViewModel.dismissError()
        assertTrue(eventViewModel.status.value !is EventStatus.Error)
        eventViewModel.setEvent(TEST_EVENT.copy(creator = EventCreator.EMPTY))
        eventViewModel.saveEvent()
        assertTrue(eventViewModel.status.value is EventStatus.Error)
    }

    @Test
    fun getOrganizerListReturnsCorrectUsername() {
        val testUserProfile =
            UserProfile(
                TEST_EVENT.creator.userId,
                "testname",
                null,
                null,
                setOf(),
                setOf(),
                setOf()
            )
        coEvery { mockedRepository.getUserProfile(any()) } returns testUserProfile
        runBlocking {
            eventViewModel =
                EventViewModel(
                    mockedRepository,
                    fakeAuthenticationService,
                    savedEventId,
                    mockedNetworkService
                )
        }
        scheduler.runCurrent()
        val organizerList = eventViewModel.organizerList
        assertEquals(listOf(testUserProfile.name), organizerList.value)
    }

    @Test
    fun setOrganizerNameAsEventCreatorSetTheCorrectValue() {
        val testUserProfile =
            UserProfile(
                TEST_EVENT.creator.userId,
                "testname",
                null,
                null,
                setOf(),
                setOf(),
                setOf()
            )
        eventViewModel.setEvent(TEST_EVENT)
        coEvery { mockedRepository.getUserProfile(any()) } returns testUserProfile
        eventViewModel.setOrganizer(testUserProfile.name)
        scheduler.runCurrent()
        assertEquals(eventViewModel.event.value.organizer, null)
    }

    @Test
    fun setOrganizerNameAsNonExistingAssociationSetNull() {
        val testUserProfile =
            UserProfile(
                TEST_EVENT.creator.userId,
                "testname",
                null,
                null,
                setOf(),
                setOf(),
                setOf()
            )
        eventViewModel.setEvent(TEST_EVENT)
        coEvery { mockedRepository.getUserProfile(any()) } returns testUserProfile
        eventViewModel.setOrganizer("anonexistingassociation")
        scheduler.runCurrent()
        assertEquals(null, eventViewModel.event.value.organizer)
    }

    @Test
    fun setOrganizerNameAsAssociationSetTheCorrectValue() {
        val testAssociation =
            Association("testAid", "testAname", "testDescription", "testUrl", setOf())
        eventViewModel.setEvent(TEST_EVENT)
        coEvery { mockedRepository.getAllAssociations() } returns listOf(testAssociation)
        eventViewModel.setOrganizer(testAssociation.name)
        scheduler.runCurrent()
        assertEquals(
            AssociationHeader.fromAssociation(testAssociation),
            eventViewModel.event.value.organizer
        )
    }

    @Test
    fun modifyExistingEventUpdateItInTheRepository() {
        val existingEvent = TEST_EVENT
        val testEventId = SavedStateHandle(mapOf("eventId" to TEST_EVENT.creator.userId))
        coEvery { mockedRepository.getEvent(any()) } returns existingEvent
        runBlocking {
            eventViewModel =
                EventViewModel(
                    mockedRepository,
                    fakeAuthenticationService,
                    testEventId,
                    mockedNetworkService
                )
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

    @Test
    fun saveNewEventCreateNewEventInRepository() {
        coEvery { mockedRepository.createEvent(TEST_EVENT) } returns TEST_EVENT.eventId
        eventViewModel.setEvent(TEST_EVENT)
        eventViewModel.saveEvent()
        scheduler.runCurrent()
        coVerify { mockedRepository.createEvent(TEST_EVENT) }
        assertEquals(TEST_EVENT.eventId, eventViewModel.event.value.eventId)
    }

    @Test
    fun eventViewModelWhileLoggedOutLogError() {
        mockLog()
        val mockedAuth = mockk<AuthenticationService>()
        every { mockedAuth.getCurrentUserID() } returns null
        runBlocking {
            eventViewModel =
                EventViewModel(mockedRepository, mockedAuth, savedEventId, mockedNetworkService)
        }
        scheduler.runCurrent()
        verify { Log.e(any(), any() as String) }
    }

    @Test
    fun eventViewModelWithNullEventIdCreateANewOne() {
        val mockedEventId = mockk<SavedStateHandle>(relaxed = true)
        every { mockedEventId.contains(any()) } returns true
        every { mockedEventId.get<String>(any()) } returns ""
        every { mockedEventId[any()] = "" }
        coEvery { mockedRepository.getUserProfile(any()) } returns UserProfile.EMPTY
        coEvery { mockedRepository.getEvent(any()) } returns null
        var creator = EventCreator.EMPTY
        runBlocking {
            creator = mockedRepository.getUserProfile("")!!.toEventCreator()
            eventViewModel =
                EventViewModel(
                    mockedRepository,
                    fakeAuthenticationService,
                    mockedEventId,
                    mockedNetworkService
                )
        }
        scheduler.runCurrent()
        assertEquals(Event.EMPTY.copy(creator = creator), eventViewModel.event.value)
        assertTrue(eventViewModel.isEventNew.value)
    }
}
