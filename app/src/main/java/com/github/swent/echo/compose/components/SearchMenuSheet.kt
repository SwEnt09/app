package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.searchmenu.FiltersContainer
import com.github.swent.echo.compose.components.searchmenu.SearchMenuDiscover
import com.github.swent.echo.compose.components.searchmenu.SearchMenuFilters
import com.github.swent.echo.viewmodels.MapOrListMode
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
    sortByCallback: (Int) -> Unit,
    resetFiltersCallback: () -> Unit,
    timeFilterCallback: (Float, Float) -> Unit,
    initialPage: Int,
    mode: MapOrListMode
) {
    // TagViewModel
    val tagViewModel: TagViewModel =
        hiltViewModel<TagViewModel, TagViewModel.TagViewModelFactory> { factory ->
            factory.create()
        }

    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true) { value ->
            if (value == SheetValue.Expanded) {
                onFullyExtended()
            }
            true
        }
    val bottomSheetHeight = 500.dp
    val paddingValues = 5.dp
    val pageHeight = 385.dp
    val paddingBottomResetFilters = 10.dp
    ModalBottomSheet(
        modifier = Modifier.fillMaxWidth().height(bottomSheetHeight).testTag("search_menu_sheet"),
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Box(
            modifier =
                Modifier.fillMaxSize().padding(paddingValues).testTag("search_menu_sheet_content")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().height(pageHeight).align(Alignment.TopCenter)
            ) {
                // Sheet content
                SearchBar(
                    stringResource(R.string.search_menu_sheet_search_interests),
                    filters.searchEntry,
                    searchEntryCallback
                )
                // Display filters or discover according to the selected mode
                Pager(
                    listOf(
                        Pair(stringResource(R.string.search_menu_sheet_filters)) {
                            SearchMenuFilters(
                                filters,
                                epflCallback,
                                sectionCallback,
                                classCallback,
                                pendingCallback,
                                confirmedCallback,
                                fullCallback,
                                sortByCallback,
                                timeFilterCallback,
                                mode
                            )
                        },
                        Pair(stringResource(R.string.search_menu_sheet_discover)) {
                            SearchMenuDiscover(searchEntryCallback, tagViewModel)
                        }
                    ),
                    initialPage
                )
            }
            // Close Search Button
            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = paddingBottomResetFilters)
            ) {
                ResetFiltersButton(resetFiltersCallback, tagViewModel)
            }
        }
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
