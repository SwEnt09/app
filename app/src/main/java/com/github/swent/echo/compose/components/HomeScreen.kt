package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.swent.echo.compose.map.MapDrawer
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.HomeScreenViewModel
import com.github.swent.echo.viewmodels.MapOrListMode
import com.github.swent.echo.viewmodels.Overlay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * The scaffold for the home screen. contains the top bar, the bottom sheet if an event is selected,
 * the hamburger menu when the button is clicked on the top bar, the search bottom sheet, the list
 * view, and the map.
 */
@Composable
fun HomeScreen(
    navActions: NavigationActions,
    homeScreenViewModel: HomeScreenViewModel,
    hasLocationPermissions: Boolean = false
) {

    // Drawer state to open and close the hamburger menu
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val mode by homeScreenViewModel.mode.collectAsState()

    // Profile information for the hamburger menu
    val profileName by homeScreenViewModel.profileName.collectAsState()
    val profileClass by homeScreenViewModel.profileClass.collectAsState()

    // Search mode for displaying events
    val searchMode by homeScreenViewModel.searchMode.collectAsState()

    // Hamburger menu compose
    ModalNavigationDrawer(
        // Content of the hamburger menu
        drawerContent = {
            HamburgerMenuDrawerSheet(navActions, drawerState, scope, profileName, profileClass) {
                homeScreenViewModel.signOut()
                navActions.navigateTo(Routes.LOGIN)
            }
        },
        drawerState = drawerState,
        modifier = Modifier.testTag("hamburger_menu"),
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            modifier = Modifier.testTag("home_screen"),
            topBar = {
                TopAppBar(
                    scope,
                    drawerState,
                    mode,
                    searchMode,
                    homeScreenViewModel::resetFiltersContainer,
                    homeScreenViewModel::switchMode
                )
            },
            floatingActionButton = {
                SearchButton(onClick = { homeScreenViewModel.setOverlay(Overlay.SEARCH_SHEET) })
            }
        ) { paddingValues ->
            Content(paddingValues, navActions, homeScreenViewModel, hasLocationPermissions)
        }
    }
}

/**
 * The content of the home screen. Contains the list view or the map view, the tag UI, the event
 * info sheet, and the search menu sheet.
 */
@Composable
private fun Content(
    paddingValues: PaddingValues,
    navActions: NavigationActions,
    homeScreenViewModel: HomeScreenViewModel,
    hasLocationPermissions: Boolean = false
) {

    // display mode for the list or map view, and the overlay for the event info sheet or the search
    val mode by homeScreenViewModel.mode.collectAsState()
    val overlay by homeScreenViewModel.overlay.collectAsState()

    // display event info and event list
    val displayEventInfo by homeScreenViewModel.displayEventInfo.collectAsState()
    val displayEventList by homeScreenViewModel.displayEventList.collectAsState()

    // filters for the search menu
    val filters by homeScreenViewModel.filtersContainer.collectAsState()
    val canUserModifyEvent by homeScreenViewModel.canUserModifyEvent.collectAsState()

    // tags for the tag UI
    val tags by homeScreenViewModel.followedTags.collectAsState()
    val selectedTagIds by homeScreenViewModel.selectedTagIds.collectAsState()

    // section and semester for the search menu
    val section by homeScreenViewModel.section.collectAsState()
    val semester by homeScreenViewModel.semester.collectAsState()

    // search mode for displaying events
    val searchMode by homeScreenViewModel.searchMode.collectAsState()

    // online status for disabling the buttons
    val isOnline by homeScreenViewModel.isOnline.collectAsState()

    Box(modifier = Modifier.padding(paddingValues)) {
        // Display the list view or the map view
        if (mode == MapOrListMode.LIST) {
            Column {
                if (tags.isNotEmpty() && !searchMode) {
                    TagUI(
                        tags = tags,
                        selectedTagIds = selectedTagIds,
                        leftPadding = 8.dp,
                        onTagClick = homeScreenViewModel::onFollowedTagClicked
                    )
                }
                ListDrawer(displayEventList, isOnline, homeScreenViewModel::refreshEvents)
            }
        } else {
            MapDrawer(
                events = displayEventList,
                callback = homeScreenViewModel::onEventSelected,
                launchEventCreation = {
                    val encodedLocation = Json.encodeToString(Location("", it))
                    navActions.navigateTo(Routes.CREATE_EVENT.build(encodedLocation))
                },
                displayLocation = hasLocationPermissions
            )
            if (!searchMode) {
                TagUI(
                    tags = tags,
                    selectedTagIds = selectedTagIds,
                    leftPadding = 8.dp,
                    onTagClick = homeScreenViewModel::onFollowedTagClicked
                )
            }
        }

        // Display the event info sheet
        if (overlay == Overlay.EVENT_INFO_SHEET && displayEventInfo != null) {
            EventInfoSheet(
                event = displayEventInfo!!,
                onDismiss = homeScreenViewModel::clearOverlay,
                onFullyExtended = {},
                canModifyEvent = canUserModifyEvent,
                onModifyEvent = {
                    navActions.navigateTo(Routes.EDIT_EVENT.build(displayEventInfo!!.eventId))
                },
                isOnline = isOnline,
                refreshEvents = homeScreenViewModel::refreshEvents
            )
            // {navActions.navigateTo(Routes.EventInfoScreen)}) <- when we make a whole screen for
            // the event info
        }

        // Display the search menu sheet
        if (overlay == Overlay.SEARCH_SHEET) {
            SearchMenuSheet(
                filters,
                onFullyExtended = {},
                onDismiss = homeScreenViewModel::clearOverlay,
                searchEntryCallback = homeScreenViewModel::onSearchEntryChanged,
                epflCallback = homeScreenViewModel::onEpflCheckedSwitch,
                sectionCallback = homeScreenViewModel::onSectionCheckedSwitch,
                classCallback = homeScreenViewModel::onClassCheckedSwitch,
                pendingCallback = homeScreenViewModel::onPendingCheckedSwitch,
                confirmedCallback = homeScreenViewModel::onConfirmedCheckedSwitch,
                fullCallback = homeScreenViewModel::onFullCheckedSwitch,
                sortByCallback = homeScreenViewModel::onSortByChanged,
                resetFiltersCallback = homeScreenViewModel::resetFiltersContainer,
                timeFilterCallback = homeScreenViewModel::onDateFilterChanged
            )
            // {navActions.navigateTo(Routes.SearchScreen)}) <- when we make a whole screen for
            // the search menu
        }
    }
}

val colorEpfl = Color.Red.copy(0.6f)
val colorSection = Color.Blue.copy(0.6f)
val colorClass = Color.Green.copy(0.6f)
