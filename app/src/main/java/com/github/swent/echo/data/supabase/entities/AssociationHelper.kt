package com.github.swent.echo.data.supabase.entities

import com.github.swent.echo.data.model.Association
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// This annotated helper class is needed for the serialization of the double join queries to work
@Serializable
data class AssociationHelper(@SerialName("associations") val association: Association)
