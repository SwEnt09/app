package com.github.swent.echo.compose.event

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * The top bar of the event creation and edition screens: a title and a back button.
 *
 * @param title the title of the screen
 * @param onBackButtonPressed a callback called when the back button is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTitleAndBackButton(title: String, onBackButtonPressed: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.padding(5.dp),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(modifier = Modifier.testTag("Back-button"), onClick = onBackButtonPressed) {
                val icon = Icons.Filled.ArrowBack
                Icon(imageVector = icon, contentDescription = icon.name)
            }
        }
    )
}
