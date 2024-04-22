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

    @Query("SELECT * FROM AssociationRoom WHERE associationId = :associationId")
    suspend fun get(associationId: String): AssociationRoom?

    @Query("SELECT * FROM AssociationRoom") suspend fun getAll(): List<AssociationRoom>
}
