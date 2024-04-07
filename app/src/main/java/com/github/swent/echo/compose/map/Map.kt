package com.github.swent.echo.compose.map

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.github.swent.echo.data.model.Event

@Composable
fun <T : View> EchoAndroidView(
    modifier: Modifier = Modifier,
    factory: (Context) -> T,
    update: (T, List<Event>, (Event) -> Unit) -> Unit,
    events: MutableState<List<Event>>,
    callback: (Event) -> Unit = {}
) {
    AndroidView(
        modifier = modifier,
        factory = factory,
        update = { update(it, events.value, callback) }
    )
}

/**
 * This composable will draw a map that displays events.
 *
 * @param context Needed to initialize the [MapViewProvider][IMapViewProvider]. Can be the activity
 *   itself if this function is called from an activity, or the `current` field of a
 *   [LocalContext][androidx.compose.ui.platform.LocalContext] if it is called from another
 *   composable.
 * @param events The list of events to be displayed by the map. Whenever this list changes, the
 *   event markers in the map will be automatically updated to match the list. Note that this
 *   parameter is a [MutableState] containing a `List`, so the correct way to instantiate it is the
 *   following: `val myEvents = remember { mutableStateOf(emptyList<Event>()) }`. Do NOT use the
 *   `by` keyword. You can then pass it as an argument to this composable. If you want to change the
 *   value stored in the [MutableState] to trigger a redraw of the markers, you can write
 *   `myEvents.value = newEventList`.
 * @param callback The function to be called when a marker on the map is clicked on. The [Event]
 *   corresponding to the marker will be passed as an argument to this function.
 */
@Composable
fun MapDrawer(
    modifier: Modifier = Modifier,
    context: Context,
    events: MutableState<List<Event>>,
    callback: (Event) -> Unit = {}
) {
    val provider = OsmdroidMapViewProvider(context)
    EchoAndroidView(
        modifier = modifier.testTag("mapViewWrapper"),
        factory = provider::factory,
        // Function that will be called when the view has been
        // inflated or state read in this function has been updated
        // AndroidView will recompose whenever said state changes
        update = provider::update,
        events = events,
        callback = callback
    )
}
