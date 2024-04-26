package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Section
import com.github.swent.echo.data.model.Semester
import com.github.swent.echo.data.model.UserProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileSupabaseSetter(
    @SerialName("user_id") val userId: String,
    val name: String,
    val semester: Semester?,
    val section: Section?,
) {
    constructor(
        userProfile: UserProfile
    ) : this(userProfile.userId, userProfile.name, userProfile.semester, userProfile.section)
}
