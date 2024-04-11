package com.github.swent.echo.compose.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.swent.echo.ui.navigation.NavigationActions
import io.mockk.mockk
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TopBarTest {

    @get:Rule val composeTestRule = createComposeRule()

    private var searchClicked = 0
    private lateinit var navActions: NavigationActions

    @Before
    fun setUp() {
        searchClicked = 0
        navActions = mockk(relaxed = true)
        composeTestRule.setContent { TopBar(navActions, { searchClicked++ }) }
    }

    @Test
    fun shouldShowEcho() {
        composeTestRule.onNodeWithText("Echo").assertExists()
    }

    @Test
    fun shouldCallOpenSearchCallbackWhenSearchButtonClicked() {
        composeTestRule.onNodeWithTag("list_map_mode_button").performClick()
        assertThat(searchClicked, equalTo(1))
    }

    @Test
    fun shouldOpenHamburgerMenuWhenMenuButtonClicked() {
        composeTestRule.onNodeWithTag("menu_button").performClick()
        composeTestRule.onNodeWithTag("hamburger_menu").assertExists()
    }

    @Test
    fun shouldShowProfileBoxWhenMenuButtonClicked() {
        composeTestRule.onNodeWithTag("menu_button").performClick()
        composeTestRule.onNodeWithTag("profile_box").assertExists()
    }

    @Test
    fun shouldShowProfileSheetWhenMenuButtonClicked() {
        composeTestRule.onNodeWithTag("menu_button").performClick()
        composeTestRule.onNodeWithTag("profile_sheet").assertExists()
    }

    @Test
    fun shouldShowProfilePictureWhenMenuButtonClicked() {
        composeTestRule.onNodeWithTag("menu_button").performClick()
        composeTestRule.onNodeWithTag("profile_picture").assertExists()
    }

    @Test
    fun shouldShowProfileInfoWhenMenuButtonClicked() {
        composeTestRule.onNodeWithTag("menu_button").performClick()
        composeTestRule.onNodeWithTag("profile_info").assertExists()
    }

    @Test
    fun shouldShowProfileNameWhenMenuButtonClicked() {
        composeTestRule.onNodeWithTag("menu_button").performClick()
        composeTestRule.onNodeWithTag("profile_name").assertExists()
    }

    @Test
    fun shouldShowProfileClassWhenMenuButtonClicked() {
        composeTestRule.onNodeWithTag("menu_button").performClick()
        composeTestRule.onNodeWithTag("profile_class").assertExists()
    }

    @Test
    fun shouldShowDarkModeButtonWhenMenuButtonClicked() {
        composeTestRule.onNodeWithTag("menu_button").performClick()
        composeTestRule.onNodeWithTag("dark_mode_button").assertExists()
    }

    @Test
    fun shouldShowAllItemsWhenMenuButtonClicked() {
        composeTestRule.onNodeWithTag("menu_button").performClick()
        for (i in 0..6) {
            composeTestRule.onNodeWithTag("navigation_item_$i").assertExists()
        }
    }

    @Test
    fun shouldCloseHamburgerMenuWhenNavigationItemClicked() {
        composeTestRule.onNodeWithTag("menu_button").performClick()
        for (i in 0..6) {
            composeTestRule.onNodeWithTag("navigation_item_$i").performClick()
            composeTestRule.onNodeWithTag("top_bar").assertExists()
        }
    }
}
