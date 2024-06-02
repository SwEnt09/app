package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Tag
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Annotated helper class for the Supabase double join queries to work.
 *
 * @property tag Tag instance.
 */
@Serializable data class TagHelper(@SerialName("tags") val tag: Tag)
