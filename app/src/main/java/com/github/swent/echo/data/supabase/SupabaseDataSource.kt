package com.github.swent.echo.data.supabase

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import com.github.swent.echo.data.supabase.entities.AssociationSubscriptionSupabase
import com.github.swent.echo.data.supabase.entities.AssociationSupabase
import com.github.swent.echo.data.supabase.entities.EventJoinSupabase
import com.github.swent.echo.data.supabase.entities.EventSupabase
import com.github.swent.echo.data.supabase.entities.EventSupabaseSetter
import com.github.swent.echo.data.supabase.entities.EventTagSupabase
import com.github.swent.echo.data.supabase.entities.UserProfileSupabase
import com.github.swent.echo.data.supabase.entities.UserProfileSupabaseSetter
import com.github.swent.echo.data.supabase.entities.UserTagSupabase
import com.github.swent.echo.data.supabase.entities.toAssociations
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.storage.storage
import kotlin.text.StringBuilder
import kotlinx.coroutines.delay

class SupabaseDataSource(private val supabase: SupabaseClient) : RemoteDataSource {

    /**
     * Transforms a list from usual toString format into the format required by Supabase /
     * PostgREST, namely surrounded by normal parentheses rather than brackets.
     *
     * @param listOfIds: a list of strings / ids which will be filtered
     * @return String of comma separated values surrounded by parentheses
     */
    private fun toFilterList(listOfIds: List<String>): String {
        val output: StringBuilder = StringBuilder()
        output.append("(")
        for (elemId in listOfIds) {
            output.append(elemId)
            output.append(",")
        }
        if (output.length > 1) {
            output.deleteAt(output.length - 1)
        }
        output.append(")")
        return output.toString()
    }

    companion object {
        const val QUERY_ASSOCIATION =
            "association_id, name, description, association_url, association_tags!association_tags_association_id_fkey(tags!association_tags_tag_id_fkey(tag_id, name, parent_id))"
        const val QUERY_EVENT =
            "event_id, user_profiles!public_events_creator_id_fkey(user_id, name), associations!public_events_organizer_id_fkey(association_id, name), title, description, event_tags!event_tags_event_id_fkey(tags!event_tags_tag_id_fkey(tag_id, name, parent_id)), location_name, location_lat, location_long, start_date, end_date, participant_count, max_participants, image_id"
        const val RETRY_DELAY_MILLI = 200L
    }

    override suspend fun getAssociation(
        associationId: String,
        maxRetriesCount: UInt
    ): Association? {
        return try {
            supabase
                .from("associations")
                .select(Columns.raw(QUERY_ASSOCIATION)) {
                    filter { eq("association_id", associationId) }
                }
                .decodeSingle<AssociationSupabase>()
                .toAssociation()
        } catch (e: Exception) {
            when (e) {
                is BadRequestRestException -> null
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getAssociation(associationId, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getAssociations(
        associations: List<String>,
        maxRetriesCount: UInt
    ): List<Association> {
        return try {
            supabase
                .from("associations")
                .select(Columns.raw(QUERY_ASSOCIATION)) {
                    filter { isIn("association_id", associations) }
                }
                .decodeList<AssociationSupabase>()
                .toAssociations()
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getAssociations(associations, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getAssociationsNotIn(
        associationIds: List<String>,
        maxRetriesCount: UInt
    ): List<Association> {
        return try {
            supabase
                .from("associations")
                .select(Columns.raw(QUERY_ASSOCIATION)) {
                    filter {
                        filterNot("association_id", FilterOperator.IN, toFilterList(associationIds))
                    }
                }
                .decodeList<AssociationSupabase>()
                .toAssociations()
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getAssociationsNotIn(associationIds, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getAllAssociations(maxRetriesCount: UInt): List<Association> {
        return try {
            supabase
                .from("associations")
                .select(Columns.raw(QUERY_ASSOCIATION))
                .decodeList<AssociationSupabase>()
                .toAssociations()
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getAllAssociations(maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getEvent(eventId: String, maxRetriesCount: UInt): Event? {
        return try {
            supabase
                .from("events")
                .select(Columns.raw(QUERY_EVENT)) { filter { eq("event_id", eventId) } }
                .decodeSingle<EventSupabase>()
                .toEvent()
        } catch (e: Exception) {
            when (e) {
                is NoSuchElementException -> null
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getEvent(eventId, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun createEvent(event: Event, maxRetriesCount: UInt): String {
        val eventSupabase = EventSupabaseSetter(event).copy(eventId = null)
        val eventId =
            try {
                supabase
                    .from("events")
                    .insert(eventSupabase) { select() }
                    .decodeSingle<EventSupabaseSetter>()
                    .eventId!!
            } catch (e: Exception) {
                when (e) {
                    is HttpRequestException -> {
                        if (maxRetriesCount == 0u) throw e
                        delay(RETRY_DELAY_MILLI)
                        return createEvent(event, maxRetriesCount - 1u)
                    }
                    else -> throw e
                }
            }
        setEventTagRelations(eventId, event.tags)
        return eventId
    }

    override suspend fun setEvent(event: Event, maxRetriesCount: UInt) {
        val eventSupabase = EventSupabaseSetter(event)
        try {
            supabase.from("events").upsert(eventSupabase, onConflict = "event_id")
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return setEvent(event, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
        deleteAllEventTagRelationsForEvent(event.eventId)
        setEventTagRelations(event.eventId, event.tags)
    }

    private suspend fun setEventTagRelations(
        eventId: String,
        tags: Set<Tag>,
        maxRetriesCount: UInt = 10u
    ) {
        val eventTags = tags.map { tag -> EventTagSupabase(tag.tagId, eventId) }
        try {
            supabase.from("event_tags").upsert(eventTags, onConflict = "tag_id,event_id")
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(100L)
                    return setEventTagRelations(eventId, tags, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    private suspend fun deleteAllEventTagRelationsForEvent(
        eventId: String,
        maxRetriesCount: UInt = RemoteDataSource.RETRY_MAX
    ) {
        try {
            supabase.from("event_tags").delete { filter { eq("event_id", eventId) } }
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(100L)
                    return deleteAllEventTagRelationsForEvent(eventId, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun deleteEvent(event: Event, maxRetriesCount: UInt) {
        try {
            supabase.from("events").delete { filter { eq("event_id", event.eventId) } }
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return deleteEvent(event, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getEventsNotIn(
        eventIds: List<String>,
        maxRetriesCount: UInt
    ): List<Event> {
        return try {
            supabase
                .from("events")
                .select(Columns.raw(QUERY_EVENT)) {
                    filter { filterNot("event_id", FilterOperator.IN, toFilterList(eventIds)) }
                }
                .decodeList<EventSupabase>()
                .map { event -> event.toEvent() }
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getEventsNotIn(eventIds, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getAllEvents(maxRetriesCount: UInt): List<Event> {
        return try {
            supabase
                .from("events")
                .select(Columns.raw(QUERY_EVENT))
                .decodeList<EventSupabase>()
                .map { event -> event.toEvent() }
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getAllEvents(maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun joinEvent(
        userId: String,
        eventId: String,
        maxRetriesCount: UInt
    ): Boolean {
        try {
            supabase.from("event_joins").upsert(EventJoinSupabase(userId, eventId))
        } catch (e: Exception) {
            when (e) {
                is UnknownRestException -> return false
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) return false
                    delay(RETRY_DELAY_MILLI)
                    return joinEvent(userId, eventId, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
        return true
    }

    override suspend fun leaveEvent(
        userId: String,
        eventId: String,
        maxRetriesCount: UInt
    ): Boolean {
        try {
            supabase.from("event_joins").delete {
                filter {
                    and {
                        eq("user_id", userId)
                        eq("event_id", eventId)
                    }
                }
            }
        } catch (e: Exception) {
            when (e) {
                is UnknownRestException -> return false
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) return false
                    delay(RETRY_DELAY_MILLI)
                    return leaveEvent(userId, eventId, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
        return true
    }

    override suspend fun getJoinedEvents(userId: String, maxRetriesCount: UInt): List<Event> {
        return try {
            supabase
                .from("events")
                .select(
                    Columns.raw(
                        QUERY_EVENT +
                            ", event_joins!event_joins_event_id_fkey!inner(event_id, user_id)"
                    )
                ) {
                    filter { eq("event_joins.user_id", userId) }
                }
                .decodeList<EventSupabase>()
                .map { event -> event.toEvent() }
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getJoinedEvents(userId, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getJoinedEventsNotIn(
        userId: String,
        eventIds: List<String>,
        maxRetriesCount: UInt
    ): List<Event> {
        return try {
            supabase
                .from("joined_event_view")
                .select(Columns.raw(QUERY_EVENT + ", join_user_id")) {
                    filter {
                        and {
                            eq("join_user_id", userId)
                            filterNot("event_id", FilterOperator.IN, toFilterList(eventIds))
                        }
                    }
                }
                .decodeList<EventSupabase>()
                .map { event -> event.toEvent() }
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getJoinedEventsNotIn(userId, eventIds, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getTag(tagId: String, maxRetriesCount: UInt): Tag? {
        return try {
            return supabase.from("tags").select() { filter { eq("tag_id", tagId) } }.decodeSingle()
        } catch (e: Exception) {
            when (e) {
                is NoSuchElementException,
                is BadRequestRestException // in case tagId is an empty string
                -> null
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getTag(tagId, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getSubTags(tagId: String, maxRetriesCount: UInt): List<Tag> {
        return try {
            supabase.from("tags").select() { filter { eq("parent_id", tagId) } }.decodeList()
        } catch (e: Exception) {
            when (e) {
                is NoSuchElementException,
                is BadRequestRestException // in case tagId is an empty string
                -> emptyList<Tag>()
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getSubTags(tagId, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getSubTagsNotIn(
        tagId: String,
        childTagIds: List<String>,
        maxRetriesCount: UInt
    ): List<Tag> {
        return try {
            supabase
                .from("tags")
                .select {
                    filter {
                        and {
                            eq("parent_id", tagId)
                            filterNot("tag_id", FilterOperator.IN, toFilterList(childTagIds))
                        }
                    }
                }
                .decodeList()
        } catch (e: Exception) {
            when (e) {
                is NoSuchElementException,
                is BadRequestRestException // in case tagId is an empty string
                -> emptyList<Tag>()
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getSubTagsNotIn(tagId, childTagIds, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getAllTags(maxRetriesCount: UInt): List<Tag> {
        return try {
            supabase.from("tags").select().decodeList<Tag>()
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getAllTags(maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getAllTagsNotIn(tagIds: List<String>, maxRetriesCount: UInt): List<Tag> {
        return try {
            supabase
                .from("tags")
                .select { filter { filterNot("tag_id", FilterOperator.IN, toFilterList(tagIds)) } }
                .decodeList()
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getAllTagsNotIn(tagIds, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getUserProfile(userId: String, maxRetriesCount: UInt): UserProfile? {
        return try {
            supabase
                .from("user_profiles")
                .select(
                    Columns.raw(
                        "user_id, name, semester, section, user_tags!user_tags_user_id_fkey(tags!user_tags_tag_id_fkey(tag_id, name, parent_id)), committee_members!public_associationMembers_user_id_fkey(associations!public_associationMembers_association_id_fkey(association_id, name)), association_subscriptions!association_subscription_user_id_fkey(associations!association_subscription_association_id_fkey(association_id, name))"
                    )
                ) {
                    filter { eq("user_id", userId) }
                }
                .decodeSingle<UserProfileSupabase>()
                .toUserProfile()
        } catch (e: Exception) {
            when (e) {
                is NoSuchElementException,
                is BadRequestRestException // in case requested id is an empty string
                -> null
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getUserProfile(userId, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun setUserProfile(userProfile: UserProfile, maxRetriesCount: UInt) {
        val userProfileSupabaseSetter = UserProfileSupabaseSetter(userProfile)
        try {
            supabase.from("user_profiles").upsert(userProfileSupabaseSetter, onConflict = "user_id")
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return setUserProfile(userProfile, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }

        setUserTags(userProfile)

        setUserAssociationSubscriptions(userProfile)
    }

    private suspend fun setUserTags(userProfile: UserProfile, maxRetriesCount: UInt = 10u) {
        try {
            supabase.from("user_tags").delete { filter { eq("user_id", userProfile.userId) } }
            val userTagsSupabase =
                userProfile.tags.map { tag -> UserTagSupabase(userProfile.userId, tag.tagId) }
            supabase.from("user_tags").upsert(userTagsSupabase)
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(100L)
                    return setUserTags(userProfile, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    private suspend fun setUserAssociationSubscriptions(
        userProfile: UserProfile,
        maxRetriesCount: UInt = 10u
    ) {
        try {
            supabase.from("association_subscriptions").delete {
                filter { eq("user_id", userProfile.userId) }
            }
            val associationSubscriptionSupabase =
                userProfile.associationsSubscriptions.map { association ->
                    AssociationSubscriptionSupabase(userProfile.userId, association.associationId)
                }
            supabase.from("association_subscriptions").upsert(associationSubscriptionSupabase)
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return setUserAssociationSubscriptions(userProfile, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun deleteUserProfile(userProfile: UserProfile, maxRetriesCount: UInt) {
        try {
            supabase.from("user_profiles").delete { filter { eq("user_id", userProfile.userId) } }
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return deleteUserProfile(userProfile, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun getUserProfilePicture(userId: String, maxRetriesCount: UInt): ByteArray? {
        return try {
            supabase.storage.from("user-profile-picture").downloadAuthenticated("$userId.jpeg")
        } catch (e: Exception) {
            when (e) {
                is NotFoundRestException -> null
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return getUserProfilePicture(userId, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun setUserProfilePicture(
        userId: String,
        picture: ByteArray,
        maxRetriesCount: UInt
    ) {
        try {
            supabase.storage
                .from("user-profile-picture")
                .upload("$userId.jpeg", picture, upsert = true)
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return setUserProfilePicture(userId, picture, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }

    override suspend fun deleteUserProfilePicture(userId: String, maxRetriesCount: UInt) {
        try {
            supabase.storage.from("user-profile-picture").delete("$userId.jpeg")
        } catch (e: Exception) {
            when (e) {
                is HttpRequestException -> {
                    if (maxRetriesCount == 0u) throw e
                    delay(RETRY_DELAY_MILLI)
                    return deleteUserProfilePicture(userId, maxRetriesCount - 1u)
                }
                else -> throw e
            }
        }
    }
}
