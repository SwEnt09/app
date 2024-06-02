package com.github.swent.echo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A simplified version of the Association data class, used to embed into other data classes
 * avoiding to embed the complete heavy Association data class.
 *
 * @property associationId the unique id of the association
 * @property name the name of the association
 */
@Serializable
data class AssociationHeader(
    @SerialName("association_id") val associationId: String,
    val name: String,
) {
    companion object {
        val EMPTY = AssociationHeader("", "")

        fun fromAssociation(association: Association?): AssociationHeader? {
            return if (association != null)
                AssociationHeader(association.associationId, association.name)
            else null
        }
    }
}

fun List<Association>.toAssociationHeader(): List<AssociationHeader> {
    return map { AssociationHeader.fromAssociation(it)!! }
}
