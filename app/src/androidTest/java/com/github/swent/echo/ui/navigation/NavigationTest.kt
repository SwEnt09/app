package com.github.swent.echo.ui.navigation

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.github.swent.echo.MainActivity
import com.github.swent.echo.compose.navigation.AppNavigationHost
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0) val hiltRule = HiltAndroidRule(this)

    // Create a test rule for the `MainActivity`
    @get:Rule(order = 1) val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun setUp(route: Routes) {
        composeTestRule.activity.setContent {
            val navController = rememberNavController()
            val navigationActions = NavigationActions(navController)
            AppNavigationHost(navController)
            navigationActions.navigateTo(route)
        }
    }

    @Test
    fun testDefaultRoute() {
        composeTestRule.activity.setContent {
            val navController = rememberNavController()
            AppNavigationHost(navController)
        }

        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()
    }

    @Test
    fun testMapRoute() {
        setUp(Routes.MAP)
        composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    }

    @Test
    fun testLoginRoute() {
        setUp(Routes.LOGIN)
        composeTestRule.onNodeWithTag("login-screen").assertIsDisplayed()
    }

    @Test
    fun testRegisterRoute() {
        setUp(Routes.REGISTER)
        composeTestRule.onNodeWithTag("register-screen").assertIsDisplayed()
    }
}
