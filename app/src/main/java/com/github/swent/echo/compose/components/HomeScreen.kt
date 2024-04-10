package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
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
            TopBar(navActions) {
                if (mode.value == MapOrListMode.MAP) {
                    mode.value = MapOrListMode.LIST
                } else {
                    mode.value = MapOrListMode.MAP
                }
            )
        )

    // Hamburger menu compose
    ModalNavigationDrawer(
        // Content of the hamburger menu
        drawerContent = {
            ModalDrawerSheet {
                // Profile picture, name and class
                Box(
                    modifier =
                    Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                        .fillMaxWidth()
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag("profile_box")
                ) {
                    Column(
                        modifier =
                        Modifier.align(Alignment.TopStart)
                            .padding(8.dp)
                            .testTag("profile_sheet")
                    ) {
                        // TODO: Replace with actual profile picture
                        Image(
                            modifier = Modifier.testTag("profile_picture"),
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "profile picture"
                        )
                        Row(modifier = Modifier.padding(8.dp).testTag("profile_info")) {
                            // TODO: Replace with actual name and class
                            Text(
                                text = "John Doe",
                                modifier = Modifier.testTag("profile_name"),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "IN - BA6",
                                color = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.testTag("profile_class")
                            )
                        }
                    }
                    // Close button for the hamburger menu
                    IconButton(
                        onClick = {
                            selectedItemIndex = 0
                            scope.launch { drawerState.close() }
                        },
                        modifier =
                        Modifier.align(Alignment.TopEnd)
                            .padding(8.dp)
                            .testTag("close_button_hamburger_menu")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close button hamburger menu"
                        )
                    }
                }

                // Display the navigation items
                items.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.title) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            scope.launch { drawerState.close() }
                            item.navOnClick?.invoke()
                        },
                        icon = {
                            Icon(
                                imageVector =
                                if (index == selectedItemIndex) {
                                    item.selectedIcon
                                } else {
                                    item.unselectedIcon
                                },
                                contentDescription = item.title
                            )
                        },
                        badge = {
                            item.badgeCount?.let { Text(text = item.badgeCount.toString()) }
                        },
                        modifier =
                        Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            .testTag("navigation_item_$index")
                    )
                }
            }
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
    ) {
        displayEventInfo.value = DisplayEventInfo(event, 0, 0, 0, "")
        overlay.value = Overlay.EVENT_INFO_SHEET
    }

    val events =
        listOf(
            Event(
                eventId = "a",
                organizerId = "a",
                title = "Bowling Event",
                description = "",
                location =
                    Location(
                        "Location 1",
                        MAP_CENTER.toGeoPoint(),
                    ),
                startDate =
                    Date.from(
                        Instant.now(),
                    ),
                endDate =
                    Date.from(
                        Instant.now(),
                    ),
                tags = emptySet(),
            ),
            Event(
                eventId = "b",
                organizerId = "a",
                title = "Swimming Event",
                description = "",
                location =
                    Location(
                        "Location 2",
                        MAP_CENTER.toGeoPoint()
                            .destinationPoint(
                                1000.0,
                                90.0,
                            ),
                    ),
                startDate =
                    Date.from(
                        Instant.now(),
                    ),
                endDate =
                    Date.from(
                        Instant.now(),
                    ),
                tags = emptySet(),
            )
        )

    Box(modifier = Modifier.padding(paddingValues)) {
        if (mode.value == MapOrListMode.LIST) {
            // TODO add the list view
        } else {
            MapDrawer(
                events = events,
                callback = { event -> onEventSelected(event) },
            )
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
