package com.github.swent.echo.compose.map

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location

val MAP_CENTER = Location("Lausanne Center", 46.5197, 6.6323)
const val DEFAULT_ZOOM = 15.0

@Composable
fun <T : View> EchoAndroidView(
    modifier: Modifier = Modifier,
    factory: (Context) -> T,
    update: (T, List<Event>, (Event) -> Unit) -> Unit,
    events: List<Event>,
    callback: (Event) -> Unit = {}
) {
    AndroidView(
        modifier = modifier.testTag("mapViewWrapper"),
        factory = factory,
        update = { update(it, events, callback) }
    )
}

/**
 * This composable will draw a map that displays events.
 *
 * @param events The list of events to be displayed by the map. Whenever this list changes, the
 *   event markers in the map will be automatically updated to match the list.
 * @param callback The function to be called when a marker on the map is clicked on. The [Event]
 *   corresponding to the marker will be passed as an argument to this function.
 */
@Composable
fun MapDrawer(modifier: Modifier = Modifier, events: List<Event>, callback: (Event) -> Unit = {}) {
    val provider = OsmdroidMapViewProvider()
    EchoAndroidView(
        modifier = modifier,
        factory = provider::factory,
        // Function that will be called when the view has been
        // inflated or state read in this function has been updated
        // AndroidView will recompose whenever said state changes
        update = provider::update,
        events = events,
        callback = callback
    )
}
