package com.github.swent.echo.compose.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.swent.echo.compose.authentication.LoginScreen
import com.github.swent.echo.compose.authentication.RegisterScreen
import com.github.swent.echo.compose.components.HomeScreen
import com.github.swent.echo.compose.event.CreateEventScreen
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes

/**
 * Navigation composable, it display the relevant screen based on the route and pass an instance of
 * navigationActions to the screens
 *
 * @param navController the navigation controller, created if none is given
 */
@Composable
fun AppNavigationHost(
    userIsLoggedIn: Boolean,
    navController: NavHostController = rememberNavController(),
) {
    val navActions = NavigationActions(navController)

    NavHost(
        navController = navController,
        startDestination = if (userIsLoggedIn) Routes.MAP.name else Routes.REGISTER.name,
    ) {
        composable(Routes.LOGIN.name) {
            LoginScreen(loginViewModel = hiltViewModel(), navActions = navActions)
        }

        composable(Routes.REGISTER.name) {
            RegisterScreen(registerViewModel = hiltViewModel(), navActions = navActions)
        }

        composable(Routes.MAP.name) {
            // placeholder for the map composable
            HomeScreen(navActions)
        }

        composable(Routes.CREATE_EVENT.name) {
            CreateEventScreen(eventViewModel = hiltViewModel(), navigationActions = navActions)
        }
    }
}
