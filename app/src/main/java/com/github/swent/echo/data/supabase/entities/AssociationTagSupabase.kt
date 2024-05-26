package com.github.swent.echo.data.supabase.entities

import kotlinx.serialization.SerialName

data class AssociationTagSupabase(
    @SerialName("tag_id") val tagId: String,
    @SerialName("association_id") val associationId: String
)
