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

@Composable
fun SearchBar(title: String, searched: String, onSearchChanged: (String) -> Unit) {
    val paddingHorizontal = 10.dp
    val cornerRadius = 10.dp
    OutlinedTextField(
        label = { Text(title) },
        value = searched,
        onValueChange = onSearchChanged,
        trailingIcon = {
            if (searched.isBlank()) {
                Icon(Icons.Outlined.Search, "search")
            } else {
                IconButton(
                    onClick = { onSearchChanged("") },
                    content = { Icon(Icons.Outlined.Close, "close") }
                )
            }
        },
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = paddingHorizontal)
                .testTag("search_bar_$title"),
        shape = RoundedCornerShape(cornerRadius)
    )
}
