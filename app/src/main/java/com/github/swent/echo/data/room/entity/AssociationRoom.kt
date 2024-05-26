package com.github.swent.echo.data.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.AssociationHeader
import java.time.ZonedDateTime

@Entity
data class AssociationRoom(
    @PrimaryKey val associationId: String,
    val name: String,
    val description: String,
    val url: String?,
    /** The time of the last update in seconds */
    val timestamp: Long = ZonedDateTime.now().toEpochSecond(),
) {
    constructor(
        association: Association
    ) : this(association.associationId, association.name, association.description, association.url)
}

@Entity(
    primaryKeys = ["associationId", "tagId"],
    indices = [Index("tagId")],
    foreignKeys =
        [
            ForeignKey(
                entity = AssociationRoom::class,
                parentColumns = ["associationId"],
                childColumns = ["associationId"],
                onDelete = CASCADE,
            ),
            ForeignKey(
                entity = TagRoom::class,
                parentColumns = ["tagId"],
                childColumns = ["tagId"],
                onDelete = CASCADE,
            ),
        ],
)
data class AssociationTagCrossRef(
    val associationId: String,
    val tagId: String,
)

data class AssociationWithTags(
    @Embedded val association: AssociationRoom,
    @Relation(
        parentColumn = "associationId",
        entityColumn = "tagId",
        associateBy = Junction(AssociationTagCrossRef::class),
    )
    val tags: List<TagRoom>
) {
    fun toAssociation(): Association =
        Association(
            association.associationId,
            association.name,
            association.description,
            association.url,
            tags.toTagSet(),
        )
}

fun List<AssociationWithTags>.toAssociations(): List<Association> = map { it.toAssociation() }

fun List<AssociationRoom>.toAssociationHeaderSet(): Set<AssociationHeader> =
    map { AssociationHeaderRoom.fromAssociationRoom(it).toAssociationHeader() }.toSet()
