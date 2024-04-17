package com.github.swent.echo.compose.event

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.github.swent.echo.R
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.viewmodels.event.EventViewModel
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EventScreenTest {

    private val STRING_1 = "a test string"
    private val STRING_2 = "another test string"
    private val STRING_3 = "a third test string"

    @get:Rule val composeTestRule = createComposeRule()

    private val mockedRepository = mockk<Repository>(relaxed = true)
    private val mockedAuthenticationService = mockk<AuthenticationService>(relaxed = true)
    private lateinit var eventViewModel: EventViewModel

    @Before
    fun init() {
        eventViewModel = EventViewModel(mockedRepository, mockedAuthenticationService)
    }

    private fun setCompose(eventViewModel: EventViewModel) {
        composeTestRule.setContent {
            EventScreen(
                stringResource(R.string.create_event_screen_title),
                onEventSaveButtonPressed = {},
                onEventBackButtonPressed = {},
                eventViewModel = eventViewModel
            )
        }
    }

    @Test
    fun titleInputIsCorrect() {
        setCompose(eventViewModel)
        val node = composeTestRule.onNodeWithTag("Title-field")
        node.performTextInput(STRING_1)
        node.assertTextContains(STRING_1)
        node.assertTextEquals(eventViewModel.getEvent().value.title)
    }

    @Test
    fun descriptionInputIsCorrect() {
        setCompose(eventViewModel)
        val node = composeTestRule.onNodeWithTag("Description-field")
        node.performTextInput(STRING_1)
        node.assertTextContains(STRING_1)
        node.assertTextEquals(eventViewModel.getEvent().value.description)
    }

    @Test
    fun locationInputIsCorrect() {
        setCompose(eventViewModel)
        val node = composeTestRule.onNodeWithTag("Location-field")
        node.performTextInput(STRING_1)
        node.assertTextContains(STRING_1)
        node.assertTextEquals(eventViewModel.getEvent().value.location.name)
    }

    @Test
    fun locationButtonDisplaysDialog() {
        setCompose(eventViewModel)
        val button = composeTestRule.onNodeWithTag("Location-button")
        val dialog = composeTestRule.onNodeWithTag("Location-dialog")
        dialog.assertDoesNotExist()
        button.performClick()
        dialog.assertIsDisplayed()
    }

    @Test
    fun startDateButtonDisplayDateDialog() {
        setCompose(eventViewModel)
        val button = composeTestRule.onNodeWithTag("Start date-button")
        val dialog = composeTestRule.onNodeWithTag("Start date-dialog")
        dialog.assertDoesNotExist()
        button.performClick()
        dialog.assertIsDisplayed()
    }

    @Test
    fun changeMultipleFieldsChangeThemInTheViewModel() {
        setCompose(eventViewModel)
        composeTestRule.onNodeWithTag("Title-field").performTextInput(STRING_1)
        composeTestRule.onNodeWithTag("Description-field").performTextInput(STRING_2)
        composeTestRule.onNodeWithTag("Location-field").performTextInput(STRING_3)
        val event = eventViewModel.getEvent()
        Assert.assertTrue(event.value.title == STRING_1)
        Assert.assertTrue(event.value.description == STRING_2)
        Assert.assertTrue(event.value.location.name == STRING_3)
    }
}
