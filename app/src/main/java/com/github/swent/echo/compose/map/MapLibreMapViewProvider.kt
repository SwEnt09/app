package com.github.swent.echo.compose.map

import android.annotation.SuppressLint
import android.content.Context
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Event
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponent
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.LocationComponentOptions
import org.maplibre.android.location.engine.LocationEngineRequest
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

class MapLibreMapViewProvider : IMapViewProvider<MapView> {

    private var mapView: MapView? = null
    private var locationComponent: LocationComponent? = null
    private var firstRecenter = true

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
        locationComponent = map.locationComponent
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
        locationComponent!!.activateLocationComponent(locationComponentActivationOptions)
        locationComponent!!.isLocationComponentEnabled = true
        locationComponent!!.cameraMode = CameraMode.NONE
        locationComponent?.lastKnownLocation?.apply {
            if (firstRecenter) {
                firstRecenter = false
                val pos = LatLng(latitude, longitude)
                map.cameraPosition =
                    CameraPosition.Builder().target(pos).zoom(DEFAULT_ZOOM).bearing(0.0).build()
            }
        }
    }

    override fun factory(context: Context, withLocation: Boolean, onCreate: () -> Unit): MapView {
        MapLibre.getInstance(context)
        val styleUrl =
            context.getString(R.string.maptiler_base_style_url) +
                context.getString(R.string.maptiler_api_key)
        val isInit = (mapView == null)
        if (isInit) {
            // Creating the map for the first time
            mapView = MapView(context)
        }
        mapView?.onCreate(null)
        mapView?.getMapAsync { map ->
            // Set the style after mapView was loaded
            map.setStyle(styleUrl) {
                map.uiSettings.apply {
                    setAttributionMargins(15, 0, 0, 15)
                    isRotateGesturesEnabled = false
                }
                // Set the map view center if we're creating the view
                if (isInit) {
                    map.cameraPosition =
                        CameraPosition.Builder()
                            .target(MAP_CENTER.toLatLng())
                            .zoom(DEFAULT_ZOOM)
                            .bearing(0.0)
                            .build()
                }
                onCreate()
            }
        }
        return mapView!!
    }

    override fun update(
        view: MapView,
        events: List<Event>,
        callback: (Event) -> Unit,
        withLocation: Boolean
    ) {
        view.getMapAsync { map ->
            redrawMarkers(map, events, callback)
            if (withLocation) {
                map.style?.apply { displayLocation(view.context, map, this) }
            }
        }
    }
}
