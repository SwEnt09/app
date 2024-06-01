package com.github.swent.echo.compose.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.testTag
import com.github.swent.echo.data.model.Event
import com.mapbox.mapboxsdk.geometry.LatLng

/**
 * A simple map view provider that uses a [ComposeView] to display a list of events. No map is shown
 * in this implementation.
 */
class SimpleMapViewProvider : MapViewProvider<ComposeView> {
    override fun factory(
        context: android.content.Context,
        withLocation: Boolean,
        onCreate: () -> Unit,
        onLongPress: (LatLng) -> Unit,
    ): ComposeView {
        val view = ComposeView(context)
        onCreate()
        return view
    }

    override fun update(
        view: ComposeView,
        events: List<Event>,
        callback: (Event) -> Unit,
        withLocation: Boolean
    ) {
        view.setContent {
            Column {
                events.forEach { event ->
                    Text(
                        text = event.title,
                        modifier =
                            Modifier.clickable(onClick = { callback(event) })
                                .testTag("event_marker_" + event.eventId),
                    )
                }
            }
        }
    }

    override fun setSavedCameraPosition(newPosition: LatLng, zoomLevel: Double) {}
}
