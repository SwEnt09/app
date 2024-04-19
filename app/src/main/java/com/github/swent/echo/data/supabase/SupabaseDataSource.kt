package com.github.swent.echo.data.supabase

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import com.github.swent.echo.data.supabase.entities.EventSupabase
import com.github.swent.echo.data.supabase.entities.EventSupabaseSetter
import com.github.swent.echo.data.supabase.entities.EventTagSupabase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

class SupabaseDataSource(supabaseClient: SupabaseClient) : RemoteDataSource {

    var supabase = supabaseClient

    override suspend fun getAssociation(associationId: String): Association {
        return supabase
            .from("associations")
            .select() { filter { eq("association_id", associationId) } }
            .decodeSingle()
    }

    override suspend fun getAllAssociations(): List<Association> {
        return supabase.from("associations").select().decodeList<Association>()
    }

    override suspend fun getEvent(eventId: String): Event {
        var event =
            supabase
                .from("events")
                .select(
                    Columns.raw(
                        "event_id, user_profiles!public_events_creator_id_fkey(user_id, name), associations(association_id, name, description), title, description, event_tags(tags(tag_id, name)), location_name, location_lat, location_long, start_date, end_date, participant_count, max_participants, image_id"
                    )
                ) {
                    filter { eq("event_id", eventId) }
                }
                .decodeSingle<EventSupabase>()
        return event.toEvent()
    }

    override suspend fun setEvent(event: Event) {
        val eventSupabase = EventSupabaseSetter(event)
        supabase.from("events").upsert(eventSupabase, onConflict = "event_id")

        val eventTags = event.tags.map { tag -> EventTagSupabase(tag.tagId, event.eventId) }
        supabase.from("event_tags").upsert(eventTags)
    }

    override suspend fun getAllEvents(): List<Event> {
        var events =
            supabase
                .from("events")
                .select(
                    Columns.raw(
                        "event_id, user_profiles!public_events_creator_id_fkey(user_id, name), associations(association_id, name, description), title, description, event_tags(tags(tag_id, name)), location_name, location_lat, location_long, start_date, end_date, participant_count, max_participants, image_id"
                    )
                )
                .decodeList<EventSupabase>()
        return events.map { event -> event.toEvent() }
    }

    override suspend fun getTag(tagId: String): Tag {
        return supabase.from("tags").select() { filter { eq("tag_id", tagId) } }.decodeSingle()
    }

    override suspend fun getAllTags(): List<Tag> {
        return supabase.from("tags").select().decodeList<Tag>()
    }

    override suspend fun getUserProfile(userId: String): UserProfile {
        return supabase
            .from("user_profiles")
            .select() { filter { eq("user_id", userId) } }
            .decodeSingle()
    }

    override suspend fun setUserProfile(userProfile: UserProfile) {
        supabase.from("user_profiles").upsert(userProfile, onConflict = "user_id")
    }
}
