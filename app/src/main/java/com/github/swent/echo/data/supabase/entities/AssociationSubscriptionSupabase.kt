package com.github.swent.echo.data.supabase.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// This annotated helper class is needed for the serialization of the double join queries to work
@Serializable
data class AssociationSubscriptionSupabase(
    @SerialName("user_id") val userId: String,
    @SerialName("association_id") val associationId: String
)
