package com.github.swent.echo.data.model

import java.time.ZonedDateTime
import kotlinx.serialization.Contextual

/**
 * The event data class.
 *
 * @property eventId the unique id of the event
 * @property creator the creator of the event
 * @property organizer the association organizing the event, if null the organizer is the event
 *   creator
 * @property title the title of the event
 * @property description the description of the event
 * @property location the location of the event
 * @property startDate the start date of the event
 * @property endDate the end date of the event
 * @property tags the tags related to the event
 * @property participantCount the number of participants of the event
 * @property maxParticipants the maximum number of participant of the event
 * @property imageId the picture reference of the event
 */
data class Event(
    val eventId: String,
    val creator: EventCreator,
    val organizer: AssociationHeader?,
    val title: String,
    val description: String,
    val location: Location,
    @Contextual val startDate: ZonedDateTime,
    @Contextual val endDate: ZonedDateTime,
    val tags: Set<Tag>,
    val participantCount: Int,
    val maxParticipants: Int,
    val imageId: Int
) : DataModel() {
    companion object {
        val EMPTY =
            Event(
                "",
                EventCreator.EMPTY,
                null,
                "",
                "",
                Location.EMPTY,
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                emptySet(),
                0,
                0,
                0
            )
    }
}
