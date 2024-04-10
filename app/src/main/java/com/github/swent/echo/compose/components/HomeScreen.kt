package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.swent.echo.compose.map.MapDrawer
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.ui.navigation.NavigationActions

enum class Overlay {
    NONE,
    EVENT_INFO_SHEET,
    SEARCH_SHEET,
    HAMBURGER_MENU
}

enum class MapOrListMode {
    MAP,
    LIST
}

data class EventInfo(
    val image: Int,
    val people: Int,
    val maxPeople: Int,
    val hostName: String,
    val event: Event
)

/**
 * The scaffold for the home screen. contains the top bar, the bottom sheet if an event is selected,
 * the hamburger menu when the button is clicked on the top bar, the search bottom sheet, the list view, and the map.
 */
@Composable
fun HomeScreen(navActions: NavigationActions) {
    val overlay = remember { mutableStateOf(Overlay.NONE) }
    val mode = remember { mutableStateOf(MapOrListMode.MAP) }
    Scaffold(
        topBar = {
            TopBar(
                { overlay.value = Overlay.HAMBURGER_MENU },
                {
                    if (mode.value == MapOrListMode.MAP) {
                        mode.value = MapOrListMode.LIST
                    } else {
                        mode.value = MapOrListMode.MAP
                    }
                }
            )
        },
        floatingActionButton = { SearchButton(onClick = { overlay.value = Overlay.SEARCH_SHEET }) }
    ) { paddingValues ->
        Content(paddingValues, overlay, mode, navActions)
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    overlay: MutableState<Overlay>,
    mode: MutableState<MapOrListMode>,
    navActions: NavigationActions
) {
    Box(modifier = Modifier.padding(paddingValues)) {
        if (mode.value == MapOrListMode.LIST) {
            // TODO add the list view
        } else {
            MapDrawer()
        }
        // add the tag filtering here
        if (overlay.value == Overlay.EVENT_INFO_SHEET) {
            // TODO get the event information from the repository
            /*
            EventInfoSheet(
                eventImage = image,
                eventPeople = people,
                eventPeopleMax = maxPeople,
                hostName = hostName,
                event = event,
                onJoinButtonPressed = {},
                onShowPeopleButtonPressed = {},
                onDismiss = { overlay.value = Overlay.NONE },
                onFullyExtended = {}
            ) */
              // {navActions.navigateTo(Routes.EventInfoScreen)}) <- when we make a whole screen for
              // the event info
        }

        if (overlay.value == Overlay.SEARCH_SHEET) {
            // TODO add the search bottom sheet
        }
        if (overlay.value == Overlay.HAMBURGER_MENU) {
            // TODO add the hamburger menu
        }
    }
}