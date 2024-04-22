package com.github.swent.echo.data.room.entity

import androidx.room.ColumnInfo
import com.github.swent.echo.data.model.EventCreator

data class EventCreatorRoom(
    val userId: String,
    @ColumnInfo(name = "creator_name") val name: String,
) {
    constructor(eventCreator: EventCreator) : this(eventCreator.userId, eventCreator.name)

    fun toEventCreator(): EventCreator = EventCreator(userId, name)
}
