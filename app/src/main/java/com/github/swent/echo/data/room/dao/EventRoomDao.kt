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
    @Query("SELECT * FROM EventRoom WHERE eventId = :eventId")
    suspend fun get(eventId: String): EventWithOrganizerAndTags?

    @Transaction
    @Query("SELECT * FROM EventRoom")
    suspend fun getAll(): List<EventWithOrganizerAndTags>
}
