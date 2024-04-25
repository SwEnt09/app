package com.github.swent.echo.data.repository

import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.SAMPLE_EVENTS
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile

/**
 * A simple implementation of the [Repository] interface that stores all data in memory.
 *
 * It uses the [AuthenticationService] to get the current user ID and creates a user profile for it.
 * It uses the [SAMPLE_EVENTS] to populate the repository with sample data.
 */
class SimpleRepository(authenticationService: AuthenticationService) : Repository {

    companion object {
        const val NUM_OF_TOP_LEVEL_TAGS = 3
        const val NUM_OF_HARDCODED_TAGS = 6
    }

    private val associations = mutableSetOf<Association>()
    private val events = mutableListOf<Event>()
    private val tags =
        mutableSetOf(
            Tag("1", "Sport", Repository.ROOT_TAG_ID),
            Tag("2", "Culture", Repository.ROOT_TAG_ID),
            Tag("3", "Music", Repository.ROOT_TAG_ID),
            Tag("4", "Football", "1"),
            Tag("5", "Basketball", "1"),
            Tag("6", "Theatre", "2"),
        )
    private val userProfiles = mutableSetOf<UserProfile>()

    init {
        // Populate the repository with sample data
        SAMPLE_EVENTS.forEach { event ->
            events.add(event)
            tags.addAll(event.tags)
            if (event.organizer != null) {
                associations.add(event.organizer)
            }
            userProfiles.add(
                UserProfile(
                    event.creator.userId,
                    event.creator.name,
                    SemesterEPFL.BA3,
                    SectionEPFL.IN,
                    emptySet(),
                )
            )
        }

        // Add a user profile for the current user
        val userId = authenticationService.getCurrentUserID()
        if (userId != null) {
            userProfiles.add(
                UserProfile(
                    userId,
                    "John Doe",
                    SemesterEPFL.BA3,
                    SectionEPFL.IN,
                    tags.take(2).toSet(),
                )
            )
        }
    }

    override suspend fun getAssociation(associationId: String): Association {
        return associations.find { it.associationId == associationId }!!
    }

    override suspend fun getAllAssociations(): List<Association> {
        return associations.toList()
    }

    override suspend fun getEvent(eventId: String): Event {
        return events.find { it.eventId == eventId }!!
    }

    override suspend fun createEvent(event: Event): String {
        val eventId = events.hashCode().toString()
        events.add(event.copy(eventId = eventId))
        return eventId
    }

    override suspend fun setEvent(event: Event) {
        events.removeIf { it.eventId == event.eventId }
        events.add(event)
    }

    override suspend fun getAllEvents(): List<Event> {
        return events
    }

    override suspend fun getTag(tagId: String): Tag {
        return tags.find { it.tagId == tagId }!!
    }

    override suspend fun getAllTags(): List<Tag> {
        return tags.toList()
    }

    override suspend fun getSubTags(tagId: String): List<Tag> {
        return tags.filter { it.parentId == tagId }
    }

    override suspend fun getUserProfile(userId: String): UserProfile? {
        return userProfiles.find { it.userId == userId }
    }

    override suspend fun setUserProfile(userProfile: UserProfile) {
        userProfiles.removeIf { it.userId == userProfile.userId }
        userProfiles.add(userProfile)
    }
}