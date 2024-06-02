package com.github.swent.echo.data.model

/**
 * Supertype for other data classes having a unique id. Provides a unified way to get the unique id
 * of the object.
 */
sealed class DataModel {
    fun getId(): String =
        when (this) {
            is Association -> this.associationId
            is Event -> this.eventId
            is Tag -> this.tagId
            is UserProfile -> this.userId
        }
}
