package com.github.swent.echo.compose.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.github.swent.echo.R
import com.mapbox.mapboxsdk.geometry.LatLng
import org.ramani.compose.CameraPosition
import org.ramani.compose.Circle
import org.ramani.compose.MapLibre
import org.ramani.compose.Symbol

/**
 * A Composable to select a location on a map.
 *
 * @param initialLocation The location at which the map must be initialized.
 * @param locationCallback The function to call when the selected location changes.
 */
@Composable
fun LocationSelector(
    modifier: Modifier = Modifier,
    initialLocation: LatLng = MAP_CENTER.toLatLng(),
    locationCallback: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val styleUrl =
        context.getString(R.string.maptiler_base_style_url) +
            context.getString(R.string.maptiler_api_key)
    var markerCenter by remember { mutableStateOf(initialLocation) }
    MapLibre(
        modifier = modifier.testTag("location-selector-map-libre").fillMaxSize(),
        styleUrl = styleUrl,
        cameraPosition = CameraPosition(target = initialLocation, zoom = DEFAULT_ZOOM)
    ) {
        Circle(
            center = markerCenter,
            radius = 30F,
            isDraggable = true,
            color = "Red",
            opacity = 0.1F,
            zIndex = 1,
            onCenterDragged = { markerCenter = it },
            onDragFinished = locationCallback
        )
        Symbol(
            center = markerCenter,
            size = 0.5F,
            color = "Red",
            imageId = R.drawable.red_marker,
            isDraggable = false
        )
    }
}

/**
 * A Composable to display a location on a mpa.
 *
 * @param position The location to be displayed.
 */
@Composable
fun LocationDisplayer(
    modifier: Modifier = Modifier,
    position: LatLng,
) {
    val context = LocalContext.current
    val styleUrl =
        context.getString(R.string.maptiler_base_style_url) +
            context.getString(R.string.maptiler_api_key)
    MapLibre(
        modifier = modifier.testTag("location-displayer-map-libre").fillMaxSize(),
        styleUrl = styleUrl,
        cameraPosition = CameraPosition(target = position, zoom = DEFAULT_ZOOM)
    ) {
        Symbol(
            center = position,
            size = 0.5F,
            color = "Red",
            imageId = R.drawable.red_marker,
            isDraggable = false
        )
    }
}
