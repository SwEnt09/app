package com.github.swent.echo.data

import com.github.swent.echo.compose.map.MAP_CENTER
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import java.time.ZonedDateTime

// This should come from the repository

val SAMPLE_EVENTS: List<Event> =
    listOf(
        Event(
            eventId = "a",
            creator = EventCreator("a", ""),
            organizer = Association("a", "a", ""),
            title = "Bowling Event",
            description = "",
            location = Location("Location 1", MAP_CENTER.toGeoPoint()),
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            tags = setOf(Tag("64", "Bowling"), Tag("1", "Sport")),
            participantCount = 5,
            maxParticipants = 8,
            imageId = 0
        ),
        Event(
            eventId = "b",
            creator = EventCreator("a", ""),
            organizer = Association("a", "a", ""),
            title = "Swimming Event",
            description = "",
            location =
                Location("Location 2", MAP_CENTER.toGeoPoint().destinationPoint(1000.0, 0.0)),
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            tags = setOf(Tag("63", "Swimming"), Tag("1", "Sport")),
            participantCount = 4,
            maxParticipants = 30,
            imageId = 0
        ),
        Event(
            eventId = "c",
            creator = EventCreator("b", "Chad"),
            organizer = Association("B", "EPFL Mewing Group", ""),
            title = "Badminton Tournament",
            description = "Only the greatest humans shall participate. Win... or die.",
            location =
                Location(
                    "Third lamppost on the right",
                    MAP_CENTER.toGeoPoint().destinationPoint(1000.0, 90.0)
                ),
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now().plusDays(200),
            tags = setOf(Tag("62", "Badminton"), Tag("1", "Sport")),
            participantCount = 1,
            maxParticipants = 200,
            imageId = 0
        ),
        Event(
            eventId = "d",
            creator = EventCreator("d", "Matt Mercer"),
            organizer = Association("C", "EPFL D&D club", ""),
            title = "D&D oneshot",
            description = "Come play D&D with us ! We have cookies.",
            location =
                Location("Baldur's Gate", MAP_CENTER.toGeoPoint().destinationPoint(500.0, 30.0)),
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now().plusDays(1),
            tags = setOf(Tag("60", "Dungeons and Dragons"), Tag("61", "Games")),
            participantCount = 3,
            maxParticipants = 5,
            imageId = 0
        )
    )
