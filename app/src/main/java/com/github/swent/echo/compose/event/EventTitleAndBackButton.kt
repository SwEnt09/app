package com.github.swent.echo.compose.event

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/** the top of the screen: a title and a back button */
@Composable
fun EventTitleAndBackButton(modifier: Modifier, title: String, onBackButtonPressed: () -> Unit) {
    Row {
        IconButton(
            modifier = modifier.testTag("Back-button"),
            onClick = { onBackButtonPressed() }
        ) {
            val icon = Icons.Filled.ArrowBack
            Icon(imageVector = icon, contentDescription = icon.name)
        }
        Text(
            text = title,
            modifier = modifier.padding(5.dp).align(Alignment.CenterVertically),
            style = MaterialTheme.typography.titleLarge
        )
    }
}
