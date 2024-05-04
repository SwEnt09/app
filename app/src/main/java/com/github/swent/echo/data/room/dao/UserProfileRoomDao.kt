package com.github.swent.echo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.github.swent.echo.data.room.entity.UserProfileAssociationSubscriptionCrossRef
import com.github.swent.echo.data.room.entity.UserProfileCommitteeMemberCrossRef
import com.github.swent.echo.data.room.entity.UserProfileRoom
import com.github.swent.echo.data.room.entity.UserProfileTagCrossRef
import com.github.swent.echo.data.room.entity.UserProfileWithTagsCommitteeMemberAndAssociationSubscription

@Dao
interface UserProfileRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfileRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfileTagCrossRefs(userProfileTagCrossRef: List<UserProfileTagCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfileCommitteeMemberCrossRefs(
        userProfileCommitteeMemberCrossRef: List<UserProfileCommitteeMemberCrossRef>
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfileAssociationSubscriptionCrossRefs(
        userProfileAssociationSubscriptionCrossRef: List<UserProfileAssociationSubscriptionCrossRef>
    )

    @Transaction
    @Query("SELECT * FROM UserProfileRoom WHERE userId = :userId AND timestamp >= :after")
    suspend fun get(
        userId: String,
        after: Long
    ): UserProfileWithTagsCommitteeMemberAndAssociationSubscription?
}
