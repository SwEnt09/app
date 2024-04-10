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
    fun shouldCallOpenSearchCallbackWhenSearchButtonClicked() {
        composeTestRule.onNodeWithTag("list_map_mode_button").performClick()
        assertThat(searchClicked, equalTo(1))
    }
}
