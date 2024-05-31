package com.github.swent.echo.data.supabase

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import com.github.swent.echo.data.repository.datasources.RemoteDataSourceRequestMaxRetryExceededException
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

    /**
     * Wrapper for Supabase requests that retries the request if it fails due to a
     * [HttpRequestException]. If the request fails after [maxRetriesCount] retries, it throws a
     * [RemoteDataSourceRequestMaxRetryExceededException] exception. In case of an
     * [NoSuchElementException], [BadRequestRestException] or [NotFoundRestException] it returns
     * null.
     *
     * @param maxRetriesCount: the number of times the request will be retried
     * @param delay: (optional) Delay between retries. Default = RETRY_DELAY_MILLI
     * @param request: the request to be executed
     */
    private suspend fun <T> supabaseRequestExceptionHandlerAndRetryWrapper(
        maxRetriesCount: UInt,
        delay: Long = RETRY_DELAY_MILLI,
        request: suspend () -> T,
    ): T? {
        return try {
            request()
        } catch (e: NoSuchElementException) {
            null
        } catch (e: BadRequestRestException) {
            null
        } catch (e: NotFoundRestException) {
            null
        } catch (e: HttpRequestException) {
            if (maxRetriesCount == 0u) throw RemoteDataSourceRequestMaxRetryExceededException()
            delay(delay)
            return supabaseRequestExceptionHandlerAndRetryWrapper(
                maxRetriesCount - 1u,
                delay,
                request
            )
        }
    }

    /**
     * List variant of the same wrapper as above. It returns emptyLists instead of null.
     *
     * @param maxRetriesCount: the number of times the request will be retried
     * @param delay: (optional) Delay between retries. Default = RETRY_DELAY_MILLI
     * @param request: the request to be executed
     */
    private suspend fun <T> supabaseRequestExceptionHandlerAndRetryWrapperS(
        maxRetriesCount: UInt,
        delay: Long = RETRY_DELAY_MILLI,
        request: suspend () -> List<T>,
    ): List<T> {
        return try {
            request()
        } catch (e: NoSuchElementException) {
            emptyList<T>()
        } catch (e: BadRequestRestException) {
            emptyList<T>()
        } catch (e: NotFoundRestException) {
            emptyList<T>()
        } catch (e: HttpRequestException) {
            if (maxRetriesCount == 0u) throw RemoteDataSourceRequestMaxRetryExceededException()
            delay(delay)
            return supabaseRequestExceptionHandlerAndRetryWrapperS(
                maxRetriesCount - 1u,
                delay,
                request
            )
        }
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
    ): Association? =
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
            supabase
                .from("associations")
                .select(Columns.raw(QUERY_ASSOCIATION)) {
                    filter { eq("association_id", associationId) }
                }
                .decodeSingle<AssociationSupabase>()
                .toAssociation()
        }

    override suspend fun getAssociations(
        associations: List<String>,
        maxRetriesCount: UInt
    ): List<Association> =
        supabaseRequestExceptionHandlerAndRetryWrapperS(maxRetriesCount) {
            supabase
                .from("associations")
                .select(Columns.raw(QUERY_ASSOCIATION)) {
                    filter { isIn("association_id", associations) }
                }
                .decodeList<AssociationSupabase>()
                .toAssociations()
        }

    override suspend fun getAssociationsNotIn(
        associationIds: List<String>,
        maxRetriesCount: UInt
    ): List<Association> =
        supabaseRequestExceptionHandlerAndRetryWrapperS(maxRetriesCount) {
            supabase
                .from("associations")
                .select(Columns.raw(QUERY_ASSOCIATION)) {
                    filter {
                        filterNot("association_id", FilterOperator.IN, toFilterList(associationIds))
                    }
                }
                .decodeList<AssociationSupabase>()
                .toAssociations()
        }

    override suspend fun getAllAssociations(maxRetriesCount: UInt): List<Association> =
        supabaseRequestExceptionHandlerAndRetryWrapperS(maxRetriesCount) {
            supabase
                .from("associations")
                .select(Columns.raw(QUERY_ASSOCIATION))
                .decodeList<AssociationSupabase>()
                .toAssociations()
        }

    override suspend fun getEvent(eventId: String, maxRetriesCount: UInt): Event? =
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
            supabase
                .from("events")
                .select(Columns.raw(QUERY_EVENT)) { filter { eq("event_id", eventId) } }
                .decodeSingle<EventSupabase>()
                .toEvent()
        }

    override suspend fun createEvent(event: Event, maxRetriesCount: UInt): String? {
        val eventSupabase = EventSupabaseSetter(event).copy(eventId = null)
        val eventId =
            supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
                supabase
                    .from("events")
                    .insert(eventSupabase) { select() }
                    .decodeSingle<EventSupabaseSetter>()
                    .eventId
            }
        eventId ?: return null
        setEventTagRelations(eventId, event.tags)
        return eventId
    }

    override suspend fun setEvent(event: Event, maxRetriesCount: UInt) {
        val eventSupabase = EventSupabaseSetter(event)
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
            supabase.from("events").upsert(eventSupabase, onConflict = "event_id")
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
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount, 100L) {
            supabase.from("event_tags").upsert(eventTags, onConflict = "tag_id,event_id")
        }
    }

    private suspend fun deleteAllEventTagRelationsForEvent(
        eventId: String,
        maxRetriesCount: UInt = 10u
    ) {
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount, 100L) {
            supabase.from("event_tags").delete { filter { eq("event_id", eventId) } }
        }
    }

    override suspend fun deleteEvent(event: Event, maxRetriesCount: UInt) {
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
            supabase.from("events").delete { filter { eq("event_id", event.eventId) } }
        }
    }

    override suspend fun getEventsNotIn(
        eventIds: List<String>,
        maxRetriesCount: UInt
    ): List<Event> =
        supabaseRequestExceptionHandlerAndRetryWrapperS(maxRetriesCount) {
            supabase
                .from("events")
                .select(Columns.raw(QUERY_EVENT)) {
                    filter { filterNot("event_id", FilterOperator.IN, toFilterList(eventIds)) }
                }
                .decodeList<EventSupabase>()
                .map { event -> event.toEvent() }
        }

    override suspend fun getAllEvents(maxRetriesCount: UInt): List<Event> =
        supabaseRequestExceptionHandlerAndRetryWrapperS(maxRetriesCount) {
            supabase
                .from("events")
                .select(Columns.raw(QUERY_EVENT))
                .decodeList<EventSupabase>()
                .map { event -> event.toEvent() }
        }

    override suspend fun joinEvent(
        userId: String,
        eventId: String,
        maxRetriesCount: UInt
    ): Boolean {
        val res =
            supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
                supabase.from("event_joins").upsert(EventJoinSupabase(userId, eventId))
            }
        res ?: return false
        return true
    }

    override suspend fun leaveEvent(
        userId: String,
        eventId: String,
        maxRetriesCount: UInt
    ): Boolean {
        val res =
            supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
                supabase.from("event_joins").delete {
                    filter {
                        and {
                            eq("user_id", userId)
                            eq("event_id", eventId)
                        }
                    }
                }
            }
        res ?: return false
        return true
    }

    override suspend fun getJoinedEvents(userId: String, maxRetriesCount: UInt): List<Event> =
        supabaseRequestExceptionHandlerAndRetryWrapperS(maxRetriesCount) {
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
        }

    override suspend fun getJoinedEventsNotIn(
        userId: String,
        eventIds: List<String>,
        maxRetriesCount: UInt
    ): List<Event> =
        supabaseRequestExceptionHandlerAndRetryWrapperS(maxRetriesCount) {
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
        }

    override suspend fun getTag(tagId: String, maxRetriesCount: UInt): Tag? =
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
            supabase.from("tags").select() { filter { eq("tag_id", tagId) } }.decodeSingle()
        }

    override suspend fun getSubTags(tagId: String, maxRetriesCount: UInt): List<Tag> =
        supabaseRequestExceptionHandlerAndRetryWrapperS(maxRetriesCount) {
            supabase.from("tags").select() { filter { eq("parent_id", tagId) } }.decodeList()
        }

    override suspend fun getSubTagsNotIn(
        tagId: String,
        childTagIds: List<String>,
        maxRetriesCount: UInt
    ): List<Tag> =
        supabaseRequestExceptionHandlerAndRetryWrapperS(maxRetriesCount) {
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
        }

    override suspend fun getAllTags(maxRetriesCount: UInt): List<Tag> =
        supabaseRequestExceptionHandlerAndRetryWrapperS(maxRetriesCount) {
            supabase.from("tags").select().decodeList<Tag>()
        }

    override suspend fun getAllTagsNotIn(tagIds: List<String>, maxRetriesCount: UInt): List<Tag> =
        supabaseRequestExceptionHandlerAndRetryWrapperS(maxRetriesCount) {
            supabase
                .from("tags")
                .select { filter { filterNot("tag_id", FilterOperator.IN, toFilterList(tagIds)) } }
                .decodeList()
        }

    override suspend fun getUserProfile(userId: String, maxRetriesCount: UInt): UserProfile? =
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
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
        }

    override suspend fun setUserProfile(userProfile: UserProfile, maxRetriesCount: UInt) {
        val userProfileSupabaseSetter = UserProfileSupabaseSetter(userProfile)
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
            supabase.from("user_profiles").upsert(userProfileSupabaseSetter, onConflict = "user_id")
        }

        setUserTags(userProfile)

        setUserAssociationSubscriptions(userProfile)
    }

    private suspend fun setUserTags(userProfile: UserProfile, maxRetriesCount: UInt = 10u) {
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount, 100L) {
            supabase.from("user_tags").delete { filter { eq("user_id", userProfile.userId) } }
            val userTagsSupabase =
                userProfile.tags.map { tag -> UserTagSupabase(userProfile.userId, tag.tagId) }
            supabase.from("user_tags").upsert(userTagsSupabase)
        }
    }

    private suspend fun setUserAssociationSubscriptions(
        userProfile: UserProfile,
        maxRetriesCount: UInt = 10u
    ) {
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
            supabase.from("association_subscriptions").delete {
                filter { eq("user_id", userProfile.userId) }
            }
            val associationSubscriptionSupabase =
                userProfile.associationsSubscriptions.map { association ->
                    AssociationSubscriptionSupabase(userProfile.userId, association.associationId)
                }
            supabase.from("association_subscriptions").upsert(associationSubscriptionSupabase)
        }
    }

    override suspend fun deleteUserProfile(userProfile: UserProfile, maxRetriesCount: UInt) {
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
            supabase.from("user_profiles").delete { filter { eq("user_id", userProfile.userId) } }
        }
    }

    override suspend fun getUserProfilePicture(userId: String, maxRetriesCount: UInt): ByteArray? =
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
            supabase.storage.from("user-profile-picture").downloadAuthenticated("$userId.jpeg")
        }

    override suspend fun setUserProfilePicture(
        userId: String,
        picture: ByteArray,
        maxRetriesCount: UInt
    ) {
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
            supabase.storage
                .from("user-profile-picture")
                .upload("$userId.jpeg", picture, upsert = true)
        }
    }

    override suspend fun deleteUserProfilePicture(userId: String, maxRetriesCount: UInt) {
        supabaseRequestExceptionHandlerAndRetryWrapper(maxRetriesCount) {
            supabase.storage.from("user-profile-picture").delete("$userId.jpeg")
        }
    }
}
