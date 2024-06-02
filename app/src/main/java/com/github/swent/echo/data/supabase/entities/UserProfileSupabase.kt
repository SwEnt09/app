package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Section
import com.github.swent.echo.data.model.Semester
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.supabase.serializer.SectionSerializer
import com.github.swent.echo.data.supabase.serializer.SemesterSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Serializable version of the UserProfile data class. Used to retrieve user profiles from Supabase.
 *
 * @property userId the unique id of the user
 * @property name the name of the user
 * @property semester the semester of the user
 * @property section the section of the user
 * @property tags the list of TagHelper embedding tags related to the user
 * @property committeeMember the list of associations where the user is a committee member of
 * @property associationsSubscriptions the list of association subscriptions of the user
 */
@Serializable
data class UserProfileSupabase(
    @SerialName("user_id") val userId: String,
    val name: String,
    @Serializable(with = SemesterSerializer::class) val semester: Semester?,
    @Serializable(with = SectionSerializer::class) val section: Section?,
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
