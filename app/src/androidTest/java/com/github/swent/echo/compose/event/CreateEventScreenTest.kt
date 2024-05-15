package com.github.swent.echo.compose.event

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.SavedStateHandle
import com.github.swent.echo.MainActivity
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.event.EventViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class CreateEventScreenTest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val mockedRepository = mockk<Repository>(relaxed = true)
    private val mockedAuthenticationService = mockk<AuthenticationService>(relaxed = true)
    private lateinit var eventViewModel: EventViewModel
    private val savedEventId = SavedStateHandle(mapOf())
    private val mockedNetworkService = mockk<NetworkService>(relaxed = true)
    private val TEST_EVENT =
        Event(
            eventId = "testid",
            creator = EventCreator("testid", "testname"),
            organizer = Association("testid", "testname", "testdesc"),
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

    @Before
    fun init() {
        every { mockedNetworkService.isOnline } returns MutableStateFlow(true)
        every { mockedAuthenticationService.getCurrentUserID() } returns TEST_EVENT.eventId
        coEvery { mockedRepository.getEvent(TEST_EVENT.eventId) } returns TEST_EVENT
        hiltRule.inject()
        eventViewModel =
            EventViewModel(
                mockedRepository,
                mockedAuthenticationService,
                savedEventId,
                mockedNetworkService
            )
    }

    @Test
    fun backButtonTriggerNavigateBack() {
        val mockedNavActions = mockk<NavigationActions>(relaxed = true)
        every { mockedNavActions.goBack() } returns Unit
        composeTestRule.activity.setContent {
            CreateEventScreen(eventViewModel = eventViewModel, navigationActions = mockedNavActions)
        }
        composeTestRule.onNodeWithTag("Back-button").performClick()
        verify { mockedNavActions.goBack() }
    }

    @Test
    fun saveButtonTriggerNavigateToMap() {
        val mockedNavActions = mockk<NavigationActions>(relaxed = true)
        every { mockedNavActions.navigateTo(any()) } returns Unit
        composeTestRule.activity.setContent {
            CreateEventScreen(eventViewModel = eventViewModel, navigationActions = mockedNavActions)
        }
        eventViewModel.setEvent(TEST_EVENT)
        composeTestRule.onNodeWithTag("Save-button").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        verify { mockedNavActions.navigateTo(Routes.MAP) }
    }
}
