package com.github.swent.echo.connectivity

import com.github.swent.echo.compose.map.MAP_CENTER
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SimpleGPSService : GPSService {
    private var _location = MutableStateFlow<LatLng?>(null)

    override fun currentUserLocation(): LatLng? = _location.value

    override val userLocation: StateFlow<LatLng?> = _location.asStateFlow()

    override fun refreshUserLocation() {
        _location.compareAndSet(null, MAP_CENTER.toLatLng())
    }
}
