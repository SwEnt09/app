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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun AssociationScreen(associationViewModel: AssociationViewModel, navActions: NavigationActions) {
    val followedAssociations by associationViewModel.followedAssociations.collectAsState()
    val committeeAssociations by associationViewModel.committeeAssociations.collectAsState()
    val showAllAssociations by associationViewModel.showAllAssociations.collectAsState()
    val pages = listOf(
        Pair("Followed Associations", followedAssociations),
        Pair("Committee Associations", committeeAssociations),
        Pair("All Associations", showAllAssociations)
    )

    var currentAssociationPage by remember { mutableStateOf(Association.EMPTY) }
    var initialPage by remember { mutableIntStateOf(0) }

    val searched by associationViewModel.searched.collectAsState()

    val isOnline by associationViewModel.isOnline.collectAsState()

    val spaceBetweenSearchAndPages = 8.dp

    Scaffold(
        topBar = {
            EventTitleAndBackButton(stringResource(R.string.hamburger_associations)) {
                if(currentAssociationPage == Association.EMPTY) {
                    navActions.navigateTo(Routes.MAP)
                } else {
                    currentAssociationPage = Association.EMPTY
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .testTag("association_screen")
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally) {
            if(currentAssociationPage == Association.EMPTY) {
                SearchBar("Associations/Categories", searched, associationViewModel::setSearched)
                Spacer(modifier = Modifier.height(spaceBetweenSearchAndPages))
                Pager(
                    pages.mapIndexed { id, page ->
                        Pair(page.first) {
                            AssociationListScreen(associationViewModel.filterAssociations(page.second)) {
                                currentAssociationPage = it
                                initialPage = id
                            }
                        }
                    },
                    initialPage
                )
            } else {
                AssociationDetails(
                    currentAssociationPage,
                    followedAssociations.contains(currentAssociationPage),
                    { associationViewModel.onFollowAssociationChanged(it) },
                    associationViewModel.associationEvents(currentAssociationPage),
                    isOnline,
                    associationViewModel::refreshEvents
                )
            }
        }
    }
}
