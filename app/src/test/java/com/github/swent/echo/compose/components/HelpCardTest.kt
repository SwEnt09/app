package com.github.swent.echo.compose.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HelpCardTest {
    @get:Rule val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        composeTestRule.setContent { HelpCard(helpItem = HelpItem("Title", "Description")) }
    }

    @Test
    fun shouldNotShowDescription() {
        composeTestRule.onNodeWithTag("title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("description").assertIsNotDisplayed()
    }

    @Test
    fun shuoldOpenDescriptionWhenClickingOnDropdownIcon() {
        composeTestRule.onNodeWithTag("toggle-description").performClick()
        composeTestRule.onNodeWithTag("description").assertIsDisplayed()
    }

    @Test
    fun shouldShowCorrectContent() {
        composeTestRule.onNodeWithTag("toggle-description").performClick()
        composeTestRule.onNodeWithText("Title")
        composeTestRule.onNodeWithText("Description")
    }
}
