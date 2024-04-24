package com.github.swent.echo.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HamburgerMenuDrawerSheet(
    navActions: NavigationActions,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
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
                navOnClick = { navActions.navigateTo(Routes.CREATE_EVENT) }
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
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp).testTag("profile_sheet")
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
                badge = { item.badgeCount?.let { Text(text = item.badgeCount.toString()) } },
                modifier =
                    Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        .testTag("navigation_item_$index")
            )
        }
    }
}

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
