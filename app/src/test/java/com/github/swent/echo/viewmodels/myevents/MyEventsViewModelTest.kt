package com.github.swent.echo.viewmodels.myevents

import com.github.swent.echo.compose.map.MAP_CENTER
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.fakes.FakeAuthenticationService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime

class MyEventsViewModelTest {
    private val fakeAuthenticationService = FakeAuthenticationService()
    private val mockedRepository = mockk<Repository>(relaxed = true)
    private lateinit var MyEventsViewModel: MyEventsViewModel
    private val scheduler = TestCoroutineScheduler()
    private val userProfile =
        UserProfile(
            userId = "u0",
            name = "John Doe",
            semester = null,
            section = null,
            tags = setOf(),
            committeeMember = setOf(),
            associationsSubscriptions = setOf()
        )
    private val testEvent = Event(
        eventId = "wow",
        creator = EventCreator("a", ""),
        organizer = Association.EMPTY,
        title = "Bowling Event",
        description = "",
        location = Location("Location 1", MAP_CENTER.toLatLng()),
        startDate = ZonedDateTime.now(),
        endDate = ZonedDateTime.now(),
        tags = setOf(Tag("1", "wow")),
        participantCount = 5,
        maxParticipants = 8,
        imageId = 0
    )
    private var testJoinedEvents = listOf<Event>()
    private val mockedNetworkService = mockk<NetworkService>(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun init() {
        fakeAuthenticationService.userID = "u0"
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        coEvery { mockedRepository.getJoinedEvents("u0") } returns testJoinedEvents
        coEvery { mockedRepository.getAllEvents() } returns listOf(testEvent)
        coEvery { mockedRepository.joinEvent("u0", testEvent) } returns testJoinEvents()
        coEvery { mockedRepository.leaveEvent("u0", testEvent) } returns testLeaveEvents()
        runBlocking {
            MyEventsViewModel = MyEventsViewModel(mockedRepository, fakeAuthenticationService)
        }
        scheduler.runCurrent()
    }

    private fun testJoinEvents(): Boolean{
        testJoinedEvents += testEvent
        return true
    }

    private fun testLeaveEvents(): Boolean{
        testJoinedEvents -= testEvent
        return true
    }

    @Test
    fun testJoinOrLeaveEvent(){
        MyEventsViewModel.joinOrLeaveEvent(testEvent)
        assert(MyEventsViewModel.joinedEvents.value.contains(testEvent))
        MyEventsViewModel.joinOrLeaveEvent(testEvent)
        assert(!MyEventsViewModel.joinedEvents.value.contains(testEvent))
    }
}