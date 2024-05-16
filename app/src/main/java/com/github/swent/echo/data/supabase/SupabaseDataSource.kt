package com.github.swent.echo.data.supabase

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import com.github.swent.echo.data.supabase.entities.AssociationSubscriptionSupabase
import com.github.swent.echo.data.supabase.entities.EventJoinSupabase
import com.github.swent.echo.data.supabase.entities.EventSupabase
import com.github.swent.echo.data.supabase.entities.EventSupabaseSetter
import com.github.swent.echo.data.supabase.entities.EventTagSupabase
import com.github.swent.echo.data.supabase.entities.UserProfileSupabase
import com.github.swent.echo.data.supabase.entities.UserProfileSupabaseSetter
import com.github.swent.echo.data.supabase.entities.UserTagSupabase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.exceptions.UnknownRestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlin.text.StringBuilder

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

    override suspend fun getAssociation(associationId: String): Association? {
        return try {
            supabase
                .from("associations")
                .select() { filter { eq("association_id", associationId) } }
                .decodeSingle()
        } catch (e: BadRequestRestException) {
            null
        }
    }

    override suspend fun getAssociations(associations: List<String>): List<Association> {
        return supabase
            .from("associations")
            .select { filter { isIn("association_id", associations) } }
            .decodeList()
    }

    override suspend fun getAssociationsNotIn(associationIds: List<String>): List<Association> {
        return supabase
            .from("associations")
            .select {
                filter {
                    filterNot("association_id", FilterOperator.IN, toFilterList(associationIds))
                }
            }
            .decodeList()
    }

    override suspend fun getAllAssociations(): List<Association> {
        return supabase.from("associations").select().decodeList<Association>()
    }

    companion object {
        const val QUERY_EVENT =
            "event_id, user_profiles!public_events_creator_id_fkey(user_id, name), associations!public_events_organizer_id_fkey(association_id, name, description), title, description, event_tags!event_tags_event_id_fkey(tags!event_tags_tag_id_fkey(tag_id, name, parent_id)), location_name, location_lat, location_long, start_date, end_date, participant_count, max_participants, image_id"
    }

    override suspend fun getEvent(eventId: String): Event? {
        return try {
            val event =
                supabase
                    .from("events")
                    .select(Columns.raw(QUERY_EVENT)) { filter { eq("event_id", eventId) } }
                    .decodeSingle<EventSupabase>()
            event.toEvent()
        } catch (e: NoSuchElementException) {
            null
        }
    }

    override suspend fun createEvent(event: Event): String {
        val eventSupabase = EventSupabaseSetter(event).copy(eventId = null)
        val eventId =
            supabase
                .from("events")
                .insert(eventSupabase) { select() }
                .decodeSingle<EventSupabaseSetter>()
                .eventId!!
        setEventTagRelations(eventId, event.tags)
        return eventId
    }

    override suspend fun setEvent(event: Event) {
        val eventSupabase = EventSupabaseSetter(event)
        supabase.from("events").upsert(eventSupabase, onConflict = "event_id")
        setEventTagRelations(event.eventId, event.tags)
    }

    private suspend fun setEventTagRelations(eventId: String, tags: Set<Tag>) {
        val eventTags = tags.map { tag -> EventTagSupabase(tag.tagId, eventId) }
        supabase.from("event_tags").upsert(eventTags, onConflict = "tag_id,event_id")
    }

    override suspend fun getEventsNotIn(eventIds: List<String>): List<Event> {
        val events =
            supabase
                .from("events")
                .select(Columns.raw(QUERY_EVENT)) {
                    filter { filterNot("event_id", FilterOperator.IN, toFilterList(eventIds)) }
                }
                .decodeList<EventSupabase>()
        return events.map { event -> event.toEvent() }
    }

    override suspend fun getAllEvents(): List<Event> {
        val events =
            supabase.from("events").select(Columns.raw(QUERY_EVENT)).decodeList<EventSupabase>()
        return events.map { event -> event.toEvent() }
    }

    override suspend fun joinEvent(userId: String, eventId: String): Boolean {
        try {
            supabase.from("event_joins").upsert(EventJoinSupabase(userId, eventId))
        } catch (e: UnknownRestException) {
            return false
        }
        return true
    }

    override suspend fun leaveEvent(userId: String, eventId: String): Boolean {
        try {
            supabase.from("event_joins").delete {
                filter {
                    and {
                        eq("user_id", userId)
                        eq("event_id", eventId)
                    }
                }
            }
        } catch (e: UnknownRestException) {
            return false
        }
        return true
    }

    override suspend fun getJoinedEvents(userId: String): List<Event> {
        val events =
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
        return events.map { event -> event.toEvent() }
    }

    override suspend fun getJoinedEventsNotIn(userId: String, eventIds: List<String>): List<Event> {
        val events =
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
        return events.map { event -> event.toEvent() }
    }

    override suspend fun getTag(tagId: String): Tag? {
        return try {
            return supabase.from("tags").select() { filter { eq("tag_id", tagId) } }.decodeSingle()
        } catch (e: NoSuchElementException) {
            null
        }
    }

    override suspend fun getSubTags(tagId: String): List<Tag> {
        return supabase.from("tags").select() { filter { eq("parent_id", tagId) } }.decodeList()
    }

    override suspend fun getSubTagsNotIn(tagId: String, childTagIds: List<String>): List<Tag> {
        return supabase
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
    }

    override suspend fun getAllTags(): List<Tag> {
        return supabase.from("tags").select().decodeList<Tag>()
    }

    override suspend fun getAllTagsNotIn(tagIds: List<String>): List<Tag> {
        return supabase
            .from("tags")
            .select { filter { filterNot("tag_id", FilterOperator.IN, toFilterList(tagIds)) } }
            .decodeList()
    }

    override suspend fun getUserProfile(userId: String): UserProfile? {
        try {
            val userProfile =
                supabase
                    .from("user_profiles")
                    .select(
                        Columns.raw(
                            "user_id, name, semester, section, user_tags!user_tags_user_id_fkey(tags!user_tags_tag_id_fkey(tag_id, name, parent_id)), committee_members!public_associationMembers_user_id_fkey(associations!public_associationMembers_association_id_fkey(association_id, name, description)), association_subscriptions!association_subscription_user_id_fkey(associations!association_subscription_association_id_fkey(association_id, name, description))"
                        )
                    ) {
                        filter { eq("user_id", userId) }
                    }
                    .decodeSingle<UserProfileSupabase>()
            return userProfile.toUserProfile()
        } catch (e: NoSuchElementException) {
            return null
        }
    }

    override suspend fun setUserProfile(userProfile: UserProfile) {
        val userProfileSupabaseSetter = UserProfileSupabaseSetter(userProfile)
        supabase.from("user_profiles").upsert(userProfileSupabaseSetter, onConflict = "user_id")

        val userTagsSupabase =
            userProfile.tags.map { tag -> UserTagSupabase(userProfile.userId, tag.tagId) }
        supabase.from("user_tags").upsert(userTagsSupabase)

        val associationSubscriptionSupabase =
            userProfile.associationsSubscriptions.map { association ->
                AssociationSubscriptionSupabase(userProfile.userId, association.associationId)
            }
        supabase.from("association_subscriptions").upsert(associationSubscriptionSupabase)
    }
}
