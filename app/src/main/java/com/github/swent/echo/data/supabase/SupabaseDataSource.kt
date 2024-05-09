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

class SupabaseDataSource(private val supabase: SupabaseClient) : RemoteDataSource {

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
        var events =
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

    override suspend fun getTag(tagId: String): Tag? {
        return try {
            supabase.from("tags").select() { filter { eq("tag_id", tagId) } }.decodeSingle()
        } catch (e: NoSuchElementException) {
            null
        }
    }

    override suspend fun getSubTags(tagId: String): List<Tag> {
        return supabase.from("tags").select() { filter { eq("parent_id", tagId) } }.decodeList()
    }

    override suspend fun getAllTags(): List<Tag> {
        return supabase.from("tags").select().decodeList<Tag>()
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
