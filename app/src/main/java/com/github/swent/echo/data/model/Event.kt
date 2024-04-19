package com.github.swent.echo.data.model

import java.time.ZonedDateTime
import kotlinx.serialization.Contextual

data class Event(
    val eventId: String,
    val creator: EventCreator,
    val organizer: Association?,
    val title: String,
    val description: String,
    val location: Location,
    @Contextual val startDate: ZonedDateTime,
    @Contextual val endDate: ZonedDateTime,
    val tags: Set<Tag>,
    val participantCount: Int,
    val maxParticipants: Int,
    val imageId: Int
) {
    companion object {
        val EMPTY =
            Event(
                "",
                EventCreator("", ""),
                null,
                "",
                "",
                Location("", 0.0, 0.0),
                ZonedDateTime.now(),
                ZonedDateTime.now(),
                emptySet(),
                0,
                0,
                0
            )
    }
}
