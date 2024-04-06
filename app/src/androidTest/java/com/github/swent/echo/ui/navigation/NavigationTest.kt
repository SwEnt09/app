package com.github.swent.echo.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.compose.navigation.AppNavigationHost
import com.github.swent.echo.ui.theme.EchoTheme
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule val composeTestRule = createComposeRule()

    // test the navigateTo function of the class NavigationActions
    @Test
    fun testNavigationActionsNavigateTo() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            AppNavigationHost(navController)
            val navigationActions = NavigationActions(navController)
            for (route in Routes.entries) {
                navigationActions.navigateTo(route)
                assert(navController.currentDestination?.route == route.name)
            }
            assert(navController.currentBackStack.value.isNotEmpty())
            assert(
                navController.currentBackStack.value.last().destination.route ==
                    Routes.entries.last().name
            )
        }
    }

    // test the goBack function of the class NavigationActions
    @Test
    fun testNavigationActionsGoBack() {
        val mockedNavController = mockk<NavHostController>(relaxed = true)
        every { mockedNavController.navigateUp() } returns true
        val navigationActions = NavigationActions(mockedNavController)
        navigationActions.goBack()
        verify { mockedNavController.navigateUp() }
    }

    // test the start route is displayed
    @Test
    fun testAppNavHostComposableStartRoute() {
        composeTestRule.setContent { EchoTheme { AppNavigationHost() } }
        composeTestRule.onNodeWithTag("signInScreen").assertIsDisplayed()
    }

    // test the map route is displayed
    @Test
    fun testAppNavHostComposableMapRoute() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            AppNavigationHost(navController)
            navController.navigate(Routes.MAP.name)
        }
        composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    }
}
