package com.github.swent.echo.data.supabase.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Annotated helper class representing a tag added to a user relation in Supabase. Needed for the
 * serialization of the double join queries.
 *
 * @property userId unique id of the user
 * @property tagId unique id of the tag
 */
@Serializable
data class UserTagSupabase(
    @SerialName("user_id") val userId: String,
    @SerialName("tag_id") val tagId: String
)
