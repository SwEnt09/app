package com.github.swent.echo.compose.components

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.MainActivity
import com.github.swent.echo.data.model.AssociationHeader
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import java.time.ZoneId
import java.time.ZonedDateTime
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EventInfoSheetTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    private var joinClicked = 0
    private var dismissed = 0
    private var extended = 0
    private var modified = 0

    private fun setUp(peopleMax: Int = 0, canModifyEvent: Boolean = false) {
        composeTestRule.activity.setContent {
            joinClicked = 0
            dismissed = 0

            val event =
                Event(
                    eventId = "1",
                    creator = EventCreator("1", "Event Creator"),
                    organizer = AssociationHeader("1", "Event Organization"),
                    title = "Event Title",
                    description = "Event Description",
                    location = Location("", 0.0, 0.0),
                    startDate = ZonedDateTime.of(2024, 6, 18, 18, 15, 0, 0, ZoneId.systemDefault()),
                    endDate = ZonedDateTime.of(2024, 1, 1, 22, 0, 0, 0, ZoneId.systemDefault()),
                    tags = setOf(),
                    participantCount = 0,
                    maxParticipants = peopleMax,
                    imageId = android.R.drawable.ic_menu_help
                )

            EventInfoSheet(
                event = event,
                onDismiss = { dismissed++ },
                onFullyExtended = { extended++ },
                canModifyEvent = canModifyEvent,
                onModifyEvent = { modified++ },
                isOnline = true,
                refreshEvents = {}
            )
        }
    }

    @Test
    fun shouldShowEventTitle() {
        setUp()
        composeTestRule.onNodeWithText("Event Title").assertExists()
    }

    @Test
    fun shouldShowEventDescription() {
        setUp()
        composeTestRule.onNodeWithText("Event Description").assertExists()
    }

    @Test
    fun shouldShowEventDateTime() {
        setUp()
        composeTestRule.onNodeWithText("18/06\n" + "18:15").assertExists()
    }

    @Test
    fun shouldShowEventOrganization() {
        setUp()
        composeTestRule.onNodeWithText("Event Organization").assertExists()
    }

    @Test
    fun shouldShowJoinButton() {
        setUp()
        composeTestRule.onNodeWithText("Join Event").assertExists()
    }

    @Test
    fun shouldShowPeopleButton() {
        setUp()
        composeTestRule.onNodeWithText("0").assertExists()
    }

    @Test
    fun shouldShowPeopleIcon() {
        setUp()
        composeTestRule.onNodeWithTag("people_icon").assertExists()
    }

    @Test
    fun shouldShowPeopleCorrectlyWhenMaxPeopleIsNotZero() {
        setUp(5)
        composeTestRule.onNodeWithText("0/5").assertExists()
    }

    @Test
    fun shouldNotShowModifyButtonWhenUserCannotModifyEvent() {
        setUp(canModifyEvent = false)
        composeTestRule.onNodeWithTag("modify_button").assertDoesNotExist()
    }

    @Test
    fun shouldShowModifyButtonWhenUserCanModifyEvent() {
        setUp(canModifyEvent = true)
        composeTestRule.onNodeWithTag("modify_button").assertExists()

        composeTestRule.onNodeWithTag("modify_button").performClick()
        assertThat(modified, equalTo(1))
    }

    @Test
    fun shouldShowModifyButtonLabel() {
        setUp(canModifyEvent = true)
        composeTestRule.onNodeWithText("Modify Event").assertExists()
    }
}
