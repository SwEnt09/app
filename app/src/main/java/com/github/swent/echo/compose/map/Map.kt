package com.github.swent.echo.compose.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.viewmodels.MapDrawerViewModel
import kotlinx.coroutines.runBlocking

val MAP_CENTER = Location("EPFL", 46.5191, 6.5668)
const val DEFAULT_ZOOM = 13.0

@Composable
fun <T : View> EchoAndroidView(
    modifier: Modifier = Modifier,
    factory: (Context, Boolean, (() -> Unit)) -> T,
    update: (T, List<Event>, (Event) -> Unit, Boolean) -> Unit,
    events: List<Event>,
    callback: (Event) -> Unit = {},
    withLocation: Boolean
) {
    var trigger by remember { mutableStateOf(false) }
    AndroidView(
        modifier = modifier.testTag("mapAndroidView"),
        factory = { factory(it, withLocation) { trigger = true } },
        update = {
            if (trigger) {
                update(it, events, callback, withLocation)
            }
        }
    )
}

val PERMISSIONS =
    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

fun permissionsDenied(context: Context) =
    PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED
    }

/**
 * This composable will draw a map that displays events.
 *
 * @param events The list of events to be displayed by the map. Whenever this list changes, the
 *   event markers in the map will be automatically updated to match the list.
 * @param callback The function to be called when a marker on the map is clicked on. The [Event]
 *   corresponding to the marker will be passed as an argument to this function.
 * @param mapDrawerViewModel The [MapDrawerViewModel] to be used as view factory.
 */
@Composable
fun MapDrawer(
    modifier: Modifier = Modifier,
    events: List<Event>,
    callback: (Event) -> Unit = {},
    mapDrawerViewModel: MapDrawerViewModel = hiltViewModel(),
) {
    var displayLocation by remember { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { p
            ->
            displayLocation = displayLocation || p.values.any { it }
        }
    val c = LocalContext.current
    /*
    This has to be blocking as we don't want the `EchoAndroidView` to be
    created before launching the permission request.
    */
    if (runBlocking { permissionsDenied(c) }) {
        SideEffect { launcher.launch(PERMISSIONS) }
    } else {
        SideEffect { displayLocation = true }
    }
    EchoAndroidView(
        modifier = modifier.testTag("mapViewWrapper"),
        factory = mapDrawerViewModel::factory,
        // Function that will be called when the view has been
        // inflated or state read in this function has been updated
        // AndroidView will recompose whenever said state changes
        update = mapDrawerViewModel::update,
        events = events,
        callback = callback,
        withLocation = displayLocation
    )
}
