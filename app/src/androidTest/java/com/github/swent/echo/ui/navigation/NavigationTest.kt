package com.github.swent.echo.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
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
            appNavigationHost(navController)
            val navigationActions = navigationActions(navController)
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
        val navigationActions = navigationActions(mockedNavController)
        navigationActions.goBack()
        verify { mockedNavController.navigateUp() }
    }

    // test the start route is displayed
    @Test
    fun testAppNavHostComposableStartRoute() {
        composeTestRule.setContent { EchoTheme { appNavigationHost() } }
        composeTestRule.onNodeWithTag("signInScreen").assertIsDisplayed()
    }

    @Test
    fun testAppNavHostComposableMapRoute() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            appNavigationHost(navController)
            navController.navigate(Routes.MAP.name)
        }
        composeTestRule.onNodeWithTag("mapScreen").assertIsDisplayed()
    }
}
