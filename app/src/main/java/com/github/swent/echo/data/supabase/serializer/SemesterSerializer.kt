package com.github.swent.echo.data.supabase.serializer

import com.github.swent.echo.data.model.Semester
import com.github.swent.echo.data.model.toSemesterEPFL
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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
