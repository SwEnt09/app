package com.github.swent.echo.connectivity

import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.flow.StateFlow

/** A service that provides the user's current location if it's accessible. */
interface GPSService {

    /** The user location as a state flow. */
    val userLocation: StateFlow<LatLng?>

    /** The current user location. */
    fun currentUserLocation(): LatLng?

    /** Force the service to refresh the user location */
    fun refreshUserLocation()
}
