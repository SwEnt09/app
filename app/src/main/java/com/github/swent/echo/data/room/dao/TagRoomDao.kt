package com.github.swent.echo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.swent.echo.data.room.entity.TagRoom

@Dao
interface TagRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(tags: TagRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(tags: List<TagRoom>)

    @Query("SELECT * FROM TagRoom WHERE tagId = :tagId AND timestamp >= :after")
    suspend fun get(tagId: String, after: Long): TagRoom?

    @Query("SELECT * FROM TagRoom WHERE timestamp >= :after")
    suspend fun getAll(after: Long): List<TagRoom>

    @Query("SELECT tagId FROM TagRoom WHERE timestamp <= :before")
    suspend fun getAllBefore(before: Long): List<String>

    @Query("SELECT * FROM TagRoom WHERE parentId = :tagId AND timestamp >= :after")
    suspend fun getSubTags(tagId: String, after: Long): List<TagRoom>
}
