package com.github.swent.echo.compose.event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.TagSelectionDialog
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.viewmodels.tag.TagViewModel

const val SECTION_ROOT_TAG_ID = "30f27641-bd63-42e7-9d95-6117ad997554"
const val SEMESTER_ROOT_TAG_ID = "319715cd-6210-4e62-a061-c533095bd098"

/** this composable contains the Tags title, text field and list of tags */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EventTagEntry(
    tags: Set<Tag>,
    enabled: Boolean,
    onTagSelected: (tag: Tag) -> Unit,
    onTagDeleted: (tag: Tag) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(EVENT_PADDING_BETWEEN_INPUTS)) {
        EventEntryName(stringResource(R.string.edit_event_screen_tags))
        Row {
            AddTagButton(
                type = stringResource(R.string.edit_event_screen_select_category),
                rootTagId = "",
                enabled = enabled,
                onTagSelected = onTagSelected
            )
            AddTagButton(
                type = stringResource(R.string.edit_event_screen_select_section),
                rootTagId = SECTION_ROOT_TAG_ID,
                enabled = enabled,
                onTagSelected = onTagSelected
            )
            AddTagButton(
                type = stringResource(R.string.edit_event_screen_select_semester),
                rootTagId = SEMESTER_ROOT_TAG_ID,
                enabled = enabled,
                onTagSelected = onTagSelected
            )
        }
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
        }
    }
}

@Composable
fun AddTagButton(
    type: String,
    rootTagId: String = "",
    enabled: Boolean,
    onTagSelected: (tag: Tag) -> Unit
) {
    var dialogVisible by remember { mutableStateOf(false) }
    val tagViewModel: TagViewModel =
        hiltViewModel<TagViewModel, TagViewModel.TagViewModelFactory>(key = rootTagId) { factory ->
            factory.create(rootTagId)
        }
    OutlinedButton(
        enabled = enabled,
        modifier = Modifier.testTag("add-tag-button-$type"),
        onClick = { dialogVisible = true }
    ) {
        val icon = Icons.Filled.Add
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription =
                    stringResource(R.string.edit_event_screen_add_tag_button) + type
            )
            Text(type, style = MaterialTheme.typography.labelSmall)
        }
    }
    if (dialogVisible) {
        TagSelectionDialog(
            onDismissRequest = { dialogVisible = false },
            tagViewModel = tagViewModel,
            onTagSelected = {
                onTagSelected(it)
                dialogVisible = false
            }
        )
    }
}
