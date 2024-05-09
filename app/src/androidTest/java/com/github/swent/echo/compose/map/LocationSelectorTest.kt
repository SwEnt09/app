package com.github.swent.echo.compose.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationSelectorTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        composeTestRule.setContent { LocationSelector {} }
    }

    @Test
    fun shouldDisplayLocationSelector() {
        composeTestRule.onNodeWithTag("location-selector-map-libre").assertIsDisplayed()
    }
}
