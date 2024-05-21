package com.github.swent.echo.compose.components.searchmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.swent.echo.compose.components.TagHierarchyNavigableBar
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.viewmodels.tag.TagViewModel
import kotlin.random.Random

@Composable
fun SearchMenuDiscover(searchEntryCallback: (String) -> Unit, tagViewModel: TagViewModel) {
    // Collecting the necessary data from the viewModel
    val tags by tagViewModel.tags.collectAsState()
    val currentDepth by tagViewModel.currentDepth.collectAsState()
    val tagParents by tagViewModel.tagParents.collectAsState()
    val subTagsMap by tagViewModel.subTagsMap.collectAsState()
    // The selected tag
    var selectedTag by remember { mutableStateOf("") }
    // a random seed for consistent random subtag list
    val randomSeed by remember { mutableIntStateOf(Random.nextInt()) }

    // Function that is used when the user clicks on a parent tag
    // at the top of the discover mode
    fun onParentClicked(tag: Tag) {
        while (tag.tagId != tagParents.peek().tagId) {
            tagViewModel.goUp()
        }
        val tagParentsName =
            if (tag.tagId == tagViewModel.rootTag.tagId) {
                ""
            } else {
                tagParents.peek().name
            }
        searchEntryCallback(tagParentsName)
        // Deselect the selected tag
        selectedTag = ""
    }

    // Function that is used when the user clicks on a tag in the main part of the
    // discover mode
    fun onTagClicked(tag: Tag) {
        // Check that the tag has subtags to display
        if (!subTagsMap[tag].isNullOrEmpty()) {
            tagViewModel.goDown(tag)
            searchEntryCallback(tagParents.peek().name)
            selectedTag = ""
            // If not, select the tag
        } else {
            searchEntryCallback(tag.name)
            selectedTag = tag.name
        }
    }

    // Main component of the discover mode
    Column(modifier = Modifier.fillMaxWidth().padding(5.dp).testTag("discover_main_component")) {
        // Display the parents of the current tag in order to go back in the hierarchy
        // when they are clicked
        TagHierarchyNavigableBar(
            tagParents,
            currentDepth,
            { tag -> onParentClicked(tag) },
            {
                searchEntryCallback(tagParents.peek().name)
                selectedTag = ""
            }
        )
        Spacer(modifier = Modifier.height(5.dp))
        // Display the tags
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.testTag("discover_lazy_grid")
        ) {
            items(tags) { tag ->
                SearchMenuDiscoverItem(
                    tag,
                    { onTagClicked(tag) },
                    selectedTag,
                    subTagsMap[tag]?.map { t -> t.name }.orEmpty(),
                    randomSeed
                )
            }
        }
    }
}

// Component that displays a tag in the discover mode
@Composable
fun SearchMenuDiscoverItem(
    tag: Tag,
    onTagClicked: (Tag) -> Unit,
    selectedTag: String,
    subTags: List<String>,
    randomSeed: Int
) {
    Box(
        modifier =
            Modifier.fillMaxWidth()
                .height(90.dp)
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
            val textColor =
                if (selectedTag == tag.name) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                }
            Text(text = tag.name, color = textColor)
            if (subTags.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = subTags.shuffled(Random(randomSeed)).joinToString(),
                    color = textColor.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}
