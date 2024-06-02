package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.AssociationHeader
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Annotated helper class for the Supabase double join queries to work.
 *
 * @property association AssociationHeader instance.
 */
@Serializable
data class AssociationHelper(@SerialName("associations") val association: AssociationHeader)
