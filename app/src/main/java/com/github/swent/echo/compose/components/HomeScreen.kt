package com.github.swent.echo.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.compose.map.MAP_CENTER
import com.github.swent.echo.compose.map.MapDrawer
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.ui.navigation.NavigationActions
import java.time.ZonedDateTime
import kotlinx.coroutines.launch

/**
 * data class to display the navigation items in the hamburger menu.
 *
 * @param title the title of the navigation item
 * @param selectedIcon the icon to display when the item is selected
 * @param unselectedIcon the icon to display when the item is not selected
 * @param badgeCount the count to display as a badge on the item (for example friends requests)
 * @param navOnClick the action to perform when the item is clicked
 */
data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null,
    val navOnClick: (() -> Unit)? = {}
)

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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navActions: NavigationActions) {
    // State for the MapOrListMode
    val overlay = remember { mutableStateOf(Overlay.NONE) }
    val mode = remember { mutableStateOf(MapOrListMode.MAP) }
    val displayEventInfo = remember { mutableStateOf<Event?>(null) }

    // Scroll behavior for the top app bar, makes it pinned
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    // Drawer state to open and close the hamburger menu
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    // List of navigation items to display in the hamburger menu
    // TODO: Change the icons to be more meaningful when we have the google icons
    // TODO: Add a navOnClick to the item to navigate to the corresponding screen as soon as you
    // implement it
    val items =
        listOf(
            NavigationItem(
                title = stringResource(id = R.string.hamburger_my_profile),
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person
            ),
            NavigationItem(
                title = stringResource(id = R.string.hamburger_my_events),
                selectedIcon = Icons.Filled.DateRange,
                unselectedIcon = Icons.Outlined.DateRange,
            ),
            NavigationItem(
                title = stringResource(id = R.string.hamburger_friends),
                selectedIcon = Icons.Filled.Face,
                unselectedIcon = Icons.Outlined.Face,
                badgeCount = 3
            ),
            NavigationItem(
                title = stringResource(id = R.string.hamburger_settings),
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
            ),
            NavigationItem(
                title = stringResource(id = R.string.hamburger_create_event),
                selectedIcon = Icons.Filled.AddCircle,
                unselectedIcon = Icons.Outlined.AddCircle,
            ),
            NavigationItem(
                title = stringResource(id = R.string.hamburger_add_friends),
                selectedIcon = Icons.Filled.Add,
                unselectedIcon = Icons.Outlined.Add,
            ),
            NavigationItem(
                title = stringResource(id = R.string.hamburger_help),
                selectedIcon = Icons.Filled.Build,
                unselectedIcon = Icons.Outlined.Build,
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
        drawerState = drawerState,
        modifier = Modifier.testTag("hamburger_menu"),
        gesturesEnabled = false
    ) {
        Scaffold(
            modifier = Modifier.testTag("home_screen"),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.app_title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis, // should not happen
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = { // hamburger menu
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Menu to access all subpages"
                            )
                        }
                    },
                    actions = { // mode switch icon
                        IconButton(
                            onClick = {
                                if (mode.value == MapOrListMode.MAP) {
                                    mode.value = MapOrListMode.LIST
                                } else {
                                    mode.value = MapOrListMode.MAP
                                }
                            },
                            modifier = Modifier.testTag("list_map_mode_button")
                        ) {
                            Icon(
                                painter =
                                    if (mode.value == MapOrListMode.MAP) {
                                        rememberVectorPainter(image = Icons.Filled.List)
                                    } else {
                                        painterResource(id = R.drawable.map_icon)
                                    },
                                contentDescription = "Search icon to access the search screen"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.testTag("top_bar")
                )
            },
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

    val events =
        listOf(
            Event(
                eventId = "a",
                organizerId = "a",
                organizerName = "a",
                creatorId = "d",
                title = "Bowling Event",
                description = "",
                location =
                    Location(
                        "Location 1",
                        MAP_CENTER.toGeoPoint(),
                    ),
                startDate = ZonedDateTime.now(),
                endDate = ZonedDateTime.now(),
                tags = emptySet(),
                participantCount = 0,
                maxParticipants = 0,
                imageId = 0
            ),
            Event(
                eventId = "b",
                organizerId = "a",
                organizerName = "a",
                creatorId = "e",
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
                startDate = ZonedDateTime.now(),
                endDate = ZonedDateTime.now(),
                tags = emptySet(),
                participantCount = 0,
                maxParticipants = 0,
                imageId = 0
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
            SearchMenuSheet(onFullyExtended = {}, onDismiss = { overlay.value = Overlay.NONE })
            // {navActions.navigateTo(Routes.SearchScreen)}) <- when we make a whole screen for
            // the search menu
        }
    }
}
