package com.github.swent.echo.data.model

import java.util.Date

data class Event(
    val eventId: String,
    val organizerId: String,
    val title: String,
    val description: String,
    val location: Location,
    val startDate: Date,
    val endDate: Date
)
