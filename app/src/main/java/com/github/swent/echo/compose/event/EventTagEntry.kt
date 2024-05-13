package com.github.swent.echo.compose.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.TagSelectionDialog
import com.github.swent.echo.data.model.Tag

/** this composable contains the Tags title, text field and list of tags */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EventTagEntry(
    tags: Set<Tag>,
    enabled: Boolean,
    onTagSelected: (tag: Tag) -> Unit,
    onTagDeleted: (tag: Tag) -> Unit
) {
    var dialogVisible by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(EVENT_PADDING_BETWEEN_INPUTS)) {
        EventEntryName(stringResource(R.string.edit_event_screen_tags))
        FlowRow {
            tags.forEach { tag ->
                InputChip(
                    selected = true,
                    onClick = { onTagDeleted(tag) },
                    label = { Text(tag.name) },
                    modifier = Modifier.padding(3.dp).testTag("${tag.name}-tag-button"),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription =
                                stringResource(R.string.edit_event_screen_delete_tag_button) +
                                    tag.name
                        )
                    }
                )
            }
            OutlinedIconButton(
                enabled = enabled,
                modifier = Modifier.testTag("add-tag-button"),
                onClick = { dialogVisible = true }
            ) {
                val icon = Icons.Filled.Add
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(R.string.edit_event_screen_add_tag_button)
                )
            }
        }
        if (dialogVisible) {
            TagSelectionDialog(
                onDismissRequest = { dialogVisible = false },
                tagViewModel = hiltViewModel(),
                onTagSelected = {
                    onTagSelected(it)
                    dialogVisible = false
                }
            )
        }
    }
}
