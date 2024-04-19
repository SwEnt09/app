package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventSupabaseSetter(
    @SerialName("event_id") val eventId: String,
    @SerialName("creator_id") val creatorId: String,
    @SerialName("organizer_id") val organizerId: String?,
    val title: String,
    val description: String,
    @SerialName("location_name") val locationName: String,
    @SerialName("location_lat") val locationLat: Double,
    @SerialName("location_long") val locationLong: Double,
    @SerialName("start_date") val startDate: Long,
    @SerialName("end_date") val endDate: Long,
    @SerialName("participant_count") val participantCount: Int?,
    @SerialName("max_participants") val maxParticipants: Int?,
    @SerialName("image_id") val imageId: Int?
) {
    constructor(
        event: Event
    ) : this(
        event.eventId,
        event.creator.userId,
        event.organizer?.associationId,
        event.title,
        event.description,
        event.location.name,
        event.location.lat,
        event.location.long,
        event.startDate.toEpochSecond(),
        event.endDate.toEpochSecond(),
        event.maxParticipants,
        event.participantCount,
        event.imageId
    )
}
