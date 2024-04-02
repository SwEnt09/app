package com.github.swent.echo.compose.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import java.util.Date
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

class EventInfoSheetTest {

    @get:Rule val composeTestRule = createComposeRule()

    private var joinClicked = 0
    private var peopleClicked = 0
    private var dismissed = 0
    private var extended = 0

    private fun setUp(peopleMax: Int = 0) {
        composeTestRule.setContent {
            joinClicked = 0
            peopleClicked = 0
            dismissed = 0

            val event =
                Event(
                    eventId = "1",
                    creatorId = "1",
                    organizerId = "1",
                    title = "Event Title",
                    description = "Event Description",
                    location = Location("", 0.0, 0.0),
                    startDate = Date(2024, 5, 18, 18, 15),
                    endDate = Date(2024, 1, 1, 22, 0),
                    tags = setOf()
                )

            EventInfoSheet(
                event = event,
                hostName = "Event Organization",
                eventImage = android.R.drawable.ic_menu_help,
                eventPeople = 0,
                eventPeopleMax = peopleMax,
                onJoinButtonPressed = { joinClicked++ },
                onShowPeopleButtonPressed = { peopleClicked++ },
                onDismiss = { dismissed++ },
                onFullyExtended = { extended++ }
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
    fun shouldCallJoinButtonPressedWhenJoinButtonClicked() {
        setUp()
        composeTestRule.onNodeWithTag("join_button").performClick()
        assertThat(joinClicked, equalTo(1))
    }

    @Test
    fun shouldCallShowPeopleButtonPressedWhenPeopleButtonClicked() {
        setUp()
        composeTestRule.onNodeWithTag("people_button").performClick()
        assertThat(peopleClicked, equalTo(1))
    }

    @Test
    fun shouldShowEventImage() {
        setUp()
        composeTestRule.onNodeWithTag("event_image").assertExists()
    }

    @Test
    fun shouldShowPeopleCorrectlyWhenMaxPeopleIsNotZero() {
        setUp(5)
        composeTestRule.onNodeWithText("0/5").assertExists()
    }
}
