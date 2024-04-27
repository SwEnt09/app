package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.swent.echo.compose.map.MapDrawer
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.viewmodels.HomeScreenViewModel
import com.github.swent.echo.viewmodels.MapOrListMode
import com.github.swent.echo.viewmodels.Overlay

/**
 * The scaffold for the home screen. contains the top bar, the bottom sheet if an event is selected,
 * the hamburger menu when the button is clicked on the top bar, the search bottom sheet, the list
 * view, and the map.
 */
@Composable
fun HomeScreen(navActions: NavigationActions, homeScreenViewModel: HomeScreenViewModel) {

    // Drawer state to open and close the hamburger menu
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val mode by homeScreenViewModel.mode.collectAsState()

    // Hamburger menu compose
    ModalNavigationDrawer(
        // Content of the hamburger menu
        drawerContent = {
            HamburgerMenuDrawerSheet(
                navActions,
                drawerState,
                scope
            ) { /* homeScreenViewModel.signOut() */}
        },
        drawerState = drawerState,
        modifier = Modifier.testTag("hamburger_menu"),
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            modifier = Modifier.testTag("home_screen"),
            topBar = { TopAppBar(scope, drawerState, mode) { homeScreenViewModel.switchMode() } },
            floatingActionButton = {
                SearchButton(onClick = { homeScreenViewModel.setOverlay(Overlay.SEARCH_SHEET) })
            }
        ) { paddingValues ->
            Content(paddingValues, navActions, homeScreenViewModel)
        }
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navActions: NavigationActions,
    homeScreenViewModel: HomeScreenViewModel
) {

    val mode by homeScreenViewModel.mode.collectAsState()
    val overlay by homeScreenViewModel.overlay.collectAsState()

    val displayEventInfo by homeScreenViewModel.displayEventInfo.collectAsState()
    val displayEventList by homeScreenViewModel.displayEventList.collectAsState()

    val filters by homeScreenViewModel.filtersContainer.collectAsState()

    Box(modifier = Modifier.padding(paddingValues)) {
        if (mode == MapOrListMode.LIST) {
            ListDrawer(displayEventList)
        } else {
            MapDrawer(
                events = displayEventList,
                callback = { event -> homeScreenViewModel.onEventSelected(event) },
            )
        }
        // add the tag filtering here
        if (overlay == Overlay.EVENT_INFO_SHEET && displayEventInfo != null) {
            EventInfoSheet(
                event = displayEventInfo!!,
                onJoinButtonPressed = {},
                onShowPeopleButtonPressed = {},
                onDismiss = { homeScreenViewModel.clearOverlay() },
                onFullyExtended = {}
            )
            // {navActions.navigateTo(Routes.EventInfoScreen)}) <- when we make a whole screen for
            // the event info
        }

        if (overlay == Overlay.SEARCH_SHEET) {
            SearchMenuSheet(
                filters,
                onFullyExtended = {},
                onDismiss = { homeScreenViewModel.clearOverlay() }
            )
            // {navActions.navigateTo(Routes.SearchScreen)}) <- when we make a whole screen for
            // the search menu
        }
    }
}
