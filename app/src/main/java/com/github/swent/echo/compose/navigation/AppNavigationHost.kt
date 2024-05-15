package com.github.swent.echo.compose.navigation

// import com.github.swent.echo.compose.authentication.ProfileCreationScreen
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.compose.association.AssociationCommitteeMemberScreen
import com.github.swent.echo.compose.association.AssociationScreen
import com.github.swent.echo.compose.association.AssociationSubscriptionsScreen
import com.github.swent.echo.compose.authentication.LoadingScreen
import com.github.swent.echo.compose.authentication.LoginScreen
import com.github.swent.echo.compose.authentication.ProfileCreationScreen
import com.github.swent.echo.compose.authentication.RegisterScreen
import com.github.swent.echo.compose.components.HomeScreen
import com.github.swent.echo.compose.event.CreateEventScreen
import com.github.swent.echo.compose.event.EditEventScreen
import com.github.swent.echo.compose.myevents.MyEventsScreen
import com.github.swent.echo.data.repository.Repository
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
    navController: NavHostController = rememberNavController(),
    authenticationService: AuthenticationService,
    repository: Repository,
) {
    val navActions = NavigationActions(navController)

    // At application start, initialize the authentication service and navigate to the map screen.
    // The navigation actions will take care of navigating to the correct screen based on the
    // current user id and its profile.
    LaunchedEffect(navController) {
        // Make sure we initialize the authentication service. Otherwise, the current user id will
        // always be null.
        authenticationService.initialize()

        // Get the current user id
        val userId = authenticationService.getCurrentUserID()

        // If the user is not logged in, navigate to the register screen. Else if the user is
        // logged in but has no profile, navigate to the create profile screen. Otherwise, navigate
        // to the map screen.
        if (userId == null) {
            navActions.navigateTo(Routes.REGISTER)
        } else if (repository.getUserProfile(userId) == null) {
            navActions.navigateTo(Routes.PROFILE_CREATION)
        } else {
            navActions.navigateTo(Routes.MAP)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.LOADING.name,
    ) {
        composable(Routes.LOADING.name) {
            QuitAppOnHardwareBackButtonPressPress()
            LoadingScreen()
        }

        composable(Routes.LOGIN.name) {
            QuitAppOnHardwareBackButtonPressPress()
            LoginScreen(loginViewModel = hiltViewModel(), navActions = navActions)
        }

        composable(Routes.REGISTER.name) {
            QuitAppOnHardwareBackButtonPressPress()
            RegisterScreen(registerViewModel = hiltViewModel(), navActions = navActions)
        }

        composable(Routes.MAP.name) {
            QuitAppOnHardwareBackButtonPressPress()
            HomeScreen(homeScreenViewModel = hiltViewModel(), navActions = navActions)
        }

        composable(Routes.CREATE_EVENT.name) {
            CreateEventScreen(eventViewModel = hiltViewModel(), navigationActions = navActions)
        }

        composable(Routes.PROFILE_CREATION.name) {
            QuitAppOnHardwareBackButtonPressPress()
            ProfileCreationScreen(
                viewModel = hiltViewModel(),
                navAction = navActions,
                tagviewModel = hiltViewModel()
            )
        }

        composable(Routes.EDIT_EVENT.name) {
            // TODO: set the event id in the eventViewModel as a savedStateHandle with key "eventId"
            EditEventScreen(eventViewModel = hiltViewModel(), navigationActions = navActions)
        }

        composable(Routes.ASSOCIATION_SUBSCRIPTIONS.name) {
            AssociationSubscriptionsScreen(
                userProfileViewModel = hiltViewModel(),
                navigationActions = navActions
            )
        }

        composable(Routes.ASSOCIATION_MEMBERSHIPS.name) {
            AssociationCommitteeMemberScreen(
                userProfileViewModel = hiltViewModel(),
                navigationActions = navActions
            )
        }

        composable(Routes.MY_EVENTS.name) { MyEventsScreen(navActions = navActions) }

        composable(Routes.ASSOCIATIONS.name) {
            AssociationScreen(hiltViewModel(), navActions = navActions)
        }
    }
}

/** Quit the app when the hardware back button is pressed */
@Composable
fun QuitAppOnHardwareBackButtonPressPress() {
    val activity = LocalContext.current as Activity
    BackHandler(enabled = true) { activity.finish() }
}
