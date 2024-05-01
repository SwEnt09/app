package com.github.swent.echo.compose.event

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.event.EventViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateEventScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    private val mockedRepository = mockk<Repository>(relaxed = true)
    private val mockedAuthenticationService = mockk<AuthenticationService>(relaxed = true)
    private lateinit var eventViewModel: EventViewModel
    private val savedEventId = SavedStateHandle(mapOf())
    private val TEST_EVENT =
        Event(
            eventId = "testid",
            creator = EventCreator("testid", "testname"),
            organizer = Association("testid", "testname", "testdesc"),
            title = "test title",
            description = "test description",
            location = Location("test location", 100.0, 100.0),
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            tags = setOf(Tag("1", "tag1")),
            0,
            15,
            0
        )

    @Before
    fun init() {
        eventViewModel = EventViewModel(mockedRepository, mockedAuthenticationService, savedEventId)
    }

    @Test
    fun backButtonTriggerNavigateBack() {
        val mockedNavActions = mockk<NavigationActions>(relaxed = true)
        every { mockedNavActions.goBack() } returns Unit
        composeTestRule.setContent {
            CreateEventScreen(eventViewModel = eventViewModel, navigationActions = mockedNavActions)
        }
        composeTestRule.onNodeWithTag("Back-button").performClick()
        verify { mockedNavActions.goBack() }
    }

    @Test
    fun saveButtonTriggerNavigateToMap() {
        val mockedNavActions = mockk<NavigationActions>(relaxed = true)
        val scheduler = TestCoroutineScheduler()
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        every { mockedNavActions.navigateTo(any()) } returns Unit
        composeTestRule.setContent {
            CreateEventScreen(eventViewModel = eventViewModel, navigationActions = mockedNavActions)
        }
        eventViewModel.setEvent(TEST_EVENT)
        composeTestRule.onNodeWithTag("Save-button").performScrollTo().performClick()
        scheduler.runCurrent()
        composeTestRule.waitForIdle()
        verify { mockedNavActions.navigateTo(Routes.MAP) }
    }
}
