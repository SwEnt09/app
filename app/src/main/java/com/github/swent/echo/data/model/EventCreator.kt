package com.github.swent.echo.data.model

import kotlinx.serialization.Serializable

/**
 * A creator of an event.
 *
 * @property userId The ID of the user who created the event.
 * @property name The name of the creator. This same name as in the [UserProfile] corresponding to
 *   the [userId].
 */
@Serializable
data class EventCreator(
    val userId: String,
    val name: String,
)
