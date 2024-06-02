package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.AssociationHeader
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Serializable version of the Event data class. Used to retrieve events from Supabase.
 *
 * @property eventId the unique id of the event
 * @property creator the creator of the event
 * @property organizer the association organizing the event, if null the organizer is the event
 *   creator
 * @property title the title of the event
 * @property description the description of the event
 * @property locationName the name of the location of the event
 * @property locationLat lat coords of the location of the event
 * @property locationLong long coords of the location of the event
 * @property startDate the start date of the event
 * @property endDate the end date of the event
 * @property tags the tags related to the event
 * @property participantCount the number of participants of the event
 * @property maxParticipants the maximum number of participant of the event
 * @property imageId the picture reference of the event
 */
@Serializable
data class EventSupabase(
    @SerialName("event_id") val eventId: String,
    @SerialName("user_profiles") val creator: EventCreator,
    @SerialName("associations") val organizer: AssociationHeader?,
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
        event.participantCount,
        event.maxParticipants,
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
