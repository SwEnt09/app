package com.github.swent.echo.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.github.swent.echo.data.room.entity.UserProfileRoom
import com.github.swent.echo.data.room.entity.UserProfileTagCrossRef
import com.github.swent.echo.data.room.entity.UserProfileWithTags

@Dao
interface UserProfileRoomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfileRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfileTagCrossRefs(userProfileTagCrossRef: List<UserProfileTagCrossRef>)

    @Transaction
    @Query("SELECT * FROM UserProfileRoom WHERE userId = :userId")
    suspend fun get(userId: String): UserProfileWithTags?
}
