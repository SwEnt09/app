package com.github.swent.echo.compose.components.myevents

import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.compose.myevents.MyEventsScreen
import com.github.swent.echo.ui.navigation.NavigationActions
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyEventsScreenTest {
    @get:Rule val composeTestRule = createComposeRule()

    private lateinit var navActions: NavigationActions

    @Before
    fun setUp() {
        composeTestRule.setContent {
            navActions = mockk(relaxed = true)
            MyEventsScreen(navActions)
        }
    }

    @Test
    fun myEventsScreenExists() {
        composeTestRule.onNodeWithTag("my_events_screen").assertExists()
    }

    @Test
    fun backButtonExists() {
        composeTestRule.onNodeWithTag("Back-button").assertExists()
    }

    @Test
    fun switchBetweenJoinedAndCreatedWorks() {
        // Check that the tabs exist
        composeTestRule.onNodeWithTag("my_events_joined_events_tab").assertExists()
        composeTestRule.onNodeWithTag("my_events_created_events_tab").assertExists()
        // Initial state
        composeTestRule.onNodeWithTag("my_events_underline_joined_events").assertExists()
        composeTestRule.onNodeWithTag("my_events_underline_created_events").assertDoesNotExist()
        // Switch to created events
        composeTestRule.onNodeWithTag("my_events_created_events_tab").performClick()
        composeTestRule.onNodeWithTag("my_events_underline_joined_events").assertDoesNotExist()
        composeTestRule.onNodeWithTag("my_events_underline_created_events").assertExists()
        // Switch back to joined events
        composeTestRule.onNodeWithTag("my_events_joined_events_tab").performClick()
        composeTestRule.onNodeWithTag("my_events_underline_joined_events").assertExists()
        composeTestRule.onNodeWithTag("my_events_underline_created_events").assertDoesNotExist()
    }
}
