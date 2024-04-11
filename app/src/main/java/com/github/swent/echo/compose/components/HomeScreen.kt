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
import androidx.compose.ui.platform.testTag
import com.github.swent.echo.compose.map.MAP_CENTER
import com.github.swent.echo.compose.map.MapDrawer
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.ui.navigation.NavigationActions
import java.time.Instant
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

data class DisplayEventInfo(
    val event: Event,
    val eventImage: Int,
    val eventPeople: Int,
    val eventPeopleMax: Int,
    val hostName: String
)

/**
 * The scaffold for the home screen. contains the top bar, the bottom sheet if an event is selected,
 * the hamburger menu when the button is clicked on the top bar, the search bottom sheet, the list
 * view, and the map.
 */
@Composable
fun HomeScreen(navActions: NavigationActions) {
    val overlay = remember { mutableStateOf(Overlay.NONE) }
    val mode = remember { mutableStateOf(MapOrListMode.MAP) }
    val displayEventInfo = remember { mutableStateOf<DisplayEventInfo?>(null) }
    Scaffold(
        modifier = Modifier.testTag("home_screen"),
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
        Content(paddingValues, overlay, mode, navActions, displayEventInfo)
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    overlay: MutableState<Overlay>,
    mode: MutableState<MapOrListMode>,
    navActions: NavigationActions,
    displayEventInfo: MutableState<DisplayEventInfo?>
) {

    fun onEventSelected(
        event: Event,
        eventImage: Int = 0,
        eventPeople: Int = 0,
        eventPeopleMax: Int = 0,
        hostName: String = ""
    ) {
        displayEventInfo.value =
            DisplayEventInfo(event, eventImage, eventPeople, eventPeopleMax, hostName)
        overlay.value = Overlay.EVENT_INFO_SHEET
    }

    val events =
        listOf(
            Event(
                eventId = "a",
                organizerId = "a",
                title = "Bowling Event",
                description = "",
                location = Location("Location 1", MAP_CENTER.toGeoPoint()),
                startDate = Date.from(Instant.now()),
                endDate = Date.from(Instant.now()),
                tags = emptySet(),
            ),
            Event(
                eventId = "b",
                organizerId = "a",
                title = "Swimming Event",
                description = "",
                location =
                    Location("Location 2", MAP_CENTER.toGeoPoint().destinationPoint(1000.0, 90.0)),
                startDate = Date.from(Instant.now()),
                endDate = Date.from(Instant.now()),
                tags = emptySet(),
            )
        )

    Box(modifier = Modifier.padding(paddingValues)) {
        if (mode.value == MapOrListMode.LIST) {
            // TODO add the list view
        } else {
            MapDrawer(events = events, callback = { event -> onEventSelected(event) })
        }
        // add the tag filtering here
        if (overlay.value == Overlay.EVENT_INFO_SHEET && displayEventInfo.value != null) {

            EventInfoSheet(
                eventImage = displayEventInfo.value!!.eventImage,
                eventPeople = displayEventInfo.value!!.eventPeople,
                eventPeopleMax = displayEventInfo.value!!.eventPeopleMax,
                hostName = displayEventInfo.value!!.hostName,
                event = displayEventInfo.value!!.event,
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
