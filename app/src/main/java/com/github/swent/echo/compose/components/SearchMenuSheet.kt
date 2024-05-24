package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.searchmenu.FiltersContainer
import com.github.swent.echo.compose.components.searchmenu.SearchMenuDiscover
import com.github.swent.echo.compose.components.searchmenu.SearchMenuFilters
import com.github.swent.echo.viewmodels.tag.TagViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMenuSheet(
    filters: FiltersContainer,
    onFullyExtended: () -> Unit,
    onDismiss: () -> Unit,
    searchEntryCallback: (String) -> Unit,
    epflCallback: () -> Unit,
    sectionCallback: () -> Unit,
    classCallback: () -> Unit,
    pendingCallback: () -> Unit,
    confirmedCallback: () -> Unit,
    fullCallback: () -> Unit,
    resetFiltersCallback: () -> Unit,
    timeFilterCallback: (Float, Float) -> Unit
) {
    // Search mode
    val searchMode = remember { mutableStateOf(SearchMode.FILTERS) }
    // TagViewModel
    val tagViewModel: TagViewModel =
        hiltViewModel<TagViewModel, TagViewModel.TagViewModelFactory>() { factory ->
            factory.create()
        }

    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = false) { value ->
            if (value == SheetValue.Expanded) {
                onFullyExtended()
            }
            true
        }
    ModalBottomSheet(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f).testTag("search_menu_sheet"),
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(5.dp).testTag("search_menu_sheet_content")) {
            // Sheet content
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.TopCenter).testTag("search_menu_first_layer")
            ) {
                // Search bar
                SearchBarTags(filters.searchEntry, searchEntryCallback)
                Spacer(modifier = Modifier.width(10.dp))
                // Switch search mode button
                SwitchSearchModeButton(searchMode)
            }
            // Display filters or discover according to the selected mode
            Box(
                modifier =
                    Modifier.align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(220.dp)
                        .absoluteOffset(y = 70.dp)
                        .testTag("search_menu_second_layer")
            ) {
                if (searchMode.value == SearchMode.FILTERS) {
                    SearchMenuFilters(
                        filters,
                        epflCallback,
                        sectionCallback,
                        classCallback,
                        pendingCallback,
                        confirmedCallback,
                        fullCallback,
                        timeFilterCallback
                    )
                } else {
                    SearchMenuDiscover(searchEntryCallback, tagViewModel)
                }
            }
            // Close Search Button
            Row(
                modifier =
                    Modifier.align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .absoluteOffset(y = 300.dp)
                        .testTag("search_menu_third_layer")
            ) {
                ResetFiltersButton(resetFiltersCallback, tagViewModel)
            }
        }
    }
}
/** Enum class for the different states of the search mode */
enum class SearchMode(val switchToName: String, val switchToIcon: ImageVector) {
    FILTERS("search_menu_sheet_discover", Icons.Filled.ShoppingCart),
    DISCOVER("search_menu_sheet_filters", Icons.Filled.Settings)
}
// Function to get the string resource for the search mode
fun stringResourceSearchMode(key: String): Int {
    return when (key) {
        "search_menu_sheet_discover" -> R.string.search_menu_sheet_discover
        "search_menu_sheet_filters" -> R.string.search_menu_sheet_filters
        else -> throw IllegalArgumentException("Invalid string key")
    }
}
/** Composable for the search bar tags TODO : update this with yoan implementation */
@Composable
fun SearchBarTags(searched: String, searchEntryCallback: (String) -> Unit) {
    OutlinedTextField(
        label = { Text(stringResource(id = R.string.search_menu_sheet_search_interests)) },
        value = searched,
        onValueChange = searchEntryCallback,
        modifier = Modifier.width(240.dp).testTag("search_menu_search_bar_tags")
    )
}
/** Composable for the switch search mode button */
@Composable
fun SwitchSearchModeButton(searchMode: MutableState<SearchMode>) {
    Button(
        onClick = {
            searchMode.value =
                if (searchMode.value == SearchMode.FILTERS) {
                    SearchMode.DISCOVER
                } else {
                    SearchMode.FILTERS
                }
        },
        modifier = Modifier.fillMaxWidth().height(40.dp).testTag("search_menu_switch_mode_button")
    ) {
        Icon(
            searchMode.value.switchToIcon,
            contentDescription =
                stringResource(id = stringResourceSearchMode(searchMode.value.switchToName)),
            modifier = Modifier.testTag("search_menu_switch_mode_button_icon")
        )
        Text(
            stringResource(id = stringResourceSearchMode(searchMode.value.switchToName)),
            modifier = Modifier.testTag("search_menu_switch_mode_button_text")
        )
    }
}
/**
 * Composable for the reset filters button, inside a Box in order to hide content when we will
 * implement discover mode
 */
// Todo : see how we want to handle this reset filters button
@Composable
fun ResetFiltersButton(callback: () -> Unit, tagViewModel: TagViewModel) {
    Box(modifier = Modifier.fillMaxWidth().testTag("search_menu_reset_filters_button")) {
        Button(
            onClick = {
                callback()
                tagViewModel.reset()
            },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(stringResource(id = R.string.search_menu_sheet_reset_filters))
        }
    }
}
