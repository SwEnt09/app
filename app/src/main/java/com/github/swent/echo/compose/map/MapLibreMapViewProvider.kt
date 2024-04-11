package com.github.swent.echo.compose.map

import android.content.Context
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Event
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView

class MapLibreMapViewProvider : IMapViewProvider<MapView> {

    private lateinit var mapView: MapView

    private fun drawMarkers(map: MapLibreMap, events: List<Event>) {
        events.forEach {
            map.addMarker(
                MarkerOptions()
                    .setPosition(LatLng(it.location.lat, it.location.long))
                    .title(it.title)
            )
        }
    }

    override fun factory(context: Context): MapView {
        MapLibre.getInstance(context)
        mapView = MapView(context)
        // val styleUrl =
        // "https://api.maptiler.com/maps/streets-v2/style.json?key=8tGwLwPFCHwJ5D8g7pic "
        val styleUrl =
            context.getString(R.string.maptiler_base_style_url) +
                context.getString(R.string.maptiler_api_key)
        mapView.onCreate(null)
        mapView.getMapAsync { map ->
            // Set the style after mapView was loaded
            map.setStyle(styleUrl) {
                map.uiSettings.setAttributionMargins(15, 0, 0, 15)
                // Set the map view center
                map.cameraPosition =
                    CameraPosition.Builder()
                        .target(MAP_CENTER.toLatLng())
                        .zoom(DEFAULT_ZOOM)
                        .bearing(2.0)
                        .build()
            }
        }
        return mapView
    }

    override fun update(view: MapView, events: List<Event>, callback: (Event) -> Unit) {
        view.getMapAsync { drawMarkers(it, events) }
    }
}
