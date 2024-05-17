package com.github.swent.echo.compose.association

import androidx.compose.runtime.Composable
import com.github.swent.echo.data.model.Association

@Composable
fun AssociationSearch(
    onAssociationClicked: (Association) -> Unit,
    associations: List<Association>
) {
    AssociationListScreen(
        associations,
        onAssociationClicked = onAssociationClicked,
        onRowClicked = onAssociationClicked,
        eventsFilter = emptyList()
    )
}
