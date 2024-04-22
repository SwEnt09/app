package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.viewmodels.tag.TagViewModel

/** The tag selection dialog */
@Composable
fun TagSelectionDialog(
    onDismissRequest: () -> Unit,
    dialogProperties: DialogProperties = DialogProperties(),
    tagViewModel: TagViewModel,
    onTagSelected: (Tag) -> Unit
) {
    val tags = tagViewModel.tags.collectAsState()
    val currentDepth = tagViewModel.currentDepth.collectAsState()
    val hasSubTags = currentDepth.value < tagViewModel.maxDepth
    Dialog(onDismissRequest = onDismissRequest, properties = dialogProperties) {
        Card(modifier = Modifier.padding(vertical = 30.dp).testTag("tag-dialog")) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row {
                    IconButton(
                        onClick = tagViewModel::goUp,
                        modifier = Modifier.testTag("tag-back-button")
                    ) {
                        val icon = Icons.Filled.KeyboardArrowLeft
                        Icon(imageVector = icon, contentDescription = icon.name)
                    }
                }
                Spacer(Modifier.padding(5.dp))
                for (tag in tags.value) {
                    TagSelectionDialogEntry(
                        tag = tag,
                        onTagClicked = {
                            onTagSelected(it)
                            onDismissRequest()
                        },
                        onTagArrowClicked = tagViewModel::goDown,
                        hasSubTags = hasSubTags
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
