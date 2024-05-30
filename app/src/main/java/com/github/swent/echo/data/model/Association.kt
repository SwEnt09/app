package com.github.swent.echo.data.model

/**
 * The association data class.
 *
 * @property associationId the unique id of the association
 * @property name the name of the association
 * @property description the description of the association
 * @property url the link to the association web page
 * @property relatedTags the tags related to the association
 */
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
