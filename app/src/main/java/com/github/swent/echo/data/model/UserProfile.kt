package com.github.swent.echo.data.model

/**
 * The user profile data class.
 *
 * @property userId the unique id of the user
 * @property name the name of the user
 * @property semester the semester of the user
 * @property section the section of the user
 * @property tags the tags related to the user
 * @property committeeMember the list of associations where the user is a committee member
 * @property associationsSubscriptions the list of association subscriptions of the user
 */
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
