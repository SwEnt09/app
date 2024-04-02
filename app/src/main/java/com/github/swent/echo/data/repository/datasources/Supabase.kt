package com.github.swent.echo.data.repository.datasources

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.User
import com.github.swent.echo.data.model.UserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class Supabase(supabaseClient: SupabaseClient) : RemoteDataSource {

    var supabase = supabaseClient

    override suspend fun getAssociation(associationId: String): Association {
        return supabase
            .from("associations")
            .select() { filter { eq("associationId", associationId) } }
            .decodeSingle()
    }

    override suspend fun setAssociation(association: Association) {
        supabase.from("associations").upsert(association, onConflict = "associationId")
    }

    override suspend fun getAllAssociations(): List<Association> {
        return supabase.from("associations").select().decodeList<Association>()
    }

    override suspend fun getEvent(eventId: String): Event {
        return supabase.from("events").select() { filter { eq("eventId", eventId) } }.decodeSingle()
    }

    override suspend fun setEvent(event: Event) {
        supabase.from("events").upsert(event, onConflict = "eventId")
    }

    override suspend fun getAllEvents(): List<Event> {
        return supabase.from("events").select().decodeList<Event>()
    }

    override suspend fun getTag(tagId: String): Tag {
        return supabase.from("tags").select() { filter { eq("tagId", tagId) } }.decodeSingle()
    }

    override suspend fun setTag(tag: Tag) {
        supabase.from("tags").upsert(tag, onConflict = "tagId")
    }

    override suspend fun getAllTags(): List<Tag> {
        return supabase.from("tags").select().decodeList<Tag>()
    }

    override suspend fun getUser(userId: String): User {
        return supabase.from("users").select() { filter { eq("userId", userId) } }.decodeSingle()
    }

    override suspend fun setUser(user: User) {
        supabase.from("users").upsert(user, onConflict = "userId")
    }

    override suspend fun getUserProfile(userId: String): UserProfile {
        return supabase
            .from("userProfiles")
            .select() { filter { eq("userId", userId) } }
            .decodeSingle()
    }

    override suspend fun setUserProfile(userProfile: UserProfile) {
        supabase.from("userProfiles").upsert(userProfile, onConflict = "userId")
    }
}
