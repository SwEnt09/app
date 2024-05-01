package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Section
import com.github.swent.echo.data.model.Semester
import com.github.swent.echo.data.model.UserProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileSupabase(
    @SerialName("user_id") val userId: String,
    val name: String,
    val semester: Semester?,
    val section: Section?,
    @SerialName("user_tags") val tags: List<TagHelper>,
    @SerialName("committee_members") val committeeMembers: List<AssociationHelper>,
    @SerialName("association_subscriptions") val associationSubscriptions: List<AssociationHelper>,
) {
    constructor(
        userProfile: UserProfile
    ) : this(
        userProfile.userId,
        userProfile.name,
        userProfile.semester,
        userProfile.section,
        userProfile.tags.map { tag -> TagHelper(tag) },
        userProfile.committeeMember.map { committeeMember -> AssociationHelper(committeeMember) },
        userProfile.associationsSubscriptions.map { associationSubscriptions ->
            AssociationHelper(associationSubscriptions)
        },
    )

    fun toUserProfile(): UserProfile {
        return UserProfile(
            userId,
            name,
            semester,
            section,
            tags.map { tagHelper -> tagHelper.tag }.toHashSet(),
            committeeMembers.map { associationHelper -> associationHelper.association }.toHashSet(),
            associationSubscriptions
                .map { associationHelper -> associationHelper.association }
                .toHashSet(),
        )
    }
}
