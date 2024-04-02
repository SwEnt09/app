package com.github.swent.echo.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Association(val associationId: String, val name: String, val description: String)
