package com.github.swent.echo.data.model

sealed class DataModel {
    fun getId(): String =
        when (this) {
            is Association -> this.associationId
            is Event -> this.eventId
            is Tag -> this.tagId
            is UserProfile -> this.userId
        }
}
