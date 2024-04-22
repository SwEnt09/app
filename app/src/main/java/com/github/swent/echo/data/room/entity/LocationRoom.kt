package com.github.swent.echo.data.room.entity

import androidx.room.ColumnInfo
import com.github.swent.echo.data.model.Location

data class LocationRoom(
    @ColumnInfo(name = "location_name") val name: String,
    val latitude: Double,
    val longitude: Double,
) {
    constructor(location: Location) : this(location.name, location.lat, location.long)

    fun toLocation(): Location = Location(name, latitude, longitude)
}
