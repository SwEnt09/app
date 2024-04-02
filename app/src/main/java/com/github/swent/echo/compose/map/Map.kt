package com.github.swent.echo.compose.map

// osmdroid libraries

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

val LAUSANNE_GEO_POINT: GeoPoint = GeoPoint(46.5197, 6.6323)
const val ZOOM_DEFAULT = 15.0

fun configureOsmdroid(context: Context) {
    Configuration.getInstance().apply {
        userAgentValue = context.packageName
        osmdroidBasePath = context.cacheDir
    }
}

fun createMapView(context: Context, tileSource: ITileSource): MapView =
    MapView(context, MapTileProviderBasic(context)).apply {
        setTileSource(tileSource)
        // setOnClickListener { ... }
        controller.setZoom(ZOOM_DEFAULT)
        controller.setCenter(LAUSANNE_GEO_POINT)
        clipToOutline = true
    }

fun updateMapView(view: MapView, newCenter: GeoPoint) {
    view.apply {
        controller.setCenter(newCenter)
    }
}

@Preview
@Composable
fun MapDrawer(
    modifier: Modifier = Modifier,
    mapViewFactory: (Context) -> MapView = { createMapView(it, TileSourceFactory.MAPNIK) },
    update: (MapView) -> Unit = { updateMapView(it, LAUSANNE_GEO_POINT) }
) {
    // var trigger by remember { mutableStateOf(...) }
    AndroidView(
        modifier = modifier.testTag("mapViewWrapper"),
        factory = mapViewFactory,
        // Function that will be called when the view has been
        // inflated or state read in this function has been updated
        // AndroidView will recompose whenever said state changes
        update = {
            updateMapView(it, LAUSANNE_GEO_POINT)
        }
    )
}
