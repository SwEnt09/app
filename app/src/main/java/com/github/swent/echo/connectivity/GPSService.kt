package com.github.swent.echo.connectivity

import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.flow.StateFlow

/** A GPS service. Allows the location to be accessed either as */
interface GPSService {
    val userLocation: StateFlow<LatLng?>

    fun currentUserLocation(): LatLng?

    fun refreshUserLocation()
}
