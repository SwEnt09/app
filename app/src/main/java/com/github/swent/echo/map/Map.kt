package com.github.swent.echo.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration

// osmdroid libraries
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView


val LAUSANNE_GEO_POINT: GeoPoint = GeoPoint(46.5197, 6.6323)
const val ZOOM_DEFAULT = 15.0
@Preview
@Composable
fun MapDrawer(modifier: Modifier = Modifier) {
    // var trigger by remember { mutableStateOf(...) }
    Configuration.getInstance().userAgentValue = LocalContext.current.packageName
    Configuration.getInstance().osmdroidBasePath = LocalContext.current.cacheDir
    AndroidView(
        modifier = modifier,
        factory = { context ->
            // Creates view
            MapView(context, MapTileProviderBasic(context)).apply{
                setTileSource(TileSourceFactory.MAPNIK)
                // setOnClickListener { ... }
                controller.setZoom(ZOOM_DEFAULT)
                controller.setCenter(LAUSANNE_GEO_POINT)
            }
        },
        update = { view ->
            // View has been inflated or state read in this block has been updated
            // AndroidView will recompose whenever the state changes
            view.controller.setCenter(LAUSANNE_GEO_POINT)
        }
    )
}
