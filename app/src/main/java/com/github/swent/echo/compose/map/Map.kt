package com.github.swent.echo.compose.map

// osmdroid libraries

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

fun updateMapView(view: MapView, newCenter: GeoPoint) {
    view.apply { controller.setCenter(newCenter) }
}

@Composable
fun <T : View> MapDrawer(modifier: Modifier = Modifier, provider: IMapViewProvider<T>) {
    // var trigger by remember { mutableStateOf(...) }
    AndroidView(
        modifier = modifier.testTag("mapViewWrapper"),
        factory = { provider.factory(it) },
        // Function that will be called when the view has been
        // inflated or state read in this function has been updated
        // AndroidView will recompose whenever said state changes
        update = { provider.update(it) }
    )
}
