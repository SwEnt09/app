package com.github.swent.echo.ui.navigation

import androidx.navigation.NavHostController

/** The routes to the screens of the app */
data class Routes(val name: String) {

    /** The different routes available */
    companion object {
        val LOADING = Routes("loading")
        val LOGIN = Routes("login")
        val REGISTER = Routes("register")
        val MAP = Routes("map")
        val CREATE_EVENT = Routes("create_event")
        val EDIT_EVENT = RoutesBuilder("edit_event", "eventId")
        val PROFILE_CREATION = Routes("profile_creation")
        val ASSOCIATION_SUBSCRIPTIONS = Routes("association_subscriptions")
        val ASSOCIATION_MEMBERSHIPS = Routes("association_memberships")
    }
}

/**
 * This class represent a route with an argument. Use [build] to create build the route with an
 * argument.
 */
data class RoutesBuilder(private val route: String, private val argName: String) {
    val name: String = "$route/{$argName}"

    fun build(arg: String): Routes {
        return Routes("$route/$arg")
    }
}

/**
 * This class represent the different navigation actions
 *
 * @param navController the navigation controller
 */
class NavigationActions(
    private val navController: NavHostController,
) {

    // navigate to one of the routes available in Routes
    fun navigateTo(route: Routes) {
        navController.navigate(route.name)
    }

    // navigate to the previous screen
    fun goBack() {
        navController.navigateUp()
    }
}
