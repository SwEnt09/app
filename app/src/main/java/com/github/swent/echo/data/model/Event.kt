package com.github.swent.echo.data.model

import java.util.Date
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val eventId: String,
    val creatorId: String,
    val organizerId: String,
    val title: String,
    val description: String,
    val location: Location,
    @Contextual val startDate: Date,
    @Contextual val endDate: Date,
    val tags: Set<Tag>
)
