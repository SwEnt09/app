package com.github.swent.echo.compose.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationSelectorTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayLocationSelector() {
        composeTestRule.setContent { LocationSelector {} }
        composeTestRule.onNodeWithTag("location-selector-map-libre").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayLocationDisplayer() {
        composeTestRule.setContent { LocationDisplayer(position = MAP_CENTER.toLatLng()) }
        composeTestRule.onNodeWithTag("location-displayer-map-libre").assertIsDisplayed()
    }
}
