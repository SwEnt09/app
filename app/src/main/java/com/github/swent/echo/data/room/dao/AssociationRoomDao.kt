package com.github.swent.echo.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.swent.echo.data.room.entity.AssociationRoom
import com.github.swent.echo.data.room.entity.AssociationTagCrossRef
import com.github.swent.echo.data.room.entity.AssociationWithTags

@Dao
interface AssociationRoomDao {
    @Upsert suspend fun insert(association: AssociationRoom)

    @Upsert
    suspend fun insertAssociationTagCrossRefs(associationTagCrossRefs: List<AssociationTagCrossRef>)

    @Query("DELETE FROM AssociationTagCrossRef WHERE associationId = :associationId")
    suspend fun deleteAllAssociationTagCrossRefsForAssociation(associationId: String)

    @Query("DELETE FROM AssociationTagCrossRef WHERE associationId IN (:associationIds)")
    suspend fun deleteAllAssociationTagCrossRefsForAssociations(associationIds: List<String>)

    @Upsert suspend fun insertAll(associations: List<AssociationRoom>)

    @Transaction
    @Query(
        "SELECT * FROM AssociationRoom WHERE associationId = :associationId AND timestamp >= :after"
    )
    suspend fun get(associationId: String, after: Long): AssociationWithTags?

    @Transaction
    @Query(
        "SELECT * FROM AssociationRoom WHERE associationId IN (:associationIds) AND timestamp >= :after"
    )
    suspend fun get(associationIds: List<String>, after: Long): List<AssociationWithTags>

    @Transaction
    @Query("SELECT * FROM AssociationRoom WHERE timestamp >= :after")
    suspend fun getAll(after: Long): List<AssociationWithTags>

    @Query("SELECT associationId FROM AssociationRoom WHERE timestamp >= :after")
    suspend fun getAllIds(after: Long): List<String>

    @Query(
        "SELECT associationId FROM AssociationRoom WHERE associationId IN (:associationIds) AND timestamp >= :after"
    )
    suspend fun getIdsFrom(associationIds: List<String>, after: Long): List<String>

    @Query("DELETE FROM AssociationRoom WHERE associationId NOT IN (:ids)")
    suspend fun deleteNotIn(ids: List<String>)
}
