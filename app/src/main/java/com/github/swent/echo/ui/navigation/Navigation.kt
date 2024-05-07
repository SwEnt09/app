package com.github.swent.echo.ui.navigation

import androidx.navigation.NavHostController
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.repository.Repository
import kotlinx.coroutines.runBlocking

/** The routes to the screens of the app */
data class Routes(val name: String) {

    /** The different routes available */
    companion object {
        val LOGIN = Routes("login")
        val REGISTER = Routes("register")
        val MAP = Routes("map")
        val CREATE_EVENT = Routes("create_event")
        val EDIT_EVENT = RoutesBuilder("edit_event", "eventId")
        val PROFILE_CREATION = Routes("profile_creation")
        val ASSOCIATION_SUBSCRIPTIONS = Routes("association_subscriptions")
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
    private val authenticationService: AuthenticationService,
    private val repository: Repository,
) {

    // navigate to one of the routes available in Routes
    fun navigateTo(route: Routes) {
        if (route == Routes.MAP) {
            val userId = authenticationService.getCurrentUserID()

            // If the user is not logged in, navigate to the login screen. Else if the user is
            // logged in but has no profile, navigate to the create profile screen.
            if (userId == null) {
                navigateTo(Routes.LOGIN)
                return
            } else if (runBlocking { repository.getUserProfile(userId) } == null) {
                navigateTo(Routes.PROFILE_CREATION)
                return
            }
        }
        navController.navigate(route.name)
    }

    // navigate to the previous screen
    fun goBack() {
        navController.navigateUp()
    }
}
