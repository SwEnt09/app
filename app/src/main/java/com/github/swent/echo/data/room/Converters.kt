package com.github.swent.echo.data.room

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): ZonedDateTime? {
        return value?.let { timestamp ->
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: ZonedDateTime?): Long? {
        return date?.toInstant()?.toEpochMilli()
    }
}
