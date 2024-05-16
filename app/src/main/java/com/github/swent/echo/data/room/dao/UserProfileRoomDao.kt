package com.github.swent.echo.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.swent.echo.data.room.entity.UserProfileAssociationSubscriptionCrossRef
import com.github.swent.echo.data.room.entity.UserProfileCommitteeMemberCrossRef
import com.github.swent.echo.data.room.entity.UserProfileRoom
import com.github.swent.echo.data.room.entity.UserProfileTagCrossRef
import com.github.swent.echo.data.room.entity.UserProfileWithTagsCommitteeMemberAndAssociationSubscription

@Dao
interface UserProfileRoomDao {
    @Upsert suspend fun insert(userProfile: UserProfileRoom)

    @Upsert
    suspend fun insertUserProfileTagCrossRefs(userProfileTagCrossRef: List<UserProfileTagCrossRef>)

    @Upsert
    suspend fun insertUserProfileCommitteeMemberCrossRefs(
        userProfileCommitteeMemberCrossRef: List<UserProfileCommitteeMemberCrossRef>
    )

    @Upsert
    suspend fun insertUserProfileAssociationSubscriptionCrossRefs(
        userProfileAssociationSubscriptionCrossRef: List<UserProfileAssociationSubscriptionCrossRef>
    )

    @Upsert
    suspend fun joinAssociation(
        userProfileAssociationSubscriptionCrossRef: UserProfileAssociationSubscriptionCrossRef
    )

    @Delete
    suspend fun leaveAssociation(
        userProfileAssociationSubscriptionCrossRef: UserProfileAssociationSubscriptionCrossRef
    )

    @Transaction
    @Query("SELECT * FROM UserProfileRoom WHERE userId = :userId AND timestamp >= :after")
    suspend fun get(
        userId: String,
        after: Long
    ): UserProfileWithTagsCommitteeMemberAndAssociationSubscription?

    @Query("SELECT userId FROM UserProfileRoom WHERE timestamp >= :after")
    suspend fun getAllIds(after: Long): List<String>
}
