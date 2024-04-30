package com.github.swent.echo.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.swent.echo.data.room.dao.AssociationRoomDao
import com.github.swent.echo.data.room.dao.EventRoomDao
import com.github.swent.echo.data.room.dao.TagRoomDao
import com.github.swent.echo.data.room.dao.UserProfileRoomDao
import com.github.swent.echo.data.room.entity.AssociationRoom
import com.github.swent.echo.data.room.entity.EventRoom
import com.github.swent.echo.data.room.entity.EventTagCrossRef
import com.github.swent.echo.data.room.entity.TagRoom
import com.github.swent.echo.data.room.entity.UserProfileAssociationSubscriptionCrossRef
import com.github.swent.echo.data.room.entity.UserProfileCommitteeMemberCrossRef
import com.github.swent.echo.data.room.entity.UserProfileRoom
import com.github.swent.echo.data.room.entity.UserProfileTagCrossRef

@Database(
    entities =
        [
            AssociationRoom::class,
            EventRoom::class,
            EventTagCrossRef::class,
            TagRoom::class,
            UserProfileRoom::class,
            UserProfileTagCrossRef::class,
            UserProfileCommitteeMemberCrossRef::class,
            UserProfileAssociationSubscriptionCrossRef::class,
        ],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val APP_DATABASE_NAME = "echo-db"
    }

    abstract fun associationDao(): AssociationRoomDao

    abstract fun eventDao(): EventRoomDao

    abstract fun tagDao(): TagRoomDao

    abstract fun userProfileDao(): UserProfileRoomDao
}
