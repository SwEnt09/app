package com.github.swent.echo.compose.association

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.swent.echo.compose.components.SearchBarTags
import com.github.swent.echo.compose.components.searchmenu.SearchMenuDiscover
import com.github.swent.echo.viewmodels.tag.TagViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssociationSearchBottomSheet(
    onFullyExtended: () -> Unit,
    onDismiss: () -> Unit,
    searchEntryCallback: (String) -> Unit,
    searched: String
) {
    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = false) { value ->
            if (value == SheetValue.Expanded) {
                onFullyExtended()
            }
            true
        }
    val bottomSheetHeightFraction = 0.5f
    val paddingInsideBottomSheet = 5.dp
    ModalBottomSheet(
        modifier =
            Modifier.fillMaxWidth()
                .fillMaxHeight(bottomSheetHeightFraction)
                .testTag("search_menu_sheet"),
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.padding(paddingInsideBottomSheet)) {
            SearchBarTags(searched, searchEntryCallback)
            val tagViewModel: TagViewModel = hiltViewModel()
            SearchMenuDiscover(searchEntryCallback, tagViewModel)
        }
    }
}
