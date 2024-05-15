package com.github.swent.echo.viewmodels.association

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
import io.mockk.every
import io.mockk.mockk
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class AssociationViewModelTest {
    private val fakeAuthenticationService = FakeAuthenticationService()
    private val mockedRepository = mockk<Repository>(relaxed = true)
    private lateinit var AssociationViewModel: AssociationViewModel
    private val scheduler = TestCoroutineScheduler()
    private val associationList =
        listOf(
            Association(
                associationId = "a",
                name = "Association A",
                description = "Description A",
            ),
            Association(
                associationId = "b",
                name = "Association B",
                description = "Description B",
            ),
            Association(
                associationId = "c",
                name = "Association C",
                description = "Description C",
            )
        )
    private val userProfile =
        UserProfile(
            userId = "u0",
            name = "John Doe",
            semester = null,
            section = null,
            tags = setOf(),
            committeeMember = associationList.subList(0, 1).toSet(),
            associationsSubscriptions = associationList.subList(0, 2).toSet()
        )
    private val eventList =
        listOf(
            Event(
                eventId = "wow",
                creator = EventCreator("a", ""),
                organizer = associationList[0],
                title = "Bowling Event",
                description = "",
                location = Location("Location 1", MAP_CENTER.toLatLng()),
                startDate = ZonedDateTime.now(),
                endDate = ZonedDateTime.now(),
                tags = setOf(Tag("1", "wow")),
                participantCount = 5,
                maxParticipants = 8,
                imageId = 0
            ),
            Event(
                eventId = "test",
                creator = EventCreator("a", ""),
                organizer = associationList[1],
                title = "Test Event",
                description = "",
                location = Location("Location 2", MAP_CENTER.toLatLng()),
                startDate = ZonedDateTime.now(),
                endDate = ZonedDateTime.now(),
                tags = setOf(Tag("2", "test")),
                participantCount = 5,
                maxParticipants = 8,
                imageId = 0
            ),
            Event(
                eventId = "wow2",
                creator = EventCreator("a", ""),
                organizer = associationList[2],
                title = "Bowling Event 2",
                description = "",
                location = Location("Location 3", MAP_CENTER.toLatLng()),
                startDate = ZonedDateTime.now(),
                endDate = ZonedDateTime.now(),
                tags = setOf(Tag("1", "wow")),
                participantCount = 5,
                maxParticipants = 8,
                imageId = 0
            )
        )
    private val mockedNetworkService = mockk<NetworkService>(relaxed = true)
    private val isOnline = MutableStateFlow(true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun init() {
        fakeAuthenticationService.userID = "u0"
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        coEvery { mockedRepository.getAllAssociations() } returns associationList
        coEvery { mockedRepository.getAllEvents() } returns eventList
        coEvery { mockedRepository.getUserProfile("u0") } returns userProfile
        every { mockedNetworkService.isOnline } returns isOnline
        coEvery { mockedRepository.getUserProfile("u0")?.associationsSubscriptions } returns
            userProfile.associationsSubscriptions
        coEvery { mockedRepository.getUserProfile("u0")?.committeeMember } returns
            userProfile.committeeMember
        runBlocking {
            AssociationViewModel =
                AssociationViewModel(
                    mockedRepository,
                    fakeAuthenticationService,
                    mockedNetworkService
                )
        }
        scheduler.runCurrent()
    }

    @Test
    fun onFollowAssociationChangedShouldWork() {
        AssociationViewModel.onFollowAssociationChanged(associationList[2])
        assert(AssociationViewModel.followedAssociations.value.contains(associationList[2]))
        AssociationViewModel.onFollowAssociationChanged(associationList[2])
        assert(!AssociationViewModel.followedAssociations.value.contains(associationList[2]))
    }

    @Test
    fun navigationBetweenPagesShouldWork() {
        AssociationViewModel.goTo(AssociationPage.DETAILS)
        assert(AssociationViewModel.currentAssociationPage.value == AssociationPage.DETAILS)
        AssociationViewModel.goTo(AssociationPage.SEARCH)
        assert(AssociationViewModel.currentAssociationPage.value == AssociationPage.SEARCH)
        AssociationViewModel.goTo(AssociationPage.MAINSCREEN)
        assert(AssociationViewModel.currentAssociationPage.value == AssociationPage.MAINSCREEN)
    }

    @Test
    fun backButtonShouldWork() {
        AssociationViewModel.goTo(AssociationPage.SEARCH)
        AssociationViewModel.goBack()
        assert(AssociationViewModel.currentAssociationPage.value == AssociationPage.MAINSCREEN)

        AssociationViewModel.goTo(AssociationPage.DETAILS)
        AssociationViewModel.goBack()
        assert(AssociationViewModel.currentAssociationPage.value == AssociationPage.MAINSCREEN)

        AssociationViewModel.goTo(AssociationPage.SEARCH)
        AssociationViewModel.goTo(AssociationPage.DETAILS)
        AssociationViewModel.goBack()
        assert(AssociationViewModel.currentAssociationPage.value == AssociationPage.SEARCH)
        AssociationViewModel.goBack()
        assert(AssociationViewModel.currentAssociationPage.value == AssociationPage.MAINSCREEN)
    }

    @Test
    fun setOverlayShouldWork() {
        AssociationViewModel.setOverlay(AssociationOverlay.NONE)
        assert(AssociationViewModel.overlay.value == AssociationOverlay.NONE)
        AssociationViewModel.setOverlay(AssociationOverlay.SEARCH)
        assert(AssociationViewModel.overlay.value == AssociationOverlay.SEARCH)
    }

    @Test
    fun setSearchedShouldWork() {
        AssociationViewModel.setSearched("Association A")
        assert(AssociationViewModel.searched.value == "Association A")
    }

    @Test
    fun associationEventsShouldReturnCorrectEvents() {
        val association = associationList[0]
        val events = AssociationViewModel.associationEvents(association)
        assert(events.size == 1)
        assert(events[0].organizer == association)
    }

    @Test
    fun filterAssociationsShouldReturnCorrectAssociations() {
        AssociationViewModel.setSearched("Association A")
        val associations = AssociationViewModel.filterAssociations()
        assert(associations.size == 1)
        assert(associations[0].name == "Association A")
    }

    @Test
    fun onAssociationToFilterChangedShouldWork() {
        AssociationViewModel.onAssociationToFilterChanged(associationList[0])
        assert(AssociationViewModel.eventsFilter.value.contains(associationList[0]))
        AssociationViewModel.onAssociationToFilterChanged(associationList[0])
        assert(!AssociationViewModel.eventsFilter.value.contains(associationList[0]))
    }
}
