package com.github.swent.echo.compose.map

import android.content.Context
import com.github.swent.echo.data.model.Event
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * Provides a [MapView] to be displayed by an
 * [AndroidView][androidx.compose.ui.viewinterop.AndroidView].
 *
 * @param initContext An initial [Context] is needed to properly initialize the `osmdroid`
 *   [Configuration]. This can simply be the activity itself if this constructor is called from an
 *   activity, or the [LocalContext][androidx.compose.ui.platform.LocalContext]`.current` if it is
 *   called from a composable.
 */
open class OsmdroidMapViewProvider(
    private val initContext: Context,
) : IMapViewProvider<MapView> {

    companion object {
        private val tileSource = TileSourceFactory.MAPNIK

        const val ZOOM_DEFAULT = 15.0
        val LAUSANNE_GEO_POINT = GeoPoint(46.5197, 6.6323)
    }

    private lateinit var mapView: MapView

    init {
        Configuration.getInstance().apply {
            userAgentValue = initContext.packageName
            osmdroidBasePath = initContext.cacheDir
        }
    }

    fun getCenter(): IGeoPoint = mapView.mapCenter

    fun getZoom() = mapView.zoomLevelDouble

    fun getClipToOutline() = mapView.clipToOutline

    private fun MapView.drawMarker(e: Event, callback: (Event) -> Unit) {
        val marker = Marker(this)
        marker.apply {
            setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
            // To get an custom marker, use initContext.getDrawable(R.drawable.test_pin)
            // 'R' refers to com.github.swent.echo.R
            icon = null
            title = e.title
            position = e.location.toGeoPoint()
            setOnMarkerClickListener { _, _ ->
                // Shows marker title by default
                callback(e)
                true
            }
        }
        overlays.add(marker)
    }

    private fun MapView.drawAllMarkers(events: List<Event>, callback: (Event) -> Unit) {
        events.forEach { drawMarker(it, callback) }
        invalidate()
    }

    override fun factory(context: Context): MapView {
        mapView =
            MapView(context, MapTileProviderBasic(context)).apply {
                setTileSource(tileSource)
                setMultiTouchControls(true)
                clipToOutline = true
                // setOnClickListener { ... }
                controller.setZoom(ZOOM_DEFAULT)
                controller.setCenter(LAUSANNE_GEO_POINT)
            }
        return mapView
    }

    override fun update(view: MapView, events: List<Event>, callback: (Event) -> Unit) {
        mapView.apply { drawAllMarkers(events, callback) }
    }
}
