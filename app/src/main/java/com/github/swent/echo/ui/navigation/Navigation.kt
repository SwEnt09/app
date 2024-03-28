package com.github.swent.echo.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/** The routes to the screens of the app */
enum class Routes {
    SIGN_IN,
    MAP,
}

/**
 * This class reprensent the different navigation actions
 *
 * @param navController the navigation controller
 */
class navigationActions(val navController: NavHostController) {

    // navigate to one of the routes available in Routes
    fun navigateTo(route: Routes) {
        navController.navigate(route.name)
    }

    // navigate to the previous screen
    fun goBack() {
        navController.navigateUp()
    }
}

/**
 * Navigation composable, it display the relevant screen based on the route and pass an instance of
 * navigationActions to the screens
 *
 * @param navController the navigation controller, created if none is given
 */
@Composable
fun appNavigationHost(
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
