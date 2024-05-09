package com.github.swent.echo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.github.swent.echo.data.room.entity.EventRoom
import com.github.swent.echo.data.room.entity.EventTagCrossRef
import com.github.swent.echo.data.room.entity.EventWithOrganizerAndTags

@Dao
interface EventRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(event: EventRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventTagCrossRegs(eventTagCrossRef: List<EventTagCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(events: List<EventRoom>)

    @Transaction
    @Query("SELECT * FROM EventRoom WHERE eventId = :eventId AND timestamp >= :after")
    suspend fun get(eventId: String, after: Long): EventWithOrganizerAndTags?

    @Transaction
    @Query("SELECT * FROM EventRoom WHERE timestamp >= :after")
    suspend fun getAll(after: Long): List<EventWithOrganizerAndTags>

    @Query("SELECT eventId FROM EventRoom WHERE timestamp <= :before")
    suspend fun getAllBefore(before: Long): List<String>
}
