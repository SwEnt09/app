package com.github.swent.echo.data.model

data class UserProfile(
    val userId: String,
    val name: String,
    val semester: Semester?,
    val section: Section?,
    val tags: Set<Tag>,
    val committeeMember: Set<AssociationHeader>,
    val associationsSubscriptions: Set<AssociationHeader>,
) {
    fun toEventCreator(): EventCreator = EventCreator(userId, name)

    companion object {
        val EMPTY =
            UserProfile(
                userId = "",
                name = "",
                semester = null,
                section = null,
                tags = setOf(),
                committeeMember = setOf(),
                associationsSubscriptions = setOf()
            )
    }
}
