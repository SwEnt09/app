package com.github.swent.echo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The tag data class.
 *
 * @property tagId the unique id of the tag
 * @property name the name of the tag
 * @property parentId the id of the parent of the tag
 */
@Serializable
data class Tag(
    @SerialName("tag_id") val tagId: String,
    val name: String,
    @SerialName("parent_id")
    val parentId: String? = null, // TODO: We might want to remove the default value here...
) {
    companion object {
        val EMPTY = Tag(tagId = "", name = "")
    }
}
