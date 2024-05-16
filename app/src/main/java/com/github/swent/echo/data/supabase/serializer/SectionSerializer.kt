package com.github.swent.echo.data.supabase.serializer

import com.github.swent.echo.data.model.Section
import com.github.swent.echo.data.model.toSectionEPFL
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SectionSerializer : KSerializer<Section> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Semester", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Section {
        val string = decoder.decodeString()
        return string.toSectionEPFL()!!
    }

    override fun serialize(encoder: Encoder, value: Section) {
        encoder.encodeString(value.name)
    }
}
