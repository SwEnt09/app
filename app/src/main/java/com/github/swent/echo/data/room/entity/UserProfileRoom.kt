package com.github.swent.echo.data.room.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.model.toSectionEPFL
import com.github.swent.echo.data.model.toSemesterEPFL
import java.time.ZonedDateTime

@Entity
data class UserProfileRoom(
    @PrimaryKey val userId: String,
    val name: String,
    val semester: String?,
    val section: String?,
    /** The time of the last update in seconds */
    val timestamp: Long = ZonedDateTime.now().toEpochSecond(),
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

@Entity(
    primaryKeys = ["userId", "tagId"],
    indices = [Index("tagId")],
    foreignKeys =
        [
            ForeignKey(
                entity = UserProfileRoom::class,
                parentColumns = ["userId"],
                childColumns = ["userId"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = TagRoom::class,
                parentColumns = ["tagId"],
                childColumns = ["tagId"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
)
data class UserProfileTagCrossRef(
    val userId: String,
    val tagId: String,
)

@Entity(
    primaryKeys = ["userId", "associationId"],
    indices = [Index("associationId")],
    foreignKeys =
        [
            ForeignKey(
                entity = UserProfileRoom::class,
                parentColumns = ["userId"],
                childColumns = ["userId"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = AssociationRoom::class,
                parentColumns = ["associationId"],
                childColumns = ["associationId"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
)
data class UserProfileCommitteeMemberCrossRef(
    val userId: String,
    val associationId: String,
)

@Entity(
    primaryKeys = ["userId", "associationId"],
    indices = [Index("associationId")],
    foreignKeys =
        [
            ForeignKey(
                entity = UserProfileRoom::class,
                parentColumns = ["userId"],
                childColumns = ["userId"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = AssociationRoom::class,
                parentColumns = ["associationId"],
                childColumns = ["associationId"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
)
data class UserProfileAssociationSubscriptionCrossRef(
    val userId: String,
    val associationId: String,
)

data class UserProfileWithTagsCommitteeMemberAndAssociationSubscription(
    @Embedded val userProfile: UserProfileRoom,
    @Relation(
        parentColumn = "userId",
        entityColumn = "tagId",
        associateBy = Junction(UserProfileTagCrossRef::class),
    )
    val tags: List<TagRoom>,
    @Relation(
        parentColumn = "userId",
        entityColumn = "associationId",
        associateBy = Junction(UserProfileCommitteeMemberCrossRef::class),
    )
    val committeeMember: List<AssociationRoom>,
    @Relation(
        parentColumn = "userId",
        entityColumn = "associationId",
        associateBy = Junction(UserProfileAssociationSubscriptionCrossRef::class),
    )
    val associationsSubscriptions: List<AssociationRoom>,
) {
    fun toUserProfile(): UserProfile =
        UserProfile(
            userProfile.userId,
            userProfile.name,
            userProfile.semester?.toSemesterEPFL(),
            userProfile.section?.toSectionEPFL(),
            tags.toTagSet(),
            committeeMember.toAssociationSet(),
            associationsSubscriptions.toAssociationSet(),
        )
}
