package com.github.swent.echo.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AssociationHeaderTest {
    @Test
    fun toAssociationHeaderTest() {
        val assocs =
            listOf(
                Association("id1", "name1", "description1", "url1", setOf(Tag("tagid", "tagname"))),
                Association("id2", "name2", "description2", "url2", setOf(Tag.EMPTY)),
            )
        val assocheaders =
            listOf(
                AssociationHeader("id1", "name1"),
                AssociationHeader("id2", "name2"),
            )
        assertEquals(assocheaders, assocs.toAssociationHeader())
    }
}
