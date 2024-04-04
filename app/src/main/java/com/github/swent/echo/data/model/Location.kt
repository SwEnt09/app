package com.github.swent.echo.data.model

import org.osmdroid.util.GeoPoint

data class Location(val name: String, val lat: Double, val long: Double) {
    constructor(name: String, point: GeoPoint) : this(name, point.latitude, point.longitude)
    /**
     * Transform this location into a [GeoPoint].
     *
     * @return The respective [GeoPoint]
     */
    fun toGeoPoint() = GeoPoint(lat, long)
}
