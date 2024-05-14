package com.github.swent.echo.compose.components

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.SAMPLE_EVENTS
import java.time.format.DateTimeFormatter
import kotlin.math.min
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListDrawerTest {
    @get:Rule val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        composeTestRule.setContent { ListDrawer(SAMPLE_EVENTS, "", "", true) }
    }

    @Test
    fun testListDrawerExists() {
        composeTestRule.onNodeWithTag("list_drawer").assertExists()
    }

    @Test
    fun testAllEventsDisplayedCorrectly() {
        // Check max 5 events to avoid interminable test when we will have a lot of events
        for (i in 0..min(SAMPLE_EVENTS.size - 1, 4)) {
            composeTestRule
                .onNodeWithTag("list_event_item_${SAMPLE_EVENTS[i].eventId}")
                .assertExists()
            composeTestRule
                .onNodeWithTag("list_event_row_${SAMPLE_EVENTS[i].eventId}", useUnmergedTree = true)
                .assertExists()
            composeTestRule
                .onNodeWithTag(
                    "list_event_title_${SAMPLE_EVENTS[i].eventId}",
                    useUnmergedTree = true
                )
                .assertTextEquals(SAMPLE_EVENTS[i].title)
            composeTestRule
                .onNodeWithTag(
                    "list_event_date_${SAMPLE_EVENTS[i].eventId}",
                    useUnmergedTree = true
                )
                .assertTextEquals(
                    SAMPLE_EVENTS[i]
                        .startDate
                        .format(DateTimeFormatter.ofPattern("E, dd/MM\nHH:mm"))
                )
            composeTestRule
                .onNodeWithTag(
                    "list_event_participants_${SAMPLE_EVENTS[i].eventId}",
                    useUnmergedTree = true
                )
                .assertTextEquals(
                    "${SAMPLE_EVENTS[i].participantCount}/${SAMPLE_EVENTS[i].maxParticipants}"
                )
        }
    }

    @Test
    fun testAllEventsExtendsCorrectlyWhenClicked() {
        // Check max 5 events to avoid interminable test when we will have a lot of events
        for (i in 0..min(SAMPLE_EVENTS.size - 1, 4)) {
            composeTestRule
                .onNodeWithTag(
                    "list_event_details_${SAMPLE_EVENTS[i].eventId}",
                    useUnmergedTree = true
                )
                .assertDoesNotExist()
            composeTestRule
                .onNodeWithTag("list_event_item_${SAMPLE_EVENTS[i].eventId}")
                .performClick()
            composeTestRule
                .onNodeWithTag(
                    "list_event_details_${SAMPLE_EVENTS[i].eventId}",
                    useUnmergedTree = true
                )
                .assertExists()
            composeTestRule
                .onNodeWithTag(
                    "list_join_event_${SAMPLE_EVENTS[i].eventId}",
                    useUnmergedTree = true
                )
                .performClick()
            composeTestRule
                .onNodeWithTag(
                    "list_event_description_${SAMPLE_EVENTS[i].eventId}",
                    useUnmergedTree = true
                )
                .assertTextEquals(SAMPLE_EVENTS[i].description)
            composeTestRule
                .onNodeWithTag("list_event_item_${SAMPLE_EVENTS[i].eventId}")
                .performClick()
            composeTestRule
                .onNodeWithTag(
                    "list_event_details_${SAMPLE_EVENTS[i].eventId}",
                    useUnmergedTree = true
                )
                .assertDoesNotExist()
        }
    }
}
