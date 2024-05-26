package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Association
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
