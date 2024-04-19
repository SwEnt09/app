package com.github.swent.echo.data.supabase.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventTagSupabase(
    @SerialName("tag_id") val tagId: String,
    @SerialName("event_id") val eventId: String
)
