package com.github.swent.echo.compose.association

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.SearchButton
import com.github.swent.echo.compose.event.EventTitleAndBackButton
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.association.AssociationOverlay
import com.github.swent.echo.viewmodels.association.AssociationPage
import com.github.swent.echo.viewmodels.association.AssociationViewModel

@Composable
fun AssociationScreen(associationViewModel: AssociationViewModel, navActions: NavigationActions) {
    val followedAssociations by associationViewModel.followedAssociations.collectAsState()
    val committeeAssociations by associationViewModel.committeeAssociations.collectAsState()

    val filteredEvents by associationViewModel.filteredEvents.collectAsState()
    val eventsFilter by associationViewModel.eventsFilter.collectAsState()

    val currentAssociationPage by associationViewModel.currentAssociationPage.collectAsState()

    val overlay by associationViewModel.overlay.collectAsState()
    val searched by associationViewModel.searched.collectAsState()

    val isOnline by associationViewModel.isOnline.collectAsState()

    fun goBack() {
        if (currentAssociationPage == AssociationPage.MAINSCREEN) {
            navActions.navigateTo(Routes.MAP)
        } else {
            associationViewModel.goBack()
        }
    }

    fun onAssociationClicked(association: Association) {
        val nextPage = AssociationPage.DETAILS
        nextPage.association = association
        associationViewModel.goTo(nextPage)
    }

    Scaffold(
        topBar = {
            EventTitleAndBackButton(stringResource(R.string.hamburger_associations)) { goBack() }
        },
        floatingActionButton = {
            SearchButton(
                onClick = {
                    if (currentAssociationPage != AssociationPage.SEARCH) {
                        associationViewModel.goTo(AssociationPage.SEARCH)
                    }
                    associationViewModel.setOverlay(AssociationOverlay.SEARCH)
                }
            )
        },
        modifier = Modifier.fillMaxSize().testTag("association_screen")
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentAssociationPage) {
                AssociationPage.MAINSCREEN -> {
                    AssociationMainScreen(
                        filteredEvents,
                        { onAssociationClicked(it) },
                        { associationViewModel.onAssociationToFilterChanged(it) },
                        followedAssociations,
                        committeeAssociations,
                        eventsFilter,
                        isOnline,
                        associationViewModel::refreshEvents
                    )
                }
                AssociationPage.DETAILS -> {
                    AssociationDetails(
                        { associationViewModel.onFollowAssociationChanged(it) },
                        currentAssociationPage.association,
                        followedAssociations.contains(currentAssociationPage.association),
                        associationViewModel.associationEvents(currentAssociationPage.association),
                        isOnline,
                        associationViewModel::refreshEvents
                    )
                }
                AssociationPage.SEARCH -> {
                    AssociationSearch(
                        { onAssociationClicked(it) },
                        associationViewModel.filterAssociations()
                    )
                    if (overlay == AssociationOverlay.SEARCH) {
                        AssociationSearchBottomSheet(
                            {},
                            { associationViewModel.setOverlay(AssociationOverlay.NONE) },
                            { associationViewModel.setSearched(it) },
                            searched
                        )
                    }
                }
            }
        }
    }
}
