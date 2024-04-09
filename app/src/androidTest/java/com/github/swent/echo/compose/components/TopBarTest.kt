package com.github.swent.echo.compose.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TopBarTest {

    @get:Rule val composeTestRule = createComposeRule()

    private var menuClicked = 0
    private var searchClicked = 0

    @Before
    fun setUp() {
        menuClicked = 0
        searchClicked = 0
        composeTestRule.setContent { TopBar({ menuClicked++ }, { searchClicked++ }) }
    }

    @Test
    fun shouldShowEcho() {
        composeTestRule.onNodeWithText("Echo").assertExists()
    }

    @Test
    fun shouldCallOpenMenuCallbackWhenMenuButtonClicked() {
        composeTestRule.onNodeWithTag("menu_button").performClick()
        assertThat(menuClicked, equalTo(1))
    }

    @Test
    fun shouldCallOpenSearchCallbackWhenSearchButtonClicked() {
        composeTestRule.onNodeWithTag("search_button").performClick()
        assertThat(searchClicked, equalTo(1))
    }
}
