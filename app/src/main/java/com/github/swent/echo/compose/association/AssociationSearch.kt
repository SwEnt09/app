package com.github.swent.echo.compose.association

import androidx.compose.runtime.Composable
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.viewmodels.association.AssociationPage

@Composable
fun AssociationSearch(goTo: (AssociationPage) -> Unit, associations: List<Association>) {
    AssociationListScreen(
        associations,
        onAssociationClicked = {
            val nextPage = AssociationPage.DETAILS
            nextPage.association = it
            goTo(nextPage)
        },
        onRowClicked = {
            val nextPage = AssociationPage.DETAILS
            nextPage.association = it
            goTo(nextPage)
        },
        eventsFilter = emptyList()
    )
}
