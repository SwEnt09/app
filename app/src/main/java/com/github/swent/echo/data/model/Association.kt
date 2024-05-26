package com.github.swent.echo.data.model

data class Association(
    val associationId: String,
    val name: String,
    val description: String,
    val url: String?,
    val relatedTags: Set<Tag>
) {
    companion object {
        val EMPTY = Association("", "", "", "", setOf())
    }
}
