package com.github.swent.echo.data.room

import com.github.swent.echo.data.model.AssociationHeader
import com.github.swent.echo.data.room.entity.AssociationHeaderRoom
import com.github.swent.echo.data.room.entity.AssociationRoom
import org.junit.Assert.assertEquals
import org.junit.Test

internal class AssociationHeaderRoomTest {
    @Test
    fun toAndFromAssociationHeaderTest() {
        val headerRoom = AssociationHeaderRoom("id", "name")
        val header = AssociationHeader("id", "name")
        assertEquals(header, headerRoom.toAssociationHeader())
        assertEquals(headerRoom, AssociationHeaderRoom.fromAssociationHeader(header))
        assertEquals(null, AssociationHeaderRoom.fromAssociationHeader(null))
    }

    @Test
    fun fromAssociationRoomTest() {
        val assocRoom = AssociationRoom("id", "name", "description", "url", 0)
        val headerRoom = AssociationHeaderRoom("id", "name")
        assertEquals(headerRoom, AssociationHeaderRoom.fromAssociationRoom(assocRoom))
    }
}
