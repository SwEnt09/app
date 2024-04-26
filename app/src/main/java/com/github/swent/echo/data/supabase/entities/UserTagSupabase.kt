package com.github.swent.echo.data.supabase.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserTagSupabase(
    @SerialName("user_id") val userId: String,
    @SerialName("tag_id") val tagId: String
)
