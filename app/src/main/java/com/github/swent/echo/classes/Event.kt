package com.github.swent.echo.classes

data class Event(
    val eventId: String,
    val organizerId: String,
    val title: String,
    val description: String,
    val location: String, // TODO: Change to Location object, once it is implemented
    val startDate: String,
    val endDate: String
)
