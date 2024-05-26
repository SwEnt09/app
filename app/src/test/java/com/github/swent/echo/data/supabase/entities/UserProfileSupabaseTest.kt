package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.AssociationHeader
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import org.junit.Assert
import org.junit.Test

class UserProfileSupabaseTest {
    val userProfile =
        UserProfile(
            "userId",
            "name",
            SemesterEPFL.BA6,
            SectionEPFL.IN,
            setOf(Tag("tagId", "tagName")),
            setOf(AssociationHeader("associationId", "associationName")),
            setOf(AssociationHeader("associationId2", "associationName")),
        )

    val userProfileSupabase =
        UserProfileSupabase(
            userProfile.userId,
            userProfile.name,
            userProfile.semester,
            userProfile.section,
            userProfile.tags.map { tag -> TagHelper(tag) },
            userProfile.committeeMember.map { association -> AssociationHelper(association) },
            userProfile.associationsSubscriptions.map { association ->
                AssociationHelper(association)
            },
        )

    @Test
    fun `UserProfileSupabase constructor returns correct object`() {
        val userProfileSupabaseConstructed = UserProfileSupabase(userProfile)
        Assert.assertEquals(userProfileSupabase, userProfileSupabaseConstructed)
    }

    @Test
    fun `toUserProfile returns correct UserProfile object`() {
        val userProfileConstructed = userProfileSupabase.toUserProfile()
        Assert.assertEquals(userProfile, userProfileConstructed)
    }
}
