package com.github.swent.echo.data.room.entity

import androidx.room.Embedded
import androidx.room.Entity
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
    val organizerId: String?,
    val title: String,
    val description: String,
    @Embedded val location: LocationRoom,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime,
    val participantCount: Int,
    val maxParticipants: Int,
    val imagePath: String?,
) {
    constructor(
        event: Event
    ) : this(
        event.eventId,
        EventCreatorRoom(event.creator),
        event.organizer?.associationId,
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

@Entity(primaryKeys = ["eventId", "tagId"], indices = [Index("tagId")])
data class EventTagCrossRef(
    val eventId: String,
    val tagId: String,
)

data class EventWithOrganizerAndTags(
    @Embedded val event: EventRoom,
    @Relation(
        parentColumn = "organizerId",
        entityColumn = "associationId",
    )
    val organizer: AssociationRoom?,
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
            organizer?.toAssociation(),
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
