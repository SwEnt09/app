package com.github.swent.echo.data.model

import kotlinx.serialization.Serializable

@Serializable data class UserProfile(val userId: String, val name: String)
