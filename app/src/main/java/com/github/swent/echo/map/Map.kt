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
@Preview
@Composable
fun MapDrawer(modifier: Modifier = Modifier) {
    var mapCenter by remember {
        mutableStateOf(LAUSANNE_GEO_POINT)
    }
    Column (modifier = modifier){
        Configuration.getInstance().userAgentValue = LocalContext.current.packageName
        Configuration.getInstance().osmdroidBasePath = LocalContext.current.cacheDir
        Text(text = "base path" + Configuration.getInstance().osmdroidBasePath.absolutePath)
        Text(text = "tile cache" + Configuration.getInstance().osmdroidTileCache.absolutePath)
        AndroidView(
            modifier = modifier, // Occupy the max size in the Compose UI tree
            factory = { context ->
                // Sets tile cache directory. May be needed in case the app doesn't have access to external storage.
                // See the permissions section in https://github.com/osmdroid/osmdroid/wiki/How-to-use-the-osmdroid-library-(Kotlin).
                // Also see https://stackoverflow.com/questions/39019687/using-osmdroid-without-getting-access-to-external-storage
                // It's possible that this is not the place to do this tough.

                //Configuration.getInstance().osmdroidTileCache = context.cacheDir


                // Creates view
                MapView(context, MapTileProviderBasic(context)).apply{
                    setTileSource(TileSourceFactory.MAPNIK)
                    setOnClickListener {
                        mapCenter = mapCenter.destinationPoint(100.0, 0.0)
                    }
                    controller.setZoom(15.0)
                }
            },
            update = { view ->
                // View has been inflated or state read in this block has been updated

                // AndroidView will recompose whenever the state changes given that the mapCenter variable is read here
                view.controller.setCenter(mapCenter)
            }
        )
    }
}
