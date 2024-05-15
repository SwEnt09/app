package com.github.swent.echo.compose.association

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.MainActivity
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
class AssociationScreenTest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navActions: NavigationActions

    @Before
    fun setUp() {
        hiltRule.inject()

        navActions = mockk(relaxed = true)
        composeTestRule.activity.setContent {
            AssociationScreen(associationViewModel = hiltViewModel(), navActions = navActions)
        }
    }

    @Test
    fun associationScreenExists() {
        composeTestRule.onNodeWithTag("association_screen").assertExists()
    }

    @Test
    fun navigationWorks() {
        composeTestRule.onNodeWithTag("association_main_screen").assertExists()

        composeTestRule.onNodeWithTag("search_button").performClick()
        composeTestRule.onNodeWithTag("association_search").assertExists()
    }
}
