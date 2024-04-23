package com.github.swent.echo.data.model

import org.junit.Assert.*
import org.junit.Test

class SectionEPFLTest {

    @Test
    fun `should convert correctly all values of the enum`() {
        for (section in SectionEPFL.entries) {
            assertEquals(section.name.toSectionEPFL(), section)
        }
    }

    @Test
    fun `should return null for unknown values`() {
        assertNull("unknown section".toSectionEPFL())
    }
}
