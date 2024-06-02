package com.github.swent.echo.data.supabase.serializer

import com.github.swent.echo.data.model.Semester
import com.github.swent.echo.data.model.toSemesterEPFL
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Custom Kotlin Serializer for the [Semester] data class. Needed to store [Semester] objects on
 * Supabase.
 */
object SemesterSerializer : KSerializer<Semester> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Semester", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Semester {
        val string = decoder.decodeString()
        return string.toSemesterEPFL()!!
    }

    override fun serialize(encoder: Encoder, value: Semester) {
        encoder.encodeString(value.name)
    }
}
