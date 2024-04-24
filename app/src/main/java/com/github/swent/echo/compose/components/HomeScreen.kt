package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.swent.echo.compose.components.searchmenu.FiltersContainer
import com.github.swent.echo.compose.components.searchmenu.SortBy
import com.github.swent.echo.compose.map.MapDrawer
import com.github.swent.echo.data.SAMPLE_EVENTS
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.ui.navigation.NavigationActions
import java.time.ZonedDateTime

enum class Overlay {
    NONE,
    EVENT_INFO_SHEET,
    SEARCH_SHEET
}

enum class MapOrListMode {
    MAP,
    LIST
}
/**
 * The scaffold for the home screen. contains the top bar, the bottom sheet if an event is selected,
 * the hamburger menu when the button is clicked on the top bar, the search bottom sheet, the list
 * view, and the map.
 */
@Composable
fun HomeScreen(navActions: NavigationActions) {
    // State for the MapOrListMode
    val overlay = remember { mutableStateOf(Overlay.NONE) }
    val mode = remember { mutableStateOf(MapOrListMode.MAP) }
    val displayEventInfo = remember { mutableStateOf<Event?>(null) }

    // Drawer state to open and close the hamburger menu
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Hamburger menu compose
    ModalNavigationDrawer(
        // Content of the hamburger menu
        drawerContent = { HamburgerMenuDrawerSheet(navActions, drawerState, scope) },
        drawerState = drawerState,
        modifier = Modifier.testTag("hamburger_menu"),
        gesturesEnabled = drawerState.isOpen
    ) {
        Scaffold(
            modifier = Modifier.testTag("home_screen"),
            topBar = { TopAppBar(scope, drawerState, mode) },
            floatingActionButton = {
                SearchButton(onClick = { overlay.value = Overlay.SEARCH_SHEET })
            }
        ) { paddingValues ->
            Content(paddingValues, overlay, mode, navActions, displayEventInfo)
        }
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    overlay: MutableState<Overlay>,
    mode: MutableState<MapOrListMode>,
    navActions: NavigationActions,
    displayEventInfo: MutableState<Event?>
) {
    fun onEventSelected(
        event: Event,
    ) {
        displayEventInfo.value = event
        overlay.value = Overlay.EVENT_INFO_SHEET
    }

    val filters =
        FiltersContainer(
            tagId = remember { mutableStateOf("") },
            epflChecked = remember { mutableStateOf(true) },
            sectionChecked = remember { mutableStateOf(true) },
            classChecked = remember { mutableStateOf(true) },
            pendingChecked = remember { mutableStateOf(true) },
            confirmedChecked = remember { mutableStateOf(true) },
            fullChecked = remember { mutableStateOf(true) },
            from = remember { mutableStateOf(ZonedDateTime.now()) },
            to = remember { mutableStateOf(ZonedDateTime.now()) },
            sortBy = remember { mutableStateOf(SortBy.NONE) }
        )

    Box(modifier = Modifier.padding(paddingValues)) {
        if (mode.value == MapOrListMode.LIST) {
            // TODO add the list view
        } else {
            MapDrawer(
                events = SAMPLE_EVENTS,
                callback = { event -> onEventSelected(event) },
            )
        }
        // add the tag filtering here
        if (overlay.value == Overlay.EVENT_INFO_SHEET && displayEventInfo.value != null) {
            EventInfoSheet(
                event = displayEventInfo.value!!,
                onJoinButtonPressed = {},
                onShowPeopleButtonPressed = {},
                onDismiss = { overlay.value = Overlay.NONE },
                onFullyExtended = {}
            )
            // {navActions.navigateTo(Routes.EventInfoScreen)}) <- when we make a whole screen for
            // the event info
        }

        if (overlay.value == Overlay.SEARCH_SHEET) {
            SearchMenuSheet(
                filters,
                onFullyExtended = {},
                onDismiss = { overlay.value = Overlay.NONE }
            )
            // {navActions.navigateTo(Routes.SearchScreen)}) <- when we make a whole screen for
            // the search menu
        }
    }
}
