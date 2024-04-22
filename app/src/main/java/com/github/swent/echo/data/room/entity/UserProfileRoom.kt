package com.github.swent.echo.data.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.model.toSectionEPFL
import com.github.swent.echo.data.model.toSemesterEPFL

@Entity
data class UserProfileRoom(
    @PrimaryKey val userId: String,
    val name: String,
    val semester: String?,
    val section: String?,
) {
    constructor(
        userProfile: UserProfile
    ) : this(
        userProfile.userId,
        userProfile.name,
        userProfile.semester?.name,
        userProfile.section?.name,
    )
}

@Entity(primaryKeys = ["userId", "tagId"], indices = [Index("tagId")])
data class UserProfileTagCrossRef(
    val userId: String,
    val tagId: String,
)

data class UserProfileWithTags(
    @Embedded val userProfile: UserProfileRoom,
    @Relation(
        parentColumn = "userId",
        entityColumn = "tagId",
        associateBy = Junction(UserProfileTagCrossRef::class),
    )
    val tags: List<TagRoom>,
) {
    fun toUserProfile(): UserProfile =
        UserProfile(
            userProfile.userId,
            userProfile.name,
            userProfile.semester?.toSemesterEPFL(),
            userProfile.section?.toSectionEPFL(),
            tags.toTagSet(),
        )
}
