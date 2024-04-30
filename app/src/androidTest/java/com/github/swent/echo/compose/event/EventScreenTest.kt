package com.github.swent.echo.compose.event

import androidx.activity.compose.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.requestFocus
import androidx.lifecycle.SavedStateHandle
import com.github.swent.echo.MainActivity
import com.github.swent.echo.R
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.viewmodels.event.EventViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class EventScreenTest {

    private val STRING_1 = "a test string"
    private val STRING_2 = "another test string"
    private val STRING_3 = "a third test string"

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val mockedRepository = mockk<Repository>(relaxed = true)
    private val mockedAuthenticationService = mockk<AuthenticationService>(relaxed = true)
    private lateinit var eventViewModel: EventViewModel
    private val savedEventId = SavedStateHandle(mapOf())

    @Before
    fun init() {
        eventViewModel = EventViewModel(mockedRepository, mockedAuthenticationService, savedEventId)
        hiltRule.inject()
    }

    private fun setCompose(eventViewModel: EventViewModel) {
        composeTestRule.activity.setContent {
            EventScreen(
                stringResource(R.string.create_event_screen_title),
                onEventSaved = {},
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

    @Test
    fun clickOnTagDeleteIt() {
        setCompose(eventViewModel)
        val tag = Tag("test", "test")
        eventViewModel.setEvent(Event.EMPTY.copy(tags = setOf(tag)))
        val tagButton = composeTestRule.onNodeWithTag("test-tag-button")
        tagButton.assertIsDisplayed()
        tagButton.performClick()
        tagButton.assertDoesNotExist()
    }

    @Test
    fun selectTagAddItToTagList() {
        setCompose(eventViewModel)
        val addTagButton = composeTestRule.onNodeWithTag("add-tag-button")
        val tag1 = Tag("1", "Sport", Repository.ROOT_TAG_ID) // first tag of simple repository
        val firstTag = composeTestRule.onNodeWithTag("${tag1.name}-select-button")
        addTagButton.performClick()
        composeTestRule.onNodeWithTag("tag-dialog").assertIsDisplayed()
        firstTag.performClick()
        composeTestRule.onNodeWithTag("tag-dialog").assertDoesNotExist()
        assertTrue(eventViewModel.event.value.tags.contains(tag1))
    }

    @Test
    fun changeEventParticipantLimitChangeItInViewModel() {
        setCompose(eventViewModel)
        val nbParticipants = 500
        composeTestRule
            .onNodeWithTag("nb-participant-field")
            .performTextReplacement(nbParticipants.toString())
        composeTestRule.onNodeWithTag("Title-field").requestFocus()
        composeTestRule.onNodeWithTag("nb-participant-field").assertIsNotFocused()
        Assert.assertTrue(eventViewModel.event.value.maxParticipants == nbParticipants)
    }

    @Test
    fun inputLettersInParticipantLimitInputFieldDoesntChangeValue() {
        setCompose(eventViewModel)
        val NotANumber = "aString"
        val maxParticipants = eventViewModel.event.value.maxParticipants
        composeTestRule.onNodeWithTag("nb-participant-field").performTextReplacement(NotANumber)
        composeTestRule.onNodeWithTag("Title-field").requestFocus()
        composeTestRule.onNodeWithTag("nb-participant-field").assertIsNotFocused()
        assertEquals(maxParticipants, eventViewModel.event.value.maxParticipants)
    }
}
