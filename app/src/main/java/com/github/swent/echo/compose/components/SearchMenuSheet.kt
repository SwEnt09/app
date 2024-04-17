package com.github.swent.echo.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class SearchMode(val switchToName: String, val switchToIcon: ImageVector){
    FILTERS("Discover", Icons.Filled.ShoppingCart),
    DISCOVER("Filters", Icons.Filled.Settings)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMenuSheet(onFullyExtended: () -> Unit, onDismiss: () -> Unit) {
    // Content of the search bar
    var searched by remember { mutableStateOf("") }

    // Search mode
    var searchMode by remember { mutableStateOf(SearchMode.DISCOVER) }

    // Sheet content
    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = false) { value ->
            if (value == SheetValue.Expanded) {
                onFullyExtended()
            }
            true
        }
    ModalBottomSheet(
        modifier = Modifier
            .fillMaxSize()
            .testTag("search_menu_sheet"),
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box (
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(5.dp)
        ){
            // Sheet content
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                // Search bar
                OutlinedTextField(
                    label = { Text("Search hobby/categorie...") },
                    value = searched,
                    onValueChange = { searched = it },
                    modifier = Modifier
                        .padding(5.dp)
                        .width(240.dp)
                )
                // Switch search mode button
                Button(
                    onClick = {
                        searchMode = if (searchMode == SearchMode.FILTERS) {
                            SearchMode.DISCOVER
                        } else {
                            SearchMode.FILTERS
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(searchMode.switchToIcon, contentDescription = searchMode.switchToName)
                    Text(searchMode.switchToName)
                }
            }
            // Display filters or discover according to the selected mode
            if (searchMode == SearchMode.FILTERS) {
                SearchMenuFilters()
            } else {
                SearchMenuDiscover()
            }
            // Close Search Button
            Box(
                modifier = Modifier.align(Alignment.Center).fillMaxWidth()
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.align(Alignment.Center).absoluteOffset(y = (-50).dp)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close Search")
                }
            }
        }
    }
}

