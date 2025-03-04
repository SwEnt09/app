package com.github.swent.echo.viewmodels.association

import com.github.swent.echo.compose.map.MAP_CENTER
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.AssociationHeader
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.model.toAssociationHeader
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.data.repository.RepositoryStoreWhileNoInternetException
import com.github.swent.echo.fakes.FakeAuthenticationService
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AssociationViewModelTest {
    private val fakeAuthenticationService = FakeAuthenticationService()
    private val mockedRepository = mockk<Repository>(relaxed = true)
    private lateinit var associationViewModel: AssociationViewModel
    private val scheduler = TestCoroutineScheduler()
    private val associationList =
        listOf(
            Association(
                associationId = "a",
                name = "Association A",
                description = "Description A",
                url = "url A",
                setOf(Tag("tagId", "tagDescription")),
            ),
            Association(
                associationId = "b",
                name = "Association B",
                description = "Description B",
                url = "url B",
                setOf(Tag.EMPTY),
            ),
            Association(
                associationId = "c",
                name = "Association C",
                description = "Description C",
                url = "url C",
                setOf(),
            )
        )
    private val userProfile =
        UserProfile(
            userId = "u0",
            name = "John Doe",
            semester = null,
            section = null,
            tags = setOf(),
            committeeMember = associationList.subList(0, 1).toAssociationHeader().toSet(),
            associationsSubscriptions = associationList.subList(0, 2).toAssociationHeader().toSet()
        )
    private val eventList =
        listOf(
            Event(
                eventId = "wow",
                creator = EventCreator("a", ""),
                organizer = AssociationHeader.fromAssociation(associationList[0]),
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
                organizer = AssociationHeader.fromAssociation(associationList[1]),
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
                organizer = AssociationHeader.fromAssociation(associationList[2]),
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
            associationViewModel =
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
        associationViewModel.onFollowAssociationChanged(associationList[2])
        assert(associationViewModel.followedAssociations.value.contains(associationList[2]))
        associationViewModel.onFollowAssociationChanged(associationList[2])
        assert(!associationViewModel.followedAssociations.value.contains(associationList[2]))
        coEvery { mockedRepository.setUserProfile(any()) } throws
            RepositoryStoreWhileNoInternetException("test")
        associationViewModel.onFollowAssociationChanged(associationList[2])
        scheduler.runCurrent()
        assert(!associationViewModel.followedAssociations.value.contains(associationList[2]))
        assert(associationViewModel.status.value is AssociationStatus.Error)
        associationViewModel.resetErrorState()
        assert(associationViewModel.status.value is AssociationStatus.Okay)
    }

    @Test
    fun setSearchedShouldWork() {
        associationViewModel.setSearched("Association A")
        assert(associationViewModel.searched.value == "Association A")
    }

    @Test
    fun associationEventsShouldReturnCorrectEvents() {
        val association = associationList[0]
        val events = associationViewModel.associationEvents(association)
        assert(events.size == 1)
        assert(events[0].organizer == AssociationHeader.fromAssociation(association))
    }

    @Test
    fun filterAssociationsShouldReturnCorrectAssociations() {
        associationViewModel.setSearched("Association A")
        val associations =
            associationViewModel.filterAssociations(associationViewModel.showAllAssociations.value)
        assert(associations.size == 1)
        assert(associations[0].name == "Association A")
    }

    @Test
    fun refreshEventsTest() = runBlocking {
        // Arrange
        val oldEvents = associationViewModel.associationEvents(associationList[0])
        val newEvents =
            listOf(
                Event(
                    eventId = "newEvent",
                    creator = EventCreator("a", ""),
                    organizer = AssociationHeader.fromAssociation(associationList[0]),
                    title = "New Event",
                    description = "",
                    location = Location("Location 2", MAP_CENTER.toLatLng()),
                    startDate = ZonedDateTime.now(),
                    endDate = ZonedDateTime.now(),
                    tags = setOf(Tag("1", "new")),
                    participantCount = 5,
                    maxParticipants = 8,
                    imageId = 0
                )
            )
        coEvery { mockedRepository.getAllEvents() } returns newEvents

        // Act
        associationViewModel.refreshEvents()
        scheduler.runCurrent() // To ensure all launched coroutines have completed

        // Assert
        coVerify { mockedRepository.getAllEvents() }
        Assert.assertNotEquals(
            oldEvents,
            associationViewModel.associationEvents(associationList[0])
        )
        Assert.assertEquals(newEvents, associationViewModel.associationEvents(associationList[0]))
    }
}
