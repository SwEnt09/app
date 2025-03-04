package com.github.swent.echo.compose.event

import androidx.activity.compose.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
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
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.data.repository.SimpleRepository
import com.github.swent.echo.data.repository.SimpleRepository.Companion.ROOT_TAG_ID
import com.github.swent.echo.viewmodels.event.EventViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
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
    private val mockedNetworkService = mockk<NetworkService>(relaxed = true)
    private val isOnline = MutableStateFlow(true)
    private val categoryName = "Category"

    @Before
    fun init() {
        every { mockedNetworkService.isOnline } returns isOnline
        eventViewModel =
            EventViewModel(
                mockedRepository,
                mockedAuthenticationService,
                savedEventId,
                mockedNetworkService
            )
        hiltRule.inject()
    }

    private fun setCompose(eventViewModel: EventViewModel, canDelete: Boolean = false) {
        composeTestRule.activity.setContent {
            EventScreen(
                stringResource(R.string.create_event),
                canDelete = canDelete,
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
        val node = composeTestRule.onNodeWithTag("Name of place-field")
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
        button.performScrollTo()
        button.assertIsDisplayed()
        button.performClick()
        dialog.assertExists()
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
        composeTestRule.onNodeWithTag("Name of place-field").performTextInput(STRING_3)
        val event = eventViewModel.event
        Assert.assertTrue(event.value.title == STRING_1)
        Assert.assertTrue(event.value.description == STRING_2)
        Assert.assertTrue(event.value.location.name == STRING_3)
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
        runBlocking {
            eventViewModel =
                EventViewModel(
                    SimpleRepository(mockedAuthenticationService),
                    mockedAuthenticationService,
                    savedEventId,
                    mockedNetworkService
                )
        }
        setCompose(eventViewModel)
        val addTagButton = composeTestRule.onNodeWithTag("add-tag-button-$categoryName")
        val tag1 = Tag("1", "Sport", ROOT_TAG_ID) // first tag of simple repository
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

    @Test
    fun saveEventWithEmptyTitleShouldDisplayError() {
        setCompose(eventViewModel)
        composeTestRule.onNodeWithTag("Save-button").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("snackbar").assertIsDisplayed()
    }

    @Test
    fun eventMaxParticipantIsCorrectOnEdit() {
        val maxParticipants = 50
        coEvery { mockedRepository.getEvent(any()) } returns
            Event.EMPTY.copy(maxParticipants = maxParticipants)
        every { mockedAuthenticationService.getCurrentUserID() } returns "testUser"
        savedEventId.set("eventId", "testEvent")
        eventViewModel =
            EventViewModel(
                mockedRepository,
                mockedAuthenticationService,
                savedEventId,
                mockedNetworkService
            )
        setCompose(eventViewModel, true)
        composeTestRule.waitForIdle()
        composeTestRule
            .onNodeWithTag("nb-participant-field")
            .assertTextContains(maxParticipants.toString())
    }

    @Test
    fun saveAndDeleteButtonsAvailableWhenOnline() {
        setCompose(eventViewModel, true)
        composeTestRule.onNodeWithTag("Save-button").assertIsEnabled()
        composeTestRule.onNodeWithTag("delete-button").assertIsEnabled()
        composeTestRule.onNodeWithTag("add-tag-button-$categoryName").assertIsEnabled()
    }

    @Test
    fun saveAndDeleteButtonsNotAvailableWhenOffline() {
        isOnline.value = false
        setCompose(eventViewModel, true)
        composeTestRule.onNodeWithTag("Save-button").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("delete-button").assertIsNotEnabled()
        composeTestRule.onNodeWithTag("add-tag-button-$categoryName").assertIsNotEnabled()
    }

    @Test
    fun tagSectionAndSemesterButtonExist() {
        setCompose(eventViewModel)
        composeTestRule.onNodeWithTag("add-tag-button-Section").assertHasClickAction()
        composeTestRule.onNodeWithTag("add-tag-button-Semester").assertHasClickAction()
    }
}
