package com.github.swent.echo.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.swent.echo.data.model.Tag
import java.time.ZonedDateTime

@Entity
data class TagRoom(
    @PrimaryKey val tagId: String,
    val parentId: String?,
    val name: String,
    /** The time of the last update in seconds */
    val timestamp: Long = ZonedDateTime.now().toEpochSecond(),
) {
    constructor(tag: Tag) : this(tag.tagId, tag.parentId, tag.name)

    fun toTag(): Tag = Tag(tagId, name, parentId)
}

fun Set<Tag>.toTagRoomList(): List<TagRoom> = this.map { TagRoom(it) }

fun List<TagRoom>.toTagList(): List<Tag> = this.map { it.toTag() }

fun List<TagRoom>.toTagSet(): Set<Tag> = this.toTagList().toSet()
