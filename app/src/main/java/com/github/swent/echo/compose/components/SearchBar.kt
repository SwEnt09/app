package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

// This Composable function creates a search bar.
@Composable
fun SearchBar(title: String, searched: String, onSearchChanged: (String) -> Unit) {
    // Define some constants for padding and corner radius.
    val paddingHorizontal = 10.dp
    val cornerRadius = 10.dp
    // Create an outlined text field for the search bar.
    OutlinedTextField(
        // The label of the text field is the title of the search bar.
        label = { Text(title) },
        // The value of the text field is the current search query.
        value = searched,
        // When the value changes, call the onSearchChanged function.
        onValueChange = onSearchChanged,
        // The trailing icon depends on whether the search query is empty.
        trailingIcon = {
            if (searched.isBlank()) {
                // If the search query is empty, show a search icon.
                Icon(Icons.Outlined.Search, "search")
            } else {
                // If the search query is not empty, show a close icon that clears the search query
                // when clicked.
                IconButton(
                    onClick = { onSearchChanged("") },
                    content = { Icon(Icons.Outlined.Close, "close") }
                )
            }
        },
        // Set the width and padding of the text field and a test tag for testing purposes.
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = paddingHorizontal)
                .testTag("search_bar_$title"),
        // Set the shape of the text field to be a rounded rectangle.
        shape = RoundedCornerShape(cornerRadius)
    )
}
