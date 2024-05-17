package com.github.swent.echo.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.swent.echo.data.room.entity.EventRoom
import com.github.swent.echo.data.room.entity.EventTagCrossRef
import com.github.swent.echo.data.room.entity.EventWithOrganizerAndTags
import com.github.swent.echo.data.room.entity.JoinedEventRoom

@Dao
interface EventRoomDao {
    @Upsert suspend fun insert(event: EventRoom)

    @Upsert suspend fun insertEventTagCrossRefs(eventTagCrossRef: List<EventTagCrossRef>)

    @Query("DELETE FROM EventTagCrossRef WHERE eventId = :eventId")
    suspend fun deleteAllEventTagCrossRefsForEvent(eventId: String)

    @Query("DELETE FROM EventTagCrossRef WHERE eventId IN (:eventIds)")
    suspend fun deleteAllEventTagCrossRefsForEvents(eventIds: List<String>)

    @Upsert suspend fun insertAll(events: List<EventRoom>)

    @Transaction
    @Query("SELECT * FROM EventRoom WHERE eventId = :eventId AND timestamp >= :after")
    suspend fun get(eventId: String, after: Long): EventWithOrganizerAndTags?

    @Query("DELETE FROM EventRoom WHERE eventId = :eventId") suspend fun delete(eventId: String)

    @Transaction
    @Query("SELECT * FROM EventRoom WHERE timestamp >= :after")
    suspend fun getAll(after: Long): List<EventWithOrganizerAndTags>

    @Query("SELECT eventId FROM EventRoom WHERE timestamp >= :after")
    suspend fun getAllIds(after: Long): List<String>

    @Query("DELETE FROM EventRoom WHERE eventId NOT IN (:ids)")
    suspend fun deleteNotIn(ids: List<String>)

    @Upsert suspend fun insertJoinedEvent(joinedEvent: JoinedEventRoom)

    @Upsert suspend fun insertJoinedEvents(joinedEvents: List<JoinedEventRoom>)

    @Delete suspend fun deleteJoinedEvent(joinedEvent: JoinedEventRoom)

    @Delete suspend fun deleteJoinedEvents(joinedEvents: List<JoinedEventRoom>)

    @Query("SELECT eventId FROM JoinedEventRoom WHERE userId = :userId AND timestamp >= :after")
    suspend fun getJoinedEvents(userId: String, after: Long): List<String>

    @Query("DELETE FROM JoinedEventRoom WHERE userId = :userId AND eventId NOT IN (:eventIds)")
    suspend fun deleteJoinedEventsNotIn(userId: String, eventIds: List<String>)

    @Transaction
    @Query("SELECT * FROM EventRoom WHERE eventId IN (:eventIds)")
    suspend fun getEventsByIds(eventIds: List<String>): List<EventWithOrganizerAndTags>
}
