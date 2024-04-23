package com.github.swent.echo.data.model

import org.junit.Assert.*
import org.junit.Test

class SemesterEPFLTest {

    @Test
    fun `should convert correctly all values of the enum`() {
        for (semester in SemesterEPFL.entries) {
            assertEquals(semester.name.toSemesterEPFL(), semester)
        }
    }

    @Test
    fun `should return null for unknown values`() {
        assertNull("unknown semester".toSemesterEPFL())
    }
}
