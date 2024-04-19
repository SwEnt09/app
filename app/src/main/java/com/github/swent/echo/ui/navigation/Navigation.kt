package com.github.swent.echo.ui.navigation

import androidx.navigation.NavHostController

/** The routes to the screens of the app */
enum class Routes {
    LOGIN,
    REGISTER,
    MAP,
    CREATE_EVENT
}

/**
 * This class reprensent the different navigation actions
 *
 * @param navController the navigation controller
 */
class NavigationActions(val navController: NavHostController) {

    // navigate to one of the routes available in Routes
    fun navigateTo(route: Routes) {
        navController.navigate(route.name)
    }

    // navigate to the previous screen
    fun goBack() {
        navController.navigateUp()
    }
}
