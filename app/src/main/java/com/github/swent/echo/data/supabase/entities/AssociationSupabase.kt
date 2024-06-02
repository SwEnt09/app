package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Association
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Annotated version of the Association data class. Used for supabase queries.
 *
 * @property associationId the unique id of the association
 * @property name the name of the association
 * @property description the description of the association
 * @property url the url to a website or social media account of the association
 * @property relatedTags a list of TagHelpers enclosing the tags related to the association
 */
@Serializable
data class AssociationSupabase(
    @SerialName("association_id") val associationId: String,
    val name: String,
    val description: String,
    @SerialName("association_url") val url: String?,
    @SerialName("association_tags") val relatedTags: List<TagHelper>
) {
    constructor(
        association: Association
    ) : this(
        association.associationId,
        association.name,
        association.description,
        association.url,
        association.relatedTags.map { tag -> TagHelper(tag) },
    )

    fun toAssociation(): Association {
        return Association(
            associationId,
            name,
            description,
            url,
            relatedTags.map { tagHelper -> tagHelper.tag }.toHashSet(),
        )
    }
}

fun List<AssociationSupabase>.toAssociations() = map { it.toAssociation() }
