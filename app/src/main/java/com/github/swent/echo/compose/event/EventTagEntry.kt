package com.github.swent.echo.compose.event

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Tag

/** this composable contains the Tags title, text field and list of tags */
@Composable
fun EventTagEntry(
    tags: Set<Tag>,
    tagText: String,
    onTagFieldChanged: (tagText: String) -> Unit,
    onTagPressed: (tag: Tag) -> Unit
) {
    EventTextEntry(name = stringResource(R.string.edit_event_screen_tags), value = tagText) {
        onTagFieldChanged(it)
    }
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        tags.forEach {
            TextButton(onClick = { onTagPressed(it) }) {
                Text(it.name)
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription =
                        stringResource(R.string.edit_event_screen_delete_tag_button) + it.name
                )
            }
        }
    }
}
