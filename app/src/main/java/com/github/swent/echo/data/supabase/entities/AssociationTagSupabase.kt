package com.github.swent.echo.data.supabase.entities

import kotlinx.serialization.SerialName

/**
 * Annotated helper class representing an tag assignment to an association relation in Supabase.
 * Needed for the serialization of the double join queries.
 *
 * @property tagId unique id of the tag assigned to the association
 * @property associationId unique id of the association to which the tag is linked to
 */
data class AssociationTagSupabase(
    @SerialName("tag_id") val tagId: String,
    @SerialName("association_id") val associationId: String
)
