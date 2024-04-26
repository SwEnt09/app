package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class EventSupabaseTest {
    val event =
        Event(
            "eventId",
            EventCreator("creatorId", "creatorName"),
            Association("organizerId", "organizerName", "organizerDescription"),
            "title",
            "description",
            Location("locationName", 0.2, 0.3),
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(1714056891), ZoneId.of("UTC")),
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(1714056891), ZoneId.of("UTC")),
            setOf(Tag("tagId", "tagName")),
            0,
            0,
            0,
        )

    val eventSupabase =
        EventSupabase(
            event.eventId,
            event.creator,
            event.organizer,
            event.title,
            event.description,
            event.location.name,
            event.location.lat,
            event.location.long,
            event.startDate.toEpochSecond(),
            event.endDate.toEpochSecond(),
            event.tags.map { tag -> TagHelper(tag) },
            event.participantCount,
            event.maxParticipants,
            event.imageId,
        )

    @Test
    fun `EventSupabase constructor returns correct object`() {
        val eventSupabaseConstructed = EventSupabase(event)
        assertEquals(eventSupabase, eventSupabaseConstructed)
    }

    @Test
    fun `toEvent returns correct Event object`() {
        val eventConstructed = eventSupabase.toEvent()
        assertEquals(event, eventConstructed)
    }
}
