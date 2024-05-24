package com.github.swent.echo.compose.navigation

// import com.github.swent.echo.compose.authentication.ProfileCreationScreen
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.compose.association.AssociationScreen
import com.github.swent.echo.compose.authentication.LoadingScreen
import com.github.swent.echo.compose.authentication.LoginScreen
import com.github.swent.echo.compose.authentication.ProfileCreationScreen
import com.github.swent.echo.compose.authentication.RegisterScreen
import com.github.swent.echo.compose.components.HelpScreen
import com.github.swent.echo.compose.components.HomeScreen
import com.github.swent.echo.compose.event.CreateEventScreen
import com.github.swent.echo.compose.event.EditEventScreen
import com.github.swent.echo.compose.myevents.MyEventsScreen
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.tag.TagViewModel
import com.github.swent.echo.viewmodels.HomeScreenViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.runBlocking

val LOCATION_PERMISSIONS =
    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

fun locationPermissionsDenied(context: Context) =
    LOCATION_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED
    }

typealias OnLocationSuccessListener = (Location) -> Unit

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
    // Handle Permissions
    var hasLocationPermissions by remember { mutableStateOf(false) }
    var alreadyDeniedLocationPermissions by rememberSaveable { mutableStateOf(false) }
    val scope = currentRecomposeScope
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            alreadyDeniedLocationPermissions = !it.values.any { granted -> granted }
            scope.invalidate()
        }
    val localContext = LocalContext.current

    val locationServices = LocationServices.getFusedLocationProviderClient(LocalContext.current)

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
            if (
                !alreadyDeniedLocationPermissions &&
                    runBlocking { locationPermissionsDenied(localContext) }
            ) {
                SideEffect { launcher.launch(LOCATION_PERMISSIONS) }
            } else {
                SideEffect { hasLocationPermissions = !alreadyDeniedLocationPermissions }
            }
            val homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
            homeScreenViewModel.bindOnLocationSuccessListener { onSuccess ->
                if (
                    LOCATION_PERMISSIONS.any {
                        ContextCompat.checkSelfPermission(localContext, it) ==
                            PackageManager.PERMISSION_GRANTED
                    }
                ) {
                    locationServices.lastLocation
                        .addOnSuccessListener { location ->
                            // TODO if the location is null, try again with scope.invalidate()
                            location?.let(onSuccess)
                        }
                        .addOnFailureListener { scope.invalidate() }
                }
            }
            HomeScreen(
                homeScreenViewModel = homeScreenViewModel,
                navActions = navActions,
                hasLocationPermissions = hasLocationPermissions
            )
        }

        composable(Routes.CREATE_EVENT.name) {
            CreateEventScreen(eventViewModel = hiltViewModel(), navigationActions = navActions)
        }

        composable(Routes.PROFILE_CREATION.name) {
            QuitAppOnHardwareBackButtonPressPress()
            ProfileCreationScreen(
                viewModel = hiltViewModel(),
                navAction = navActions,
                tagviewModel =
                    hiltViewModel<TagViewModel, TagViewModel.TagViewModelFactory>() { factory ->
                        factory.create()
                    }
            )
        }

        composable(Routes.EDIT_EVENT.name) {
            EditEventScreen(eventViewModel = hiltViewModel(), navigationActions = navActions)
        }

        composable(Routes.MY_EVENTS.name) {
            MyEventsScreen(myEventsViewModel = hiltViewModel(), navActions = navActions)
        }

        composable(Routes.ASSOCIATIONS.name) {
            AssociationScreen(hiltViewModel(), navActions = navActions)
        }

        composable(Routes.HELP.name) { HelpScreen(navActions) }
    }
}

/** Quit the app when the hardware back button is pressed */
@Composable
fun QuitAppOnHardwareBackButtonPressPress() {
    val activity = LocalContext.current as Activity
    BackHandler(enabled = true) { activity.finish() }
}
