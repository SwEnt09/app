package com.github.swent.echo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class Tag(@SerialName("tag_id") val tagId: String, val name: String)
