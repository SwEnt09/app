package com.github.swent.echo.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.swent.echo.data.room.entity.TagRoom

@Dao
interface TagRoomDao {
    @Upsert suspend fun insert(tags: TagRoom)

    @Upsert suspend fun insertAll(tags: List<TagRoom>)

    @Query("SELECT * FROM TagRoom WHERE tagId = :tagId AND timestamp >= :after")
    suspend fun get(tagId: String, after: Long): TagRoom?

    @Query("SELECT * FROM TagRoom WHERE timestamp >= :after")
    suspend fun getAll(after: Long): List<TagRoom>

    @Query("SELECT tagId FROM TagRoom WHERE timestamp >= :after")
    suspend fun getAllIds(after: Long): List<String>

    @Query("DELETE FROM TagRoom WHERE tagId NOT IN (:tagIds)")
    suspend fun deleteNotIn(tagIds: List<String>)

    @Query("SELECT * FROM TagRoom WHERE parentId = :tagId AND timestamp >= :after")
    suspend fun getSubTags(tagId: String, after: Long): List<TagRoom>

    @Query("DELETE FROM TagRoom WHERE parentId = :tagId AND tagId NOT IN (:childTagIds)")
    suspend fun deleteSubTagsNotIn(tagId: String, childTagIds: List<String>)
}
