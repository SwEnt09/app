package com.github.swent.echo.data.supabase.serializer

import com.github.swent.echo.data.model.SemesterEPFL
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SemesterSerializerTest {
    private lateinit var semesterSerializer: SemesterSerializer
    private lateinit var encoder: Encoder
    private lateinit var decoder: Decoder

    @Before
    fun setUp() {
        semesterSerializer = SemesterSerializer
        encoder = mockk(relaxed = true)
        decoder = mockk(relaxed = true)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun testDescriptor() {
        val descriptor = semesterSerializer.descriptor
        assertEquals("Semester", descriptor.serialName)
        assertEquals(PrimitiveKind.STRING, descriptor.kind)
    }

    @Test
    fun testSerialize() {
        val semester = SemesterEPFL.BA1
        semesterSerializer.serialize(encoder, semester)
        verify { encoder.encodeString(semester.name) }
    }

    @Test
    fun testDeserialize() {
        semesterSerializer.deserialize(decoder)
        verify { decoder.decodeString() }
    }
}
