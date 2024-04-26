package com.github.swent.echo.data.model

data class UserProfile(
    val userId: String,
    val name: String,
    val semester: Semester?,
    val section: Section?,
    val tags: Set<Tag>
) {
    fun toEventCreator(): EventCreator = EventCreator(userId, name)
}
