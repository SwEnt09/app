package com.github.swent.echo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.swent.echo.data.room.entity.AssociationRoom

@Dao
interface AssociationRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(association: AssociationRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(associations: List<AssociationRoom>)

    @Query(
        "SELECT * FROM AssociationRoom WHERE associationId = :associationId AND timestamp >= :after"
    )
    suspend fun get(associationId: String, after: Long): AssociationRoom?

    @Query("SELECT * FROM AssociationRoom WHERE timestamp >= :after")
    suspend fun getAll(after: Long): List<AssociationRoom>

    @Query("SELECT associationId FROM AssociationRoom WHERE timestamp <= :before")
    suspend fun getAllBefore(before: Long): List<String>
}
