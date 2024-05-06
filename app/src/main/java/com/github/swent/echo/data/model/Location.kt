package com.github.swent.echo.data.model

import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.serialization.Serializable

@Serializable
data class Location(val name: String, val lat: Double, val long: Double) {

    constructor(name: String, point: LatLng) : this(name, point.latitude, point.longitude)

    /**
     * Transform this location into a [LatLng].
     *
     * @return The respective [LatLng].
     */
    fun toLatLng() = LatLng(lat, long)

    companion object {
        val EMPTY = Location(name = "", lat = 0.0, long = 0.0)
    }
}
