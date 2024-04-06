package com.github.swent.echo.compose.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.swent.echo.ui.navigation.Routes

/**
 * Navigation composable, it display the relevant screen based on the route and pass an instance of
 * navigationActions to the screens
 *
 * @param navController the navigation controller, created if none is given
 */
@Composable
fun AppNavigationHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SIGN_IN.name,
    ) {
        composable(Routes.SIGN_IN.name) {
            // placeholder for the sign in composable to test it's displayed
            Text("sign in screen", modifier = Modifier.testTag("signInScreen"))
        }

        composable(Routes.MAP.name) {
            // placeholder for the map composable
            Text("map screen", modifier = Modifier.testTag("mapScreen"))
        }
    }
}
