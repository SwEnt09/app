package com.github.swent.echo.data.room.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["userId", "eventId"],
    indices = [Index("eventId")],
    foreignKeys =
        [
            ForeignKey(
                entity = UserProfileRoom::class,
                parentColumns = ["userId"],
                childColumns = ["userId"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = EventRoom::class,
                parentColumns = ["eventId"],
                childColumns = ["eventId"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
)
data class JoinedEventRoom(
    val userId: String,
    val eventId: String,
)
