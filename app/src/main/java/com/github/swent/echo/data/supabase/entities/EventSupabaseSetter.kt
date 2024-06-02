package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Serializable version of the Event data class. Used to store events in Supabase.
 *
 * @property eventId the unique id of the event
 * @property creatorId the id of the creator of the event
 * @property organizerId the id of the association organizing the event, if null the organizer is
 *   the event creator
 * @property title the title of the event
 * @property description the description of the event
 * @property locationName the name of the location of the event
 * @property locationLat lat coords of the location of the event
 * @property locationLong long coords of the location of the event
 * @property startDate the start date of the event
 * @property endDate the end date of the event
 * @property participantCount the number of participants of the event
 * @property maxParticipants the maximum number of participant of the event
 * @property imageId the picture reference of the event
 */
@Serializable
data class EventSupabaseSetter(

    // having the default value set to null here is needed so that the serializer
    // completely ignores the attribute on insertion when we want Supabase to generate the
    // uuid eventId
    @SerialName("event_id") val eventId: String? = null,
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
        event.participantCount,
        event.maxParticipants,
        event.imageId
    )
}
