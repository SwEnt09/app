package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert
import org.junit.Test

class EventSupabaseSetterTest {
    val event =
        Event(
            "eventId",
            EventCreator("creatorId", "creatorName"),
            Association("organizerId", "organizerName", "organizerDescription"),
            "title",
            "description",
            Location("locationName", 0.2, 0.3),
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(1714056891), ZoneId.systemDefault()),
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(1714066891), ZoneId.systemDefault()),
            setOf(Tag("tagId", "tagName")),
            12,
            25,
            2,
        )

    val eventSupabaseSetter =
        EventSupabaseSetter(
            event.eventId,
            event.creator.userId,
            event.organizer?.associationId,
            event.title,
            event.description,
            event.location.name,
            event.location.lat,
            event.location.long,
            event.startDate.toEpochSecond(),
            event.endDate.toEpochSecond(),
            event.participantCount,
            event.maxParticipants,
            event.imageId,
        )

    @Test
    fun `EventSupabaseSetter constructor returns correct object`() {
        val eventSupabaseSetterConstructed = EventSupabaseSetter(event)
        Assert.assertEquals(eventSupabaseSetter, eventSupabaseSetterConstructed)
    }
}
