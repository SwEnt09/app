package com.github.swent.echo.compose.components.myevents

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.MainActivity
import com.github.swent.echo.compose.myevents.MyEventsScreen
import com.github.swent.echo.ui.navigation.NavigationActions
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MyEventsScreenTest {
    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navActions: NavigationActions

    @Before
    fun setUp() {
        hiltRule.inject()

        navActions = mockk(relaxed = true)
        composeTestRule.activity.setContent { MyEventsScreen(hiltViewModel(), navActions) }
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
    fun pagerExists() {
        composeTestRule.onNodeWithTag("pager").assertExists()
    }

    @Test
    fun goToCreatedEventsAndBack() {
        composeTestRule.onNodeWithTag("pager").assertIsDisplayed()
        composeTestRule.onNodeWithTag("page_title_1").performClick()
        composeTestRule.onNodeWithTag("page_title_0").performClick()
    }
}
