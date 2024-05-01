package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventSupabase(
    @SerialName("event_id") val eventId: String,
    @SerialName("user_profiles") val creator: EventCreator,
    @SerialName("associations") val organizer: Association?,
    val title: String,
    val description: String,
    @SerialName("location_name") val locationName: String,
    @SerialName("location_lat") val locationLat: Double,
    @SerialName("location_long") val locationLong: Double,
    @SerialName("start_date") val startDate: Long,
    @SerialName("end_date") val endDate: Long,
    @SerialName("event_tags") val tags: List<TagHelper>,
    @SerialName("participant_count") val participantCount: Int?,
    @SerialName("max_participants") val maxParticipants: Int?,
    @SerialName("image_id") val imageId: Int?
) {
    constructor(
        event: Event
    ) : this(
        event.eventId,
        event.creator,
        event.organizer,
        event.title,
        event.description,
        event.location.name,
        event.location.lat,
        event.location.long,
        event.startDate.toEpochSecond(),
        event.endDate.toEpochSecond(),
        event.tags.map { tag -> TagHelper(tag) },
        event.maxParticipants,
        event.participantCount,
        event.imageId
    )

    fun toEvent(): Event {
        return Event(
            eventId,
            creator,
            organizer,
            title,
            description,
            Location(locationName, locationLat, locationLong),
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(startDate), ZoneId.systemDefault()),
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(endDate), ZoneId.systemDefault()),
            tags.map { tagHelper -> tagHelper.tag }.toHashSet(),
            participantCount!!,
            maxParticipants!!,
            imageId!!
        )
    }
}
