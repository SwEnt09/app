package com.github.swent.echo.compose.components

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.MainActivity
import com.github.swent.echo.data.SAMPLE_EVENTS
import com.github.swent.echo.data.model.Event
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlin.math.min
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ListDrawerTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        composeTestRule.activity.setContent { ListDrawer(SAMPLE_EVENTS, true, {}) }
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
                .assertIsDisplayed()
            composeTestRule
                .onNodeWithTag(
                    "list_event_participants_${SAMPLE_EVENTS[i].eventId}",
                    useUnmergedTree = true
                )
                .assertTextEquals(
                    "${SAMPLE_EVENTS[i].participantCount} / ${SAMPLE_EVENTS[i].maxParticipants}"
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
                .assertIsDisplayed()
        }
    }

    @Test
    fun shouldDisplayViewOnMapWhenCallbackIsNotNull() {
        var eventToView: Event? = null
        composeTestRule.activity.setContent {
            ListDrawer(SAMPLE_EVENTS, true, {}, { eventToView = it })
        }
        for (i in 0..min(SAMPLE_EVENTS.size - 1, 4)) {
            val eventId = SAMPLE_EVENTS[i].eventId

            // View on map icon is not displayed
            composeTestRule.onNodeWithTag("view_on_map_$eventId").assertIsNotDisplayed()

            // Click on the event
            composeTestRule.onNodeWithTag("list_event_item_$eventId").performClick()

            // Clicking on the view on map icon should call the callback
            composeTestRule.onNodeWithTag("view_on_map_$eventId").performClick()

            assertEquals(eventId, eventToView?.eventId)
        }
    }

    @Test
    fun shuoldDisplayEditIconWhenIsEventCreator() {
        var eventToModify: Event? = null
        val eventId = SAMPLE_EVENTS[0].eventId
        val userId = SAMPLE_EVENTS[0].creator.userId

        composeTestRule.activity.setContent {
            ListDrawer(SAMPLE_EVENTS, true, {}, modify = { eventToModify = it }, userId = userId)
        }

        // View on map icon is not displayed
        composeTestRule.onNodeWithTag("modify_$eventId").assertIsNotDisplayed()

        // Click on the event
        composeTestRule.onNodeWithTag("list_event_item_$eventId").performClick()

        // Clicking on the view on map icon should call the callback
        composeTestRule.onNodeWithTag("modify_event_$eventId").performClick()

        assertEquals(eventId, eventToModify?.eventId)
    }
}
