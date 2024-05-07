package com.github.swent.echo.compose.components.searchmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.viewmodels.tag.TagViewModel

@Composable
fun SearchMenuDiscover(searchEntryCallback: (String) -> Unit, tagViewModel: TagViewModel) {
    // Collecting the necessary data from the viewModel
    val tags = tagViewModel.tags.collectAsState()
    val currentDepth = tagViewModel.currentDepth.collectAsState()
    val tagParents = tagViewModel.tagParents.collectAsState()
    // Check if the current tag has subtags. Not optimal because some hierarchy
    // have less than maxDepth levels and it would be better to check the number
    // of subtags, but getSubTags doesn't work for now
    val hasSubTags = currentDepth.value < tagViewModel.maxDepth
    // The selected tag
    var selectedTag by remember { mutableStateOf("") }

    // Function that is used when the user clicks on a parent tag
    // at the top of the discover mode
    fun onParentClicked(tag: Tag) {
        // while(tag.tagId != tagParents.value.peek().tagId) { tagViewModel.goUp() } would
        // be more scalable but too many bugs. This solution works for 3 level hierarchy like
        // we have but need to be adapted if we add more levels.
        if (tag.tagId == tagViewModel.rootTag.tagId) {
            tagViewModel.reset()
            searchEntryCallback("")
        } else {
            tagViewModel.goUp()
            searchEntryCallback(tagParents.value.peek().name)
        }
        // Deselect the selected tag
        selectedTag = ""
    }

    // Function that is used when the user clicks on a tag in the main part of the
    // discover mode
    fun onTagClicked(tag: Tag) {
        // Check that the tag has subtags to display
        if (hasSubTags) {
            tagViewModel.goDown(tag)
            searchEntryCallback(tagParents.value.peek().name)
            selectedTag = ""
            // If not, select the tag
        } else {
            searchEntryCallback(tag.name)
            selectedTag = tag.name
        }
    }
    /*
        // Doesn't work for now
        @Composable
        fun getSubTags(tag: Tag): List<Tag> {
            var subTags by remember { mutableStateOf(listOf<Tag>()) }
            LaunchedEffect(tag) { subTags = tagViewModel.getSubTags(tag) }
            return subTags
        }
    */
    // Main component of the discover mode
    Column(modifier = Modifier.fillMaxWidth().padding(5.dp).testTag("discover_main_component")) {
        // Display the parents of the current tag in order to go back in the hierarchy
        // when they are clicked
        Row(
            modifier = Modifier.fillMaxWidth().height(25.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // if else here to not display the root name if we are at the root, but select category
            if (currentDepth.value == 0) {
                Text(
                    stringResource(R.string.discover_select_a_category),
                    modifier = Modifier.testTag("discover_select_category")
                )
            } else {
                // Format the display of the parents of the current tag. The last tag is not
                // clickable because it is the current tag, that's why we take ...size - 1
                tagParents.value.subList(0, tagParents.value.size - 1).forEachIndexed { id, tag ->
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
                                stringResource(R.string.discover_all)
                            } else {
                                tag.name
                            },
                        modifier =
                            Modifier.clickable(onClick = { onParentClicked(tag) })
                                .testTag("discover_parent_${tag.name}"),
                        color = ButtonDefaults.buttonColors().containerColor
                    )
                }
                // Display the current tag
                Text(
                    " > ${tagParents.value.peek().name}",
                    modifier =
                        Modifier.clickable(
                                // Reset the selected tag when the current tag is clicked
                                onClick = {
                                    searchEntryCallback(tagParents.value.peek().name)
                                    selectedTag = ""
                                }
                            )
                            .testTag("discover_parent_${tagParents.value.peek().name}")
                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        // Display the tags
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            modifier = Modifier.testTag("discover_lazy_grid")
        ) {
            items(tags.value) { tag ->
                SearchMenuDiscoverItem(tag, { onTagClicked(tag) }, selectedTag)
            }
        }
    }
}

// Component that displays a tag in the discover mode
@Composable
fun SearchMenuDiscoverItem(tag: Tag, onTagClicked: (Tag) -> Unit, selectedTag: String) {
    /*
    val sizeSubTags = subTags.size
    val randomSubTags = subTags.shuffled()
    val subListSubTags = randomSubTags.subList(0, min(sizeSubTags, 5))
     */
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .height(120.dp)
                .padding(3.dp)
                .clip(RoundedCornerShape(8.dp))
                // Change the background color of the tag if it is selected
                .background(
                    if (selectedTag == tag.name) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    }
                )
                .clickable(onClick = { onTagClicked(tag) })
                .testTag("discover_child_${tag.name}")
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = tag.name)
            /*
            if (sizeSubTags > 0) {
                val tripleDot = if (sizeSubTags > 5) "..." else ""
                Text(
                    text = subListSubTags.joinToString { it.name } + tripleDot,
                    color = LocalContentColor.current.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp
                )
            }
             */
        }
    }
}
