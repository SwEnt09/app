package com.github.swent.echo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A creator of an event. A simplified version of the Event data class, used to embed into other
 * data classes avoiding to embed the complete heavy Event data class.
 *
 * @property userId The ID of the user who created the event.
 * @property name The name of the creator. This same name as in the [UserProfile] corresponding to
 *   the [userId].
 */
@Serializable
data class EventCreator(
    @SerialName("user_id") val userId: String,
    val name: String,
) {
    companion object {
        val EMPTY = EventCreator("", "")
    }
}
