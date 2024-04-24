package com.github.swent.echo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Association(
    @SerialName("association_id") val associationId: String,
    val name: String,
    val description: String
) {
    companion object {
        val EMPTY = Association("", "", "")
    }
}
