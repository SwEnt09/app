package com.github.swent.echo.compose.association

import androidx.compose.runtime.Composable
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event

@Composable
fun AssociationMainScreen(
    events: List<Event>,
    goTo: (AssociationPage) -> Unit,
    addAssociationToFilter: (Association) -> Unit,
    followedAssociations: List<Association>,
    committeeAssociations: List<Association>,
    eventsFilter: List<Association>
) {}
