package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Section
import com.github.swent.echo.data.model.Semester
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.supabase.serializer.SectionSerializer
import com.github.swent.echo.data.supabase.serializer.SemesterSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileSupabaseSetter(
    @SerialName("user_id") val userId: String,
    val name: String,
    @Serializable(with = SemesterSerializer::class) val semester: Semester?,
    @Serializable(with = SectionSerializer::class) val section: Section?,
) {
    constructor(
        userProfile: UserProfile
    ) : this(userProfile.userId, userProfile.name, userProfile.semester, userProfile.section)
}
