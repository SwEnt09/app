package com.github.swent.echo.compose.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview

/**
 * Top bar of the app with the title and navigation icons.
 *
 * @param onOpenMenu callback to open the menu
 * @param onOpenSearch callback to open the search screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onOpenMenu: () -> Unit, onOpenSearch: () -> Unit) {
    // Scroll behavior for the top app bar, makes it pinned
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    // Top app bar with title, navigation icon, and actions
    CenterAlignedTopAppBar(
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        title = {
            Text(
                "Echo",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis, // should not happen
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = { // hamburger menu
            IconButton(onClick = onOpenMenu, modifier = Modifier.testTag("menu_button")) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu to access all subpages"
                )
            }
        },
        actions = { // search icon
            IconButton(onClick = onOpenSearch, modifier = Modifier.testTag("search_button")) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search icon to access the search screen"
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@Preview
@Composable
fun TopBarPreview() {
    TopBar({}, {})
}
