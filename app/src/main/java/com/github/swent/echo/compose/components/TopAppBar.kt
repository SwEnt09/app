package com.github.swent.echo.compose.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.github.swent.echo.R
import com.github.swent.echo.viewmodels.MapOrListMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * The top app bar for the app. Contains the app title, the hamburger menu, the search reset button,
 * and the list/map mode switch button.
 *
 * @param scope the coroutine scope to launch the drawer state
 * @param drawerState the drawer state to open and close the hamburger menu
 * @param mode the current mode of the app, either list or map
 * @param searchMode whether the search mode is active
 * @param resetSearch callback to reset the search
 * @param switchMode callback to switch between list and map mode
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    scope: CoroutineScope,
    drawerState: DrawerState,
    mode: MapOrListMode,
    searchMode: Boolean,
    resetSearch: () -> Unit,
    switchMode: () -> Unit
) {
    // Scroll behavior for the top app bar, makes it pinned
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

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
        actions = { // search reset button and list/map mode switch button
            if (searchMode) {
                IconButton(
                    onClick = resetSearch,
                    modifier = Modifier.testTag("search_reset_button")
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.search_off),
                        contentDescription = "Search reset icon to reset the search field"
                    )
                }
            }
            IconButton(onClick = switchMode, modifier = Modifier.testTag("list_map_mode_button")) {
                Icon(
                    painter =
                        if (mode == MapOrListMode.MAP) {
                            rememberVectorPainter(image = Icons.Filled.List)
                        } else {
                            painterResource(id = R.drawable.map_icon)
                        },
                    contentDescription =
                        "list/map mode switch icon to switch between list and map mode"
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = Modifier.testTag("top_bar")
    )
}
