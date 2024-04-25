package com.github.swent.echo.compose.event

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.TagSelectionDialog
import com.github.swent.echo.data.model.Tag

/** this composable contains the Tags title, text field and list of tags */
@Composable
fun EventTagEntry(
    tags: Set<Tag>,
    onTagSelected: (tag: Tag) -> Unit,
    onTagDeleted: (tag: Tag) -> Unit
) {
    var dialogVisible by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(EVENT_PADDING_BETWEEN_INPUTS)) {
        EventEntryName(stringResource(R.string.edit_event_screen_tags))
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            tags.forEach { tag ->
                TextButton(
                    onClick = { onTagDeleted(tag) },
                    modifier = Modifier.testTag("${tag.name}-tag-button")
                ) {
                    Text(tag.name)
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription =
                            stringResource(R.string.edit_event_screen_delete_tag_button) + tag.name
                    )
                }
            }
            OutlinedIconButton(
                modifier = Modifier.testTag("add-tag-button"),
                onClick = { dialogVisible = true }
            ) {
                val icon = Icons.Filled.Add
                Icon(imageVector = icon, contentDescription = icon.name)
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
