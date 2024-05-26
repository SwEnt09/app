package com.github.swent.echo.data.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.github.swent.echo.data.model.Event
import java.time.ZonedDateTime

@Entity
data class EventRoom(
    @PrimaryKey val eventId: String,
    @Embedded val creator: EventCreatorRoom,
    @Embedded val organizer: AssociationHeaderRoom?,
    val title: String,
    val description: String,
    @Embedded val location: LocationRoom,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime,
    val participantCount: Int,
    val maxParticipants: Int,
    val imagePath: String?,
    /** The time of the last update in seconds */
    val timestamp: Long = ZonedDateTime.now().toEpochSecond(),
) {
    constructor(
        event: Event
    ) : this(
        event.eventId,
        EventCreatorRoom(event.creator),
        AssociationHeaderRoom.fromAssociationHeader(event.organizer),
        event.title,
        event.description,
        LocationRoom(event.location),
        event.startDate,
        event.endDate,
        event.participantCount,
        event.maxParticipants,
        null,
    )
}

@Entity(
    primaryKeys = ["eventId", "tagId"],
    indices = [Index("tagId")],
    foreignKeys =
        [
            ForeignKey(
                entity = EventRoom::class,
                parentColumns = ["eventId"],
                childColumns = ["eventId"],
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
data class EventTagCrossRef(
    val eventId: String,
    val tagId: String,
)

data class EventWithTags(
    @Embedded val event: EventRoom,
    @Relation(
        parentColumn = "eventId",
        entityColumn = "tagId",
        associateBy = Junction(EventTagCrossRef::class),
    )
    val tags: List<TagRoom>,
) {

    fun toEvent(): Event =
        Event(
            event.eventId,
            event.creator.toEventCreator(),
            event.organizer?.toAssociationHeader(),
            event.title,
            event.description,
            event.location.toLocation(),
            event.startDate,
            event.endDate,
            tags.toTagSet(),
            event.participantCount,
            event.maxParticipants,
            0, // TODO: Use the correct [imageId] or remove image logic form the code base.
        )
}
