package com.github.swent.echo.compose.map

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.viewmodels.MapDrawerViewModel
import com.mapbox.mapboxsdk.geometry.LatLng

val MAP_CENTER = Location("EPFL", 46.5191, 6.5668)
const val DEFAULT_ZOOM = 13.0

/**
 * Wrapper around an [AndroidView] to facilitate readability.
 *
 * @param factory The factory that will product a [View].
 * @param update The function to update said [View].
 * @param events The list of events to be displayed by the map. Whenever this list changes, the
 *   event markers in the map will be automatically updated to match the list.
 * @param callback The function to be called when a marker on the map is clicked on. The [Event]
 *   corresponding to the marker will be passed as an argument to this function.
 * @param withLocation Whether the [View] should display the location of the user.
 * @param launchEventCreation A function to enable the [View] to launch event creations.
 */
@Composable
fun <T : View> EchoAndroidView(
    modifier: Modifier = Modifier,
    factory: (Context, Boolean, (() -> Unit), (LatLng) -> Unit) -> T,
    update: (T, List<Event>, (Event) -> Unit, Boolean) -> Unit,
    events: List<Event>,
    callback: (Event) -> Unit = {},
    withLocation: Boolean,
    launchEventCreation: (LatLng) -> Unit
) {
    // Will change when the map construction is done in order to trigger an update.
    var trigger by remember { mutableStateOf(false) }
    AndroidView(
        modifier = modifier.testTag("mapAndroidView"),
        factory = { factory(it, withLocation, { trigger = true }) { launchEventCreation(it) } },
        update = { update(it, events, callback, trigger && withLocation) }
    )
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
    launchEventCreation: (LatLng) -> Unit = {},
    mapDrawerViewModel: MapDrawerViewModel,
    displayLocation: Boolean = false
) {
    EchoAndroidView(
        modifier = modifier.testTag("mapViewWrapper"),
        factory = mapDrawerViewModel::factory,
        // Function that will be called when the view has been
        // inflated or state read in this function has been updated
        // AndroidView will recompose whenever said state changes
        update = mapDrawerViewModel::update,
        events = events,
        callback = callback,
        withLocation = displayLocation,
        launchEventCreation = launchEventCreation,
    )
}
