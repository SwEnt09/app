package com.github.swent.echo.compose.event

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
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
import com.github.swent.echo.viewmodels.event.EventStatus
import com.github.swent.echo.viewmodels.event.EventViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.ZonedDateTime
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineScheduler
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class EditEventScreenTest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

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

    private val mockedRepository = mockk<Repository>(relaxed = true)
    private val mockedAuthenticationService = mockk<AuthenticationService>(relaxed = true)
    private lateinit var eventViewModel: EventViewModel
    private val savedEventId = SavedStateHandle(mapOf(Pair("eventId", TEST_EVENT.eventId)))
    private val mockedNetworkService = mockk<NetworkService>(relaxed = true)

    val scheduler = TestCoroutineScheduler()
    val mockedNavActions = mockk<NavigationActions>(relaxed = true)

    @Before
    fun init() {
        every { mockedNetworkService.isOnline } returns MutableStateFlow(true)
        every { mockedAuthenticationService.getCurrentUserID() } returns TEST_EVENT.eventId
        every { mockedNavActions.navigateTo(any()) } returns Unit
        every { mockedNavActions.goBack() } returns Unit
        coEvery { mockedRepository.getEvent(TEST_EVENT.eventId) } returns TEST_EVENT
        eventViewModel =
            EventViewModel(
                mockedRepository,
                mockedAuthenticationService,
                savedEventId,
                mockedNetworkService
            )
        hiltRule.inject()
        composeTestRule.activity.setContent {
            EditEventScreen(eventViewModel = eventViewModel, navigationActions = mockedNavActions)
        }
    }

    @Test
    fun backButtonTriggerNavigateBack() {
        composeTestRule.onNodeWithTag("Back-button").performClick()
        verify { mockedNavActions.goBack() }
    }

    @Test
    fun saveButtonTriggerNavigateToMap() {
        eventViewModel.setEvent(TEST_EVENT)
        composeTestRule.onNodeWithTag("Save-button").performScrollTo().performClick()
        scheduler.runCurrent()
        composeTestRule.waitForIdle()
        verify { mockedNavActions.navigateTo(Routes.MAP) }
    }

    @Test
    fun eventScreenContainsExistingEvent() {
        scheduler.runCurrent()
        assertTrue(eventViewModel.event.value == TEST_EVENT)
        assertTrue(eventViewModel.status.value == EventStatus.Saved)
    }

    @Test
    fun deleteButtonWithConfirmNavigateToMap() {
        composeTestRule.onNodeWithTag("delete-button").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("delete-confirm").performClick()
        scheduler.runCurrent()
        composeTestRule.waitForIdle()
        verify { mockedNavActions.navigateTo(Routes.MAP) }
    }

    @Test
    fun deleteButtonWithCancelDoesNotNavigateToMap() {
        composeTestRule.onNodeWithTag("delete-button").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("delete-cancel").performClick()
        scheduler.runCurrent()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("Save-button").assertIsDisplayed()
    }
}
