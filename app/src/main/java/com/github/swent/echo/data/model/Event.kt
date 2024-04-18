package com.github.swent.echo.data.model

import java.time.ZonedDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    @SerialName("event_id") val eventId: String,
    val creator: UserProfile,
    val organizer: Association?,
    val title: String,
    val description: String,
    val location: Location,
    @SerialName("start_date") @Contextual val startDate: ZonedDateTime,
    @SerialName("end_date") @Contextual val endDate: ZonedDateTime,
    val tags: Set<Tag>,
    @SerialName("participant_count") val participantCount: Int,
    @SerialName("max_participants") val maxParticipants: Int,
    @SerialName("image_id") val imageId: Int
) {
    companion object {
        val EMPTY =
            Event(
                "",
                UserProfile("", "", null, null, setOf()),
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
