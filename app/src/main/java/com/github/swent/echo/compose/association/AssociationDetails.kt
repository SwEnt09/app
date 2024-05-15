package com.github.swent.echo.compose.association

import androidx.compose.runtime.Composable
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event

@Composable
fun AssociationDetails(
    follow: (Association) -> Unit,
    association: Association,
    isFollowed: Boolean,
    events: List<Event>
) {}
