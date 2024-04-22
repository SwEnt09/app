package com.github.swent.echo.compose.event

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.SavedStateHandle
import com.github.swent.echo.R
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.viewmodels.event.EventViewModel
import io.mockk.mockk
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
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
    private val savedEventId = SavedStateHandle(mapOf())

    @Before
    fun init() {
        eventViewModel = EventViewModel(mockedRepository, mockedAuthenticationService, savedEventId)
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
        node.assertTextEquals(eventViewModel.event.value.title)
    }

    @Test
    fun descriptionInputIsCorrect() {
        setCompose(eventViewModel)
        val node = composeTestRule.onNodeWithTag("Description-field")
        node.performTextInput(STRING_1)
        node.assertTextContains(STRING_1)
        node.assertTextEquals(eventViewModel.event.value.description)
    }

    @Test
    fun locationInputIsCorrect() {
        setCompose(eventViewModel)
        val node = composeTestRule.onNodeWithTag("Location-field")
        node.performTextInput(STRING_1)
        node.assertTextContains(STRING_1)
        node.assertTextEquals(eventViewModel.event.value.location.name)
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

    @Ignore // working locally but not on the CI
    @Test
    fun startDateButtonDisplayDateDialog() {
        setCompose(eventViewModel)
        val button = composeTestRule.onNodeWithTag("Start date-button")
        val dialog = composeTestRule.onNodeWithTag("Start date-dialog")
        dialog.assertDoesNotExist()
        button.performScrollTo().performClick()
        dialog.assertIsDisplayed()
    }

    @Test
    fun startTimeButtonDisplayTimeDialog() {
        setCompose(eventViewModel)
        val button = composeTestRule.onNodeWithTag("Start date-time-button")
        val dialog = composeTestRule.onNodeWithTag("Start date-time-dialog")
        val dialogButton = composeTestRule.onNodeWithTag("Start date-time-dialog-button")
        dialog.assertDoesNotExist()
        button.performScrollTo().performClick()
        dialog.assertIsDisplayed()
        dialogButton.performClick()
        dialog.assertDoesNotExist()
    }

    @Test
    fun changeMultipleFieldsChangeThemInTheViewModel() {
        setCompose(eventViewModel)
        composeTestRule.onNodeWithTag("Title-field").performTextInput(STRING_1)
        composeTestRule.onNodeWithTag("Description-field").performTextInput(STRING_2)
        composeTestRule.onNodeWithTag("Location-field").performTextInput(STRING_3)
        val event = eventViewModel.event
        Assert.assertTrue(event.value.title == STRING_1)
        Assert.assertTrue(event.value.description == STRING_2)
        Assert.assertTrue(event.value.location.name == STRING_3)
    }

    @Test
    fun changeEventLocationDialogValueChangeItInViewModel() {
        setCompose(eventViewModel)
        val testLocation = eventViewModel.event.value.location.copy(lat = 15.5, long = 15.5)
        composeTestRule.onNodeWithTag("Location-button").performClick()
        val latInput = composeTestRule.onNodeWithTag("event_latitude_text_field")
        val longInput = composeTestRule.onNodeWithTag("event_longitude_text_field")
        val okButton = composeTestRule.onNodeWithTag("event_location_confirm_button")
        latInput.performTextReplacement(testLocation.lat.toString())
        longInput.performTextReplacement(testLocation.long.toString())
        okButton.performClick()
        Assert.assertTrue(eventViewModel.event.value.location == testLocation)
    }
}
