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

/** the root tag of the section and the semester respectively */
const val SECTION_ROOT_TAG_ID = "30f27641-bd63-42e7-9d95-6117ad997554"
const val SEMESTER_ROOT_TAG_ID = "319715cd-6210-4e62-a061-c533095bd098"

/**
 * This composable contains the Tags title, the list of tags and buttons to choose tags.
 *
 * @param tags the list of tags of the event
 * @param enabled true if the buttons are enabled
 * @param onTagDeleted a callback called when a tag is added to the event
 * @param onTagSelected a callback called when a tag is deleted from the event
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EventTagEntry(
    tags: Set<Tag>,
    enabled: Boolean,
    onTagSelected: (tag: Tag) -> Unit,
    onTagDeleted: (tag: Tag) -> Unit
) {
    val tagButtonsStrings =
        listOf(
            Pair(stringResource(R.string.edit_event_screen_select_category), ""),
            Pair(stringResource(R.string.edit_event_screen_select_section), SECTION_ROOT_TAG_ID),
            Pair(stringResource(R.string.edit_event_screen_select_semester), SEMESTER_ROOT_TAG_ID)
        )
    Column(modifier = Modifier.fillMaxWidth().padding(EVENT_PADDING_BETWEEN_INPUTS)) {
        EventEntryName(stringResource(R.string.edit_event_screen_tags))
        Row {
            for (buttonString in tagButtonsStrings) {
                AddTagButton(
                    type = buttonString.first,
                    rootTagId = buttonString.second,
                    enabled = enabled,
                    onTagSelected = onTagSelected
                )
            }
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

/**
 * A button to add a tag.
 *
 * @param type the type of tag displayed on the button
 * @param rootTagId the id of the root tag, the category root tag is used if empty
 * @param enabled true if the button in enabled
 * @param onTagSelected a callback called when a tag is selected
 */
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
            tagType = stringResource(R.string.tag_navigation_bar_before_type) + type + " : ",
            onTagSelected = {
                onTagSelected(it)
                dialogVisible = false
            }
        )
    }
}
