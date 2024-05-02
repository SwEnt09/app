package com.github.swent.echo.data

import com.github.swent.echo.compose.map.MAP_CENTER
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import java.time.ZonedDateTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import org.maplibre.android.geometry.LatLng

// This should come from the repository

val x: android.location.Location = android.location.Location("")

fun LatLng.toDestPt(distanceInMeters: Double, bearingInDegrees: Double): LatLng {
    val d = distanceInMeters * 8.983e-6
    val la = d * cos(bearingInDegrees * PI / 180.0)
    val lo = d * sin(bearingInDegrees * PI / 180.0)
    return LatLng(this.latitude + la, this.longitude + lo)
}

val SAMPLE_EVENTS: List<Event> =
    listOf(
        Event(
            eventId = "a",
            creator = EventCreator("a", ""),
            organizer = Association("a", "a", ""),
            title = "Bowling Event",
            description = "",
            location = Location("Location 1", MAP_CENTER.toLatLng()),
            startDate = ZonedDateTime.now().plusDays(2),
            endDate = ZonedDateTime.now().plusDays(3),
            tags = setOf(Tag("64", "Bowling"), Tag("1", "Sport"), Tag("65", "EPFL")),
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
            location = Location("Location 2", MAP_CENTER.toLatLng().toDestPt(1000.0, 0.0)),
            startDate = ZonedDateTime.now().plusDays(3),
            endDate = ZonedDateTime.now().plusDays(6),
            tags = setOf(Tag("63", "Swimming"), Tag("1", "Sport"), Tag("66", "IN")),
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
                    MAP_CENTER.toLatLng().toDestPt(1000.0, 90.0)
                ),
            startDate = ZonedDateTime.now().plusDays(4),
            endDate = ZonedDateTime.now().plusDays(200),
            tags = setOf(Tag("62", "Badminton"), Tag("1", "Sport"), Tag("67", "BA6")),
            participantCount = 200,
            maxParticipants = 200,
            imageId = 0
        ),
        Event(
            eventId = "d",
            creator = EventCreator("d", "Matt Mercer"),
            organizer = null,
            title = "D&D oneshot",
            description = "Come play D&D with us ! We have cookies.",
            location = Location("Baldur's Gate", MAP_CENTER.toLatLng().toDestPt(500.0, 30.0)),
            startDate = ZonedDateTime.now().plusDays(1),
            endDate = ZonedDateTime.now().plusDays(2),
            tags =
                setOf(
                    Tag("60", "Dungeons and Dragons"),
                    Tag("61", "Games"),
                    Tag("65", "EPFL"),
                    Tag("66", "IN"),
                    Tag("67", "BA6")
                ),
            participantCount = 3,
            maxParticipants = 5,
            imageId = 0
        )
    )
