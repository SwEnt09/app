package com.github.swent.echo.compose.map

import android.annotation.SuppressLint
import android.content.Context
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Event
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.LocationComponentOptions
import org.maplibre.android.location.engine.LocationEngineRequest
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

class MapLibreMapViewProvider : IMapViewProvider<MapView> {

    private var canDisplayLocation = false

    private fun redrawMarkers(map: MapLibreMap, events: List<Event>, callback: (Event) -> Unit) {
        map.markers.forEach { map.removeMarker(it) }
        events.forEach {
            val markerBuilder = MarkerOptions().setPosition(it.location.toLatLng()).title(it.title)
            map.addMarker(markerBuilder)
        }
        val h = map.markers.zip(events).toMap()
        map.setOnMarkerClickListener { marker ->
            h[marker]?.let { callback(it) }
            true
        }
    }

    @SuppressLint("MissingPermission")
    private fun displayLocation(context: Context, map: MapLibreMap, style: Style) {
        val locationComponent = map.locationComponent
        val locationComponentOptions =
            LocationComponentOptions.builder(context).pulseEnabled(true).build()
        val locationComponentActivationOptions =
            LocationComponentActivationOptions.builder(context, style)
                .locationComponentOptions(locationComponentOptions)
                .useDefaultLocationEngine(true)
                .locationEngineRequest(
                    LocationEngineRequest.Builder(750)
                        .setFastestInterval(750)
                        .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                        .build()
                )
                .build()
        locationComponent.activateLocationComponent(locationComponentActivationOptions)
        locationComponent.isLocationComponentEnabled = true
        locationComponent.cameraMode = CameraMode.TRACKING

        // locationComponent!!.forceLocationUpdate(lastLocation)
    }

    override fun factory(context: Context): MapView {
        MapLibre.getInstance(context)
        val mapView = MapView(context)
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
                if (canDisplayLocation) {
                    displayLocation(context, map, it)
                }
            }
        }
        return mapView
    }

    override fun update(view: MapView, events: List<Event>, callback: (Event) -> Unit) {
        view.getMapAsync { redrawMarkers(it, events, callback) }
    }

    override fun enableLocation() {
        canDisplayLocation = true
    }

    override fun disableLocation() {
        canDisplayLocation = false
    }
}
