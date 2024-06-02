package com.github.swent.echo.data.supabase.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Annotated helper class representing an event join relation in Supabase. Needed for the
 * serialization of the double join queries.
 *
 * @property userId unique id of the user subscribed to the event
 * @property eventId unique id of the event the user is subscribed to
 */
@Serializable
data class EventJoinSupabase(
    @SerialName("user_id") val userId: String,
    @SerialName("event_id") val eventId: String
)
