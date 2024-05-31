package com.github.swent.echo.compose.association

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.Pager
import com.github.swent.echo.compose.components.SearchBar
import com.github.swent.echo.compose.event.EventTitleAndBackButton
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.association.AssociationViewModel

/**
 * A screen which displays associations. This Composable function takes an AssociationViewModel and
 * NavigationActions. It uses a Scaffold to provide a layout structure for the screen.
 */
@Composable
fun AssociationScreen(
    associationViewModel:
        AssociationViewModel, // The ViewModel that provides the data for the screen
    navActions: NavigationActions // The actions that can be performed for navigation
) {
    // Collect the state of followed associations, committee associations, and all associations
    val followedAssociations by associationViewModel.followedAssociations.collectAsState()
    val committeeAssociations by associationViewModel.committeeAssociations.collectAsState()
    val showAllAssociations by associationViewModel.showAllAssociations.collectAsState()

    // Create a list of pages for the Pager
    val pages =
        listOf(
            Pair("Followed Associations", followedAssociations),
            Pair("Committee Associations", committeeAssociations),
            Pair("All Associations", showAllAssociations)
        )

    // Collect the state of the current association page and the initial page
    val currentAssociationPage by associationViewModel.currentAssociationPage.collectAsState()
    val initialPage by associationViewModel.initialPage.collectAsState()

    // Collect the state of the searched text
    val searched by associationViewModel.searched.collectAsState()

    // Collect the state of the online status
    val isOnline by associationViewModel.isOnline.collectAsState()

    // Define the space between the search bar and the pages
    val spaceBetweenSearchAndPages = 8.dp

    // Scaffold provides a framework for material design surfaces
    Scaffold(
        topBar = {
            // Display the title and back button
            EventTitleAndBackButton(stringResource(R.string.hamburger_associations)) {
                if (currentAssociationPage == Association.EMPTY) {
                    // Navigate to the map if the current association page is empty
                    navActions.navigateTo(Routes.MAP)
                } else {
                    // Set the current association page to empty
                    associationViewModel.setCurrentAssociationPage(Association.EMPTY)
                }
            }
        },
        modifier = Modifier.fillMaxSize().testTag("association_screen")
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Check if the current association page is empty, which means we must display the
            // default screen
            if (currentAssociationPage == Association.EMPTY) {
                // Display the search bar
                SearchBar(
                    stringResource(R.string.associations_categories),
                    searched,
                    associationViewModel::setSearched
                )
                Spacer(modifier = Modifier.height(spaceBetweenSearchAndPages))
                // Display the pager with the list of pages
                Pager(
                    pages.mapIndexed { id, page ->
                        Pair(page.first) {
                            AssociationListScreen(
                                associationViewModel.filterAssociations(page.second)
                            ) {
                                associationViewModel.setCurrentAssociationPage(it, id)
                            }
                        }
                    },
                    initialPage
                )
            } else {
                // Display the association details
                AssociationDetails(
                    currentAssociationPage,
                    followedAssociations.contains(currentAssociationPage),
                    { associationViewModel.onFollowAssociationChanged(it) },
                    associationViewModel.associationEvents(currentAssociationPage),
                    isOnline,
                    associationViewModel::refreshEvents,
                    userId = associationViewModel.userId,
                    modify = { navActions.navigateTo(Routes.EDIT_EVENT.build(it.eventId)) },
                )
            }
        }
    }
}
