package com.github.swent.echo.data.supabase.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Annotated helper class representing an association subscription relation in Supabase. Needed for
 * the serialization of the double join queries.
 *
 * @property userId unique id of the user subscribed to the association
 * @property associationId unique id of the association the user is subscribed to
 */
@Serializable
data class AssociationSubscriptionSupabase(
    @SerialName("user_id") val userId: String,
    @SerialName("association_id") val associationId: String
)
