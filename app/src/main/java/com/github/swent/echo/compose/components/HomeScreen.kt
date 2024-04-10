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
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.ui.navigation.NavigationActions
import java.util.Date

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
fun HomeScreen(navActions: NavigationActions? = null) {
    val overlay = remember { mutableStateOf(Overlay.NONE) }
    val mode = remember { mutableStateOf(MapOrListMode.MAP) }
    Scaffold(
        topBar = {
            TopBar(
                {},
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
    navActions: NavigationActions?
) {
    Box(modifier = Modifier.padding(paddingValues)) {
        if (mode.value == MapOrListMode.LIST) {
            // TODO add the list view
        } else {
            MapDrawer()
        }
        // add the tag filtering here
        if (overlay.value == Overlay.EVENT_INFO_SHEET) {
            // TODO get the event from the repository
            val event =
                Event(
                    eventId = "1",
                    organizerId = "1",
                    title = "Event Title",
                    description = "Event Description",
                    location = Location("", 0.0, 0.0),
                    startDate = Date(2024, 5, 18, 18, 15),
                    endDate = Date(2024, 1, 1, 22, 0),
                    tags = setOf()
                )
            EventInfoSheet(
                eventImage = 0 /* TODO get the image from the repository*/,
                eventPeople = 2 /* TODO get the number of people from the repository */,
                eventPeopleMax = 4 /* TODO get the max number of people from the repository */,
                hostName = "Bowling club" /* TODO get the host name from the repository */,
                event = event /* TODO get the event from the repository */,
                onJoinButtonPressed = {},
                onShowPeopleButtonPressed = {},
                onDismiss = { overlay.value = Overlay.NONE },
                onFullyExtended = {}
            )
            // {navActions.navigateTo(Routes.EventInfoScreen)}) <- when we make a whole screen for
            // the event info
        }

        if (overlay.value == Overlay.SEARCH_SHEET) {
            SearchMenuSheet(onFullyExtended = {}, onDismiss = { overlay.value = Overlay.NONE })
            // {navActions.navigateTo(Routes.SearchScreen)}) <- when we make a whole screen for
            // the search menu
        }
    }
}
