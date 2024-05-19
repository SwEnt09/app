package com.github.swent.echo.compose.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.viewmodels.tag.TagViewModel

/** The tag selection dialog */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagSelectionDialog(
    onDismissRequest: () -> Unit,
    dialogProperties: DialogProperties = DialogProperties(),
    tagViewModel: TagViewModel,
    tagType: String = stringResource(R.string.edit_event_screen_select_category),
    onTagSelected: (Tag) -> Unit
) {
    val tags by tagViewModel.tags.collectAsState()
    val subTagsMap by tagViewModel.subTagsMap.collectAsState()
    val tagParents by tagViewModel.tagParents.collectAsState()
    val currentDepth by tagViewModel.currentDepth.collectAsState()
    Dialog(onDismissRequest = onDismissRequest, properties = dialogProperties) {
        Card(
            modifier =
                Modifier.width(300.dp)
                    .height(500.dp)
                    .padding(vertical = 30.dp)
                    .testTag("tag-dialog")
        ) {
            LazyColumn {
                stickyHeader {
                    Card(modifier = Modifier.padding(10.dp)) {
                        TagHierarchyNavigableBar(
                            tagParents = tagParents,
                            currentDepth = currentDepth,
                            onParentTagClicked = { tag ->
                                while (tag.tagId != tagParents.peek().tagId) {
                                    tagViewModel.goUp()
                                }
                            },
                            onCurrentTagClicked = {},
                            rootName = tagType
                        )
                    }
                }
                items(tags) { tag ->
                    TagSelectionDialogEntry(
                        tag = tag,
                        onTagClicked = {
                            onTagSelected(it)
                            onDismissRequest()
                        },
                        onTagArrowClicked = tagViewModel::goDown,
                        hasSubTags = subTagsMap[tag]?.isNotEmpty() ?: false
                    )
                }
            }
        }
    }
}

/** A line for a tag in the dialog */
@Composable
fun TagSelectionDialogEntry(
    tag: Tag,
    onTagClicked: (Tag) -> Unit,
    onTagArrowClicked: (Tag) -> Unit,
    hasSubTags: Boolean
) {
    Row(
        modifier = Modifier.padding(5.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = { onTagClicked(tag) },
            modifier = Modifier.testTag("${tag.name}-select-button")
        ) {
            Text(tag.name)
        }
        if (hasSubTags) {
            IconButton(
                modifier = Modifier.testTag("${tag.name}-subtag-button"),
                onClick = { onTagArrowClicked(tag) }
            ) {
                val icon = Icons.Filled.KeyboardArrowRight
                Icon(imageVector = icon, contentDescription = icon.name)
            }
        }
    }
}
