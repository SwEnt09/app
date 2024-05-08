package com.github.swent.echo.data.supabase.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventJoinSupabase(
    @SerialName("user_id") val userId: String,
    @SerialName("event_id") val eventId: String
)
