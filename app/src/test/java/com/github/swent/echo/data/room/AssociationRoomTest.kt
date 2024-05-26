package com.github.swent.echo.data.room

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.AssociationHeader
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.room.entity.AssociationRoom
import com.github.swent.echo.data.room.entity.AssociationWithTags
import com.github.swent.echo.data.room.entity.TagRoom
import com.github.swent.echo.data.room.entity.toAssociationHeaderSet
import com.github.swent.echo.data.room.entity.toAssociations
import org.junit.Assert.assertEquals
import org.junit.Test

class AssociationRoomTest {
    @Test
    fun associationConversionTest() {
        val assoc =
            listOf(
                Association("id", "name", "desc", "url", setOf(Tag("tagid", "tagname", "parentid")))
            )
        val assocRoom = assoc.map { AssociationRoom(it) }
        val assocWithTags =
            assocRoom.map {
                AssociationWithTags(it, listOf(TagRoom("tagid", "parentid", "tagname")))
            }
        val assocHeaderSet = setOf(AssociationHeader("id", "name"))

        assertEquals(assoc, assocWithTags.toAssociations())
        assertEquals(assocHeaderSet, assocRoom.toAssociationHeaderSet())
    }
}
