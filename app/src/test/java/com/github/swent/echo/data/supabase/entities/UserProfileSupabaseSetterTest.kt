package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.AssociationHeader
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import org.junit.Assert
import org.junit.Test

class UserProfileSupabaseSetterTest {
    val userProfile =
        UserProfile(
            "userId",
            "name",
            SemesterEPFL.BA6,
            SectionEPFL.IN,
            setOf(Tag("tagId", "tagName")),
            setOf(AssociationHeader("associationId", "associationName")),
            setOf(AssociationHeader("associationId", "associationName")),
        )

    val userProfileSupabaseSetter =
        UserProfileSupabaseSetter(
            userProfile.userId,
            userProfile.name,
            userProfile.semester,
            userProfile.section,
        )

    @Test
    fun `UserProfileSupabaseSetter constructor returns correct object`() {
        val userProfileSupabaseSetterConstructed = UserProfileSupabaseSetter(userProfile)
        Assert.assertEquals(userProfileSupabaseSetter, userProfileSupabaseSetterConstructed)
    }
}
