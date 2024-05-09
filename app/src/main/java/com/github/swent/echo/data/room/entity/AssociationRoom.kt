package com.github.swent.echo.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.swent.echo.data.model.Association
import java.time.ZonedDateTime

@Entity
data class AssociationRoom(
    @PrimaryKey val associationId: String,
    val name: String,
    val description: String,
    /** The time of the last update in seconds */
    val timestamp: Long = ZonedDateTime.now().toEpochSecond(),
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

fun List<AssociationRoom>.toAssociationSet(): Set<Association> = map { it.toAssociation() }.toSet()

fun Set<Association>.toAssociationRoomList(): List<AssociationRoom> = map { AssociationRoom(it) }
