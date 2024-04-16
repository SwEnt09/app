package com.github.swent.echo.data

import com.github.swent.echo.compose.map.MAP_CENTER
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.UserProfile
import java.time.ZonedDateTime

// This should come from the repository

val SAMPLE_EVENTS: List<Event> =
    listOf(
        Event(
            eventId = "a",
            creator = UserProfile("a", ""),
            organizer = Association("a", "a", ""),
            title = "Bowling Event",
            description = "",
            location = Location("Location 1", MAP_CENTER.toGeoPoint()),
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            tags = emptySet(),
            participantCount = 5,
            maxParticipants = 8,
            imageId = 0
        ),
        Event(
            eventId = "b",
            creator = UserProfile("a", ""),
            organizer = Association("a", "a", ""),
            title = "Swimming Event",
            description = "",
            location =
                Location("Location 2", MAP_CENTER.toGeoPoint().destinationPoint(1000.0, 90.0)),
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            tags = emptySet(),
            participantCount = 4,
            maxParticipants = 30,
            imageId = 0
        )
    )
