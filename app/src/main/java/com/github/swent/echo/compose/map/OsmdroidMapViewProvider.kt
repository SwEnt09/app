package com.github.swent.echo.compose.map

import android.content.Context
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

open class OsmdroidMapViewProvider(context: Context) : IMapViewProvider<MapView> {

    companion object {
        private val tileSource = TileSourceFactory.MAPNIK

        const val ZOOM_DEFAULT = 15.0
        val LAUSANNE_GEO_POINT = GeoPoint(46.5197, 6.6323)
    }

    private lateinit var mapView: MapView

    init {
        Configuration.getInstance().apply {
            userAgentValue = context.packageName
            osmdroidBasePath = context.cacheDir
        }
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

    override fun update(view: MapView) {
        // view.controller.setCenter(LAUSANNE_GEO_POINT)
    }

    fun getCenter() = mapView.mapCenter

    fun getZoom() = mapView.zoomLevelDouble

    fun getClipToOutline() = mapView.clipToOutline
}
