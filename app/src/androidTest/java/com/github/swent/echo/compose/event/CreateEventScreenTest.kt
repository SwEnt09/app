package com.github.swent.echo.compose.event

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.SavedStateHandle
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.event.EventViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateEventScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    private val mockedRepository = mockk<Repository>(relaxed = true)
    private val mockedAuthenticationService = mockk<AuthenticationService>(relaxed = true)
    private lateinit var eventViewModel: EventViewModel
    private val savedEventId = SavedStateHandle(mapOf())

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
        every { mockedNavActions.navigateTo(any()) } returns Unit
        composeTestRule.setContent {
            CreateEventScreen(eventViewModel = eventViewModel, navigationActions = mockedNavActions)
        }
        composeTestRule.onNodeWithTag("Save-button").performScrollTo().performClick()
        verify { mockedNavActions.navigateTo(Routes.MAP) }
    }
}
