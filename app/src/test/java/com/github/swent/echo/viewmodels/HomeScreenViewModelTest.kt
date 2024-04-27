package com.github.swent.echo.viewmodels

import com.github.swent.echo.compose.map.MAP_CENTER
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.fakes.FakeAuthenticationService
import io.mockk.mockk
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HomeScreenViewModelTest {

    private val fakeAuthenticationService = FakeAuthenticationService()
    private val mockedRepository = mockk<Repository>(relaxed = true)
    private lateinit var homeScreenViewModel: HomeScreenViewModel
    private val scheduler = TestCoroutineScheduler()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun init() {
        fakeAuthenticationService.userID = "u0"
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        runBlocking {
            homeScreenViewModel = HomeScreenViewModel(mockedRepository, fakeAuthenticationService)
        }
        scheduler.runCurrent()
    }

    @Test
    fun overlayTest() {
        assertEquals(homeScreenViewModel.overlay.value, Overlay.NONE)
        homeScreenViewModel.setOverlay(Overlay.EVENT_INFO_SHEET)
        assertEquals(homeScreenViewModel.overlay.value, Overlay.EVENT_INFO_SHEET)
        homeScreenViewModel.clearOverlay()
        assertEquals(homeScreenViewModel.overlay.value, Overlay.NONE)
    }

    @Test
    fun modeTest() {
        assertEquals(homeScreenViewModel.mode.value, MapOrListMode.MAP)
        homeScreenViewModel.switchMode()
        assertEquals(homeScreenViewModel.mode.value, MapOrListMode.LIST)
        homeScreenViewModel.switchMode()
        assertEquals(homeScreenViewModel.mode.value, MapOrListMode.MAP)
    }

    @Test
    fun filtersContainerTest() {
        assertEquals(homeScreenViewModel.filtersContainer.value.searchEntry, "")
        homeScreenViewModel.onSearchEntryChanged("test")
        assertEquals(homeScreenViewModel.filtersContainer.value.searchEntry, "test")
        homeScreenViewModel.resetFiltersContainer()
        assertEquals(homeScreenViewModel.filtersContainer.value.searchEntry, "")
    }

    @Test
    fun filtersContainerRefreshTest() {
        assertEquals(homeScreenViewModel.filtersContainer.value.searchEntry, "")
        homeScreenViewModel.onSearchEntryChanged("test")
        assertEquals(homeScreenViewModel.filtersContainer.value.searchEntry, "test")
        homeScreenViewModel.refreshFiltersContainer()
        assertEquals(homeScreenViewModel.displayEventList.value.size, 0)

        homeScreenViewModel.onSearchEntryChanged("Dungeons and Dragons")
        // /!\ this is a tag name that needs to be changed when the
        // repository is linked to the viewModel (mock the rep then)
        assertEquals(homeScreenViewModel.filtersContainer.value.searchEntry, "Dungeons and Dragons")
        homeScreenViewModel.refreshFiltersContainer()
        assertEquals(homeScreenViewModel.displayEventList.value.size, 1)
    }

    @Test
    fun profileNameTest() {
        assertEquals(
            homeScreenViewModel.profileName.value,
            "John Doe"
        ) // /!\ this is a placeholder that needs to be changed when the repository is linked to the
        // viewModel (mock the rep then)
        assertEquals(homeScreenViewModel.profileClass.value, "IN - BA6")
    }

    @Test
    fun displayEventInfoTest() {
        assertEquals(homeScreenViewModel.displayEventInfo.value, null)
        homeScreenViewModel.onEventSelected(
            Event(
                eventId = "a",
                creator = EventCreator("a", ""),
                organizer = Association("a", "a", ""),
                title = "Bowling Event",
                description = "",
                location = Location("Location 1", MAP_CENTER.toGeoPoint()),
                startDate = ZonedDateTime.now(),
                endDate = ZonedDateTime.now(),
                tags = setOf(Tag("64", "Bowling"), Tag("1", "Sport")),
                participantCount = 5,
                maxParticipants = 8,
                imageId = 0
            )
        )
        assertEquals(homeScreenViewModel.displayEventInfo.value?.title, "Bowling Event")
    }

    @Test
    fun callbacksTest() {
        val v = homeScreenViewModel.filtersContainer.value.epflChecked
        homeScreenViewModel.onEpflCheckedSwitch()
        assertEquals(homeScreenViewModel.filtersContainer.value.epflChecked, !v)

        val v2 = homeScreenViewModel.filtersContainer.value.sectionChecked
        homeScreenViewModel.onSectionCheckedSwitch()
        assertEquals(homeScreenViewModel.filtersContainer.value.sectionChecked, !v2)

        val v3 = homeScreenViewModel.filtersContainer.value.classChecked
        homeScreenViewModel.onClassCheckedSwitch()
        assertEquals(homeScreenViewModel.filtersContainer.value.classChecked, !v3)

        val v4 = homeScreenViewModel.filtersContainer.value.pendingChecked
        homeScreenViewModel.onPendingCheckedSwitch()
        assertEquals(homeScreenViewModel.filtersContainer.value.pendingChecked, !v4)

        val v5 = homeScreenViewModel.filtersContainer.value.confirmedChecked
        homeScreenViewModel.onConfirmedCheckedSwitch()
        assertEquals(homeScreenViewModel.filtersContainer.value.confirmedChecked, !v5)

        val v6 = homeScreenViewModel.filtersContainer.value.fullChecked
        homeScreenViewModel.onFullCheckedSwitch()
        assertEquals(homeScreenViewModel.filtersContainer.value.fullChecked, !v6)
    }
}
