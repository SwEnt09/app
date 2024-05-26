package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Tag
import org.junit.Assert.assertEquals
import org.junit.Test

class AssociationSupabaseTest {
    @Test
    fun toAssociationTest() {
        val assoc = Association("id", "name", "desc", "url", setOf(Tag.EMPTY))
        val assocSupa = AssociationSupabase(assoc)
        assertEquals(assoc, assocSupa.toAssociation())
        assertEquals(listOf(assoc), listOf(assocSupa).toAssociations())
    }
}
