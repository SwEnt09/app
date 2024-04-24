package com.github.swent.echo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
