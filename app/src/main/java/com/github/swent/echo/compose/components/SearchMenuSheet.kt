package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMenuSheet(onFullyExtended: () -> Unit, onDismiss: () -> Unit) {
    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = false) { value ->
            if (value == SheetValue.Expanded) {
                onFullyExtended()
            }
            true
        }
    ModalBottomSheet(
        modifier = Modifier.fillMaxSize().testTag("search_menu_sheet"),
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 32.dp)
        ) {
            // Sheet content
            Text(text = "Search Menu Sheet") // temporary text to show the sheet is working
        }
    }
}
