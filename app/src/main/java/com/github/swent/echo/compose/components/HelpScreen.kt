package com.github.swent.echo.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.ui.navigation.NavigationActions

data class HelpItem(
    val title: String,
    val description: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navActions: NavigationActions) {
    val helpItems =
        listOf(
            HelpItem(
                title = stringResource(R.string.help_screen_how_can_i_find_events),
                description =
                    stringResource(R.string.help_screen_how_can_i_find_events_description),
            ),
            HelpItem(
                title = stringResource(R.string.help_screen_create_event),
                description = stringResource(R.string.help_screen_create_event_description),
            ),
            HelpItem(
                title = stringResource(R.string.help_screen_following_associations),
                description =
                    stringResource(R.string.help_screen_following_associations_description),
            ),
            HelpItem(
                title = stringResource(R.string.help_screen_list_mode),
                description = stringResource(R.string.help_screen_list_mode_description),
            ),
            HelpItem(
                title = stringResource(R.string.help_screen_my_interest_changed),
                description = stringResource(R.string.help_screen_my_interest_changed_description),
            ),
        )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = navActions::goBack, modifier = Modifier.testTag("back")) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.help_screen_navigate_back)
                        )
                    }
                },
                title = { Text(stringResource(R.string.help_screen_top_appbar_title)) },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier =
                    Modifier.padding(horizontal = 16.dp).verticalScroll(rememberScrollState())
            ) {
                Text(
                    stringResource(R.string.help_screen_title),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.help_screen_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                helpItems.map {
                    HelpCard(helpItem = it)
                    HorizontalDivider(
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HelpCard(helpItem: HelpItem) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                helpItem.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.testTag("title").clickable { expanded = !expanded },
            )
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.testTag("toggle-description")
            ) {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.help_screen_toggle_description),
                    modifier = Modifier.scale(scaleX = 1f, scaleY = if (expanded) -1f else 1f)
                )
            }
        }
        if (expanded)
            Text(
                helpItem.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp).testTag("description"),
            )
    }
}
