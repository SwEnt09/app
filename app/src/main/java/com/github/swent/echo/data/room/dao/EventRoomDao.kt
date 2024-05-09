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

    @Upsert suspend fun insertEventTagCrossRegs(eventTagCrossRef: List<EventTagCrossRef>)

    @Upsert suspend fun insertAll(events: List<EventRoom>)

    @Transaction
    @Query("SELECT * FROM EventRoom WHERE eventId = :eventId AND timestamp >= :after")
    suspend fun get(eventId: String, after: Long): EventWithOrganizerAndTags?

    @Transaction
    @Query("SELECT * FROM EventRoom WHERE timestamp >= :after")
    suspend fun getAll(after: Long): List<EventWithOrganizerAndTags>

    @Query("SELECT eventId FROM EventRoom WHERE timestamp <= :before")
    suspend fun getAllBefore(before: Long): List<String>

    @Upsert suspend fun insertJoinedEvent(joinedEvent: JoinedEventRoom)

    @Delete suspend fun deleteJoinedEvent(joinedEvent: JoinedEventRoom)

    @Query("SELECT eventId FROM JoinedEventRoom WHERE userId = :userId")
    suspend fun getJoinedEvents(userId: String): List<String>

    @Transaction
    @Query("SELECT * FROM EventRoom WHERE eventId IN (:eventIds)")
    suspend fun getEventsByIds(eventIds: List<String>): List<EventWithOrganizerAndTags>
}
