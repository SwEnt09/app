package com.github.swent.echo.data.model

import java.time.ZonedDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val eventId: String,
    val creatorId: String,
    val organizerId: String,
    val organizerName: String,
    val title: String,
    val description: String,
    val location: Location,
    @Contextual val startDate: ZonedDateTime,
    @Contextual val endDate: ZonedDateTime,
    val tags: Set<Tag>,
    val participantCount: Int,
    val maxParticipants: Int,
    val imageId: Int
)
