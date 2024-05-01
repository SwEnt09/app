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

    @Query("SELECT * FROM TagRoom WHERE tagId = :tagId") suspend fun get(tagId: String): TagRoom?

    @Query("SELECT * FROM TagRoom") suspend fun getAll(): List<TagRoom>

    @Query("SELECT * FROM TagRoom WHERE parentId = :tagId")
    suspend fun getSubTags(tagId: String): List<TagRoom>
}
