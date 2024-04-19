package com.github.swent.echo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    @SerialName("user_id") val userId: String,
    val name: String,
    val semester: Semester?,
    val section: Section?,
    val tags: Set<Tag>
)
