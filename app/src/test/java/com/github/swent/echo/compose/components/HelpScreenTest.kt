package com.github.swent.echo.compose.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.ui.navigation.NavigationActions
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HelpScreenTest {

    @get:Rule val composeTestRule = createComposeRule()

    private lateinit var navActions: NavigationActions

    @Before
    fun setUp() {
        navActions = mockk(relaxed = true)
        composeTestRule.setContent { HelpScreen(navActions = navActions) }
    }

    @Test
    fun shuoldCallGoBackWhenBackButtonIsPressed() {
        composeTestRule.onNodeWithTag("back")
    }
}
