package com.github.swent.echo.compose.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/**
 * Button to open the search bottom sheet.
 *
 * @param onClick the action to perform when the button is clicked
 */
@Composable
fun SearchButton(onClick: () -> Unit) {
    FloatingActionButton(modifier = Modifier.testTag("search_button"), onClick = onClick) {
        Icon(Icons.Default.Search, contentDescription = "Search")
    }
}
