package com.github.swent.echo.compose.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.swent.echo.R
import com.mapbox.mapboxsdk.geometry.LatLng
import org.ramani.compose.CameraPosition
import org.ramani.compose.Circle
import org.ramani.compose.MapLibre

@Composable
fun TestLocationCaller() {
    var pos by remember { mutableStateOf(MAP_CENTER.toLatLng()) }
    Surface {
        Column {
            Box(modifier = Modifier.weight(1F)) {
                Column {
                    Text(text = "Latitude: " + pos.latitude.toString())
                    Text(text = "Longitude: " + pos.longitude.toString())
                }
            }
            Box(modifier = Modifier.weight(10F)) {
                LocationSelector(locationCallback = { pos = it })
            }
        }
    }
}

@Composable
fun LocationSelector(modifier: Modifier = Modifier, locationCallback: (LatLng) -> Unit) {
    val context = LocalContext.current
    val styleUrl =
        context.getString(R.string.maptiler_base_style_url) +
            context.getString(R.string.maptiler_api_key)
    MapLibre(
        modifier = modifier.fillMaxSize(),
        styleUrl = styleUrl,
        cameraPosition = CameraPosition(target = MAP_CENTER.toLatLng(), zoom = DEFAULT_ZOOM)
    ) {
        Circle(
            center = MAP_CENTER.toLatLng(),
            radius = 20F,
            isDraggable = true,
            color = "Red",
            opacity = 1F,
            onDragFinished = locationCallback
        )
    }
}
