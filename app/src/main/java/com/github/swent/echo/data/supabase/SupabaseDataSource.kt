package com.github.swent.echo.data.supabase

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import com.github.swent.echo.data.supabase.entities.AssociationSubscriptionSupabase
import com.github.swent.echo.data.supabase.entities.EventSupabase
import com.github.swent.echo.data.supabase.entities.EventSupabaseSetter
import com.github.swent.echo.data.supabase.entities.EventTagSupabase
import com.github.swent.echo.data.supabase.entities.UserProfileSupabase
import com.github.swent.echo.data.supabase.entities.UserProfileSupabaseSetter
import com.github.swent.echo.data.supabase.entities.UserTagSupabase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class SupabaseDataSource(private val supabase: SupabaseClient) : RemoteDataSource {

    override suspend fun getAssociation(associationId: String): Association {
        return supabase
            .from("associations")
            .select() { filter { eq("association_id", associationId) } }
            .decodeSingle()
    }

    override suspend fun getAllAssociations(): List<Association> {
        return supabase.from("associations").select().decodeList<Association>()
    }

    companion object {
        const val QUERY_EVENT =
            "event_id, user_profiles!public_events_creator_id_fkey(user_id, name), associations(association_id, name, description), title, description, event_tags(tags(tag_id, name, parent_id)), location_name, location_lat, location_long, start_date, end_date, participant_count, max_participants, image_id"
    }

    override suspend fun getEvent(eventId: String): Event {
        var event =
            supabase
                .from("events")
                .select(Columns.raw(QUERY_EVENT)) { filter { eq("event_id", eventId) } }
                .decodeSingle<EventSupabase>()
        return event.toEvent()
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
        var events =
            supabase.from("events").select(Columns.raw(QUERY_EVENT)).decodeList<EventSupabase>()
        return events.map { event -> event.toEvent() }
    }

    override suspend fun getTag(tagId: String): Tag {
        return supabase.from("tags").select() { filter { eq("tag_id", tagId) } }.decodeSingle()
    }

    override suspend fun getAllTags(): List<Tag> {
        return supabase.from("tags").select().decodeList<Tag>()
    }

    override suspend fun getUserProfile(userId: String): UserProfile {
        var userProfile =
            supabase
                .from("user_profiles")
                .select(
                    Columns.raw(
                        "user_id, name, semester, section, user_tags(tags(tag_id, name, parent_id)), committee_members(associations(association_id, name, description)), association_subscriptions(associations(association_id, name, description))"
                    )
                ) {
                    filter { eq("user_id", userId) }
                }
                .decodeSingle<UserProfileSupabase>()
        return userProfile.toUserProfile()
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
