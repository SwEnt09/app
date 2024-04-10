package com.github.swent.echo.compose.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchButtonTest {

    @get:Rule val composeTestRule = createComposeRule()

    private var clicked = 0

    @Before
    fun setUp() {
        clicked = 0
        composeTestRule.setContent { SearchButton(onClick = { clicked++ }) }
    }

    @Test
    fun shouldShowSearchButton() {
        composeTestRule.onNodeWithTag("search_button").assertExists()
    }

    @Test
    fun shouldClickSearchButton() {
        composeTestRule.onNodeWithTag("search_button").performClick()
        assertThat(clicked, equalTo(1))
    }
}
