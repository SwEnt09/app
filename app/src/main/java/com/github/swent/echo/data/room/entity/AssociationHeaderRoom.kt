package com.github.swent.echo.data.room.entity

import androidx.room.ColumnInfo
import com.github.swent.echo.data.model.AssociationHeader

data class AssociationHeaderRoom(
    val associationId: String,
    @ColumnInfo(name = "organizer_name") val name: String,
) {
    fun toAssociationHeader(): AssociationHeader = AssociationHeader(associationId, name)

    companion object {
        fun fromAssociationHeader(associationHeader: AssociationHeader?): AssociationHeaderRoom? {
            return if (associationHeader != null) {
                AssociationHeaderRoom(associationHeader.associationId, associationHeader.name)
            } else {
                null
            }
        }

        fun fromAssociationRoom(associationRoom: AssociationRoom): AssociationHeaderRoom {
            return AssociationHeaderRoom(associationRoom.associationId, associationRoom.name)
        }
    }
}
