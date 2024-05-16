package com.github.swent.echo.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.github.swent.echo.data.room.entity.AssociationRoom

@Dao
interface AssociationRoomDao {
    @Upsert suspend fun insert(association: AssociationRoom)

    @Upsert suspend fun insertAll(associations: List<AssociationRoom>)

    @Query(
        "SELECT * FROM AssociationRoom WHERE associationId = :associationId AND timestamp >= :after"
    )
    suspend fun get(associationId: String, after: Long): AssociationRoom?

    @Query("SELECT * FROM AssociationRoom WHERE timestamp >= :after")
    suspend fun getAll(after: Long): List<AssociationRoom>

    @Query("SELECT associationId FROM AssociationRoom WHERE timestamp >= :after")
    suspend fun getAllIds(after: Long): List<String>

    @Query("DELETE FROM AssociationRoom WHERE associationId NOT IN (:ids)")
    suspend fun deleteNotIn(ids: List<String>)
}
