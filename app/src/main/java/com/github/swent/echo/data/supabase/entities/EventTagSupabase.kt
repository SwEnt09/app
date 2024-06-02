package com.github.swent.echo.data.supabase.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Annotated helper class representing a tag added to an event relation in Supabase. Needed for the
 * serialization of the double join queries.
 *
 * @property tagId unique id of the tag assigned to the event
 * @property eventId unique id of the event the tag is assigned to
 */
@Serializable
data class EventTagSupabase(
    @SerialName("tag_id") val tagId: String,
    @SerialName("event_id") val eventId: String
)
