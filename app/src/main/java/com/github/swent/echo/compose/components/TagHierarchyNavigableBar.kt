package com.github.swent.echo.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Tag
import java.util.Stack

/**
 * Hierarchy of tags displayed as a top bar and navigable in click.
 *
 * @param tagParents a stack of the parent tags of the current tag
 * @param currentDepth the current depth in the tree
 * @param onParentTagClicked a callback called when a parent tag is clicked
 * @param onCurrentTagClicked a callback called when the current tag is clicked
 * @param rootName the string to display when at the root of the tag hierarchy
 */
@Composable
fun TagHierarchyNavigableBar(
    tagParents: Stack<Tag>,
    currentDepth: Int,
    onParentTagClicked: (tag: Tag) -> Unit,
    onCurrentTagClicked: () -> Unit,
    rootName: String = stringResource(R.string.discover_select_a_category)
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(25.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // if else here to not display the root name if we are at the root, but select category
        if (currentDepth == 0) {
            Text(rootName, modifier = Modifier.testTag("discover_select_category"))
        } else {
            // Format the display of the parents of the current tag. The last tag is not
            // clickable because it is the current tag, that's why we take ...size - 1
            tagParents.subList(0, tagParents.size - 1).forEachIndexed { id, tag ->
                Text(
                    text =
                        if (id == 0) {
                            ""
                        } else {
                            " > "
                        }
                )
                Text(
                    // if else here to display "All" instead of the root tag name
                    text =
                        if (id == 0) {
                            stringResource(R.string.edit_event_screen_tag_not_selected)
                        } else {
                            tag.name
                        },
                    modifier =
                        Modifier.clickable(onClick = { onParentTagClicked(tag) })
                            .testTag("discover_parent_${tag.name}"),
                    color = ButtonDefaults.buttonColors().containerColor
                )
            }
            // Display the current tag
            Text(
                " > ${tagParents.peek().name}",
                modifier =
                    Modifier.clickable(
                            // Reset the selected tag when the current tag is clicked
                            onClick = onCurrentTagClicked
                        )
                        .testTag("discover_parent_${tagParents.peek().name}")
            )
        }
    }
}
