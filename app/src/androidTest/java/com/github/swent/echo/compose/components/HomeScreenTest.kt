package com.github.swent.echo.compose.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.swent.echo.compose.map.MAP_CENTER
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.ui.navigation.NavigationActions
import io.mockk.mockk
import java.time.Instant
import java.util.Date
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    private lateinit var navActions: NavigationActions

    @Before
    fun setUp() {
        navActions = mockk(relaxed = true)
        composeTestRule.setContent { HomeScreen(navActions) }
    }

    @Test
    fun shouldShowHomeScreen() {
        composeTestRule.onNodeWithTag("home_screen").assertExists()
    }

    @Test
    fun shouldShowTopBar() {
        composeTestRule.onNodeWithTag("top_bar").assertExists()
    }

    @Test
    fun shouldShowSearchButton() {
        composeTestRule.onNodeWithTag("search_button").assertExists()
    }

    @Test
    fun shouldShowMap() {
        composeTestRule.onNodeWithTag("mapViewWrapper").assertExists()
    }

    @Test
    fun shouldNotShowMapWhenListMode() {
        composeTestRule.onNodeWithTag("list_map_mode_button").performClick()
        composeTestRule.onNodeWithTag("mapViewWrapper").assertDoesNotExist()
    }

    @Test
    fun shouldShowSearchMenuSheet() {
        composeTestRule.onNodeWithTag("search_button").performClick()
        composeTestRule.onNodeWithTag("search_menu_sheet").assertExists()
    }

    @Test
    fun shouldNotShowEventInfoSheet() {
        composeTestRule.onNodeWithTag("event_info_sheet").assertDoesNotExist()
    }

    @Test
    fun shouldShowEventInfoDataClass() {
        val event =
            Event(
                eventId = "a",
                organizerId = "a",
                title = "Bowling Event",
                description = "",
                location = Location("Location 1", MAP_CENTER.toGeoPoint()),
                startDate = Date.from(Instant.now()),
                endDate = Date.from(Instant.now()),
                tags = emptySet(),
            )
        val obj = DisplayEventInfo(event, 0, 0, 0, "")
    }
}
