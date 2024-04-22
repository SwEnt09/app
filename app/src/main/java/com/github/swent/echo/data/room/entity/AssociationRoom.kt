package com.github.swent.echo.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.swent.echo.data.model.Association

@Entity
data class AssociationRoom(
    @PrimaryKey val associationId: String,
    val name: String,
    val description: String
) {
    constructor(
        association: Association
    ) : this(
        association.associationId,
        association.name,
        association.description,
    )

    fun toAssociation(): Association = Association(associationId, name, description)
}
