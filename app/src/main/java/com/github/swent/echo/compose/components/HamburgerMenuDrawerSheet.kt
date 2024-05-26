package com.github.swent.echo.compose.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.compose.map.MAP_CENTER
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun HamburgerMenuDrawerSheet(
    navActions: NavigationActions,
    drawerState: DrawerState,
    scope: CoroutineScope,
    profileName: String,
    profileClass: String,
    profilePicture: Bitmap? = null,
    onSignOutPressed: () -> Unit,
    onToggle: () -> Unit
) {
    /**
     * List of navigation items to display in the hamburger menu
     *
     * Add a navOnClick to the item to navigate to the corresponding screen as soon as you implement
     * it.
     *
     * If you add new items, make sure to set correct number of items to test in HomeScreenTest.kt
     * There is two tests to modify, a comment is provided ahead of them
     */
    val items =
        listOf(
            NavigationItem(
                title = stringResource(id = R.string.hamburger_my_profile),
                selectedIcon = Icons.Filled.Person,
                navOnClick = { navActions.navigateTo(Routes.PROFILE_CREATION) }
            ),
            NavigationItem(
                title = stringResource(id = R.string.hamburger_my_events),
                selectedIcon = Icons.Filled.DateRange,
                navOnClick = { navActions.navigateTo(Routes.MY_EVENTS) }
            ),
            NavigationItem(
                title = stringResource(id = R.string.hamburger_create_event),
                selectedIcon = Icons.Filled.AddCircle,
                navOnClick = {
                    val encodedMapCenter = Json.encodeToString(MAP_CENTER)
                    navActions.navigateTo(Routes.CREATE_EVENT.build(encodedMapCenter))
                }
            ),
            NavigationItem(
                title = stringResource(R.string.hamburger_associations),
                selectedIcon = Icons.Filled.Star,
                navOnClick = { navActions.navigateTo(Routes.ASSOCIATIONS) }
            ),
            NavigationItem(
                title = stringResource(id = R.string.hamburger_help),
                selectedIcon = ImageVector.vectorResource(id = R.drawable.help),
                navOnClick = { navActions.navigateTo(Routes.HELP) }
            ),
            NavigationItem(
                title = stringResource(id = R.string.hamburger_log_out),
                selectedIcon = Icons.Filled.Close,
                navOnClick = { onSignOutPressed() }
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
                Image(
                    modifier = Modifier.testTag("profile_picture").size(150.dp).clip(CircleShape),
                    painter =
                        if (profilePicture != null) {
                            BitmapPainter(profilePicture!!.asImageBitmap())
                        } else {
                            painterResource(R.drawable.echologoround)
                        },
                    contentDescription = "profile picture"
                )
                Row(modifier = Modifier.padding(8.dp).testTag("profile_info")) {
                    Text(
                        text = profileName,
                        modifier = Modifier.testTag("profile_name"),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = profileClass,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.testTag("profile_class")
                    )
                }
            }
            // Close button for the hamburger menu
            IconButton(
                onClick = { scope.launch { drawerState.close() } },
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
            ThemeToggleButton(onToggle = onToggle)
        }
        // Display the navigation items
        items.forEachIndexed { index, item ->
            NavigationDrawerItem(
                label = { Text(text = item.title) },
                onClick = {
                    scope.launch { drawerState.close() }
                    item.navOnClick?.invoke()
                },
                selected = false,
                icon = { Icon(imageVector = item.selectedIcon, contentDescription = item.title) },
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
 * @param navOnClick the action to perform when the item is clicked
 */
data class NavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val navOnClick: (() -> Unit)? = {}
)

@Composable
fun ThemeToggleButton(onToggle: () -> Unit) {
    IconButton(onClick = onToggle, modifier = Modifier.testTag("theme_toggle_button")) {
        Icon(
            painter = painterResource(id = R.drawable.light_bulb_foreground),
            contentDescription = "Toggle theme"
        )
    }
}
