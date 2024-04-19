package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Tag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// This annotated helper class is needed for the serialization of the double join queries to work
@Serializable data class TagHelper(@SerialName("tags") val tag: Tag)
