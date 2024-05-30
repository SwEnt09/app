@file:JvmName("MainScreenTagsFilterKt")

package com.github.swent.echo.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.swent.echo.ExcludeFromJacocoGeneratedReport
import com.github.swent.echo.data.model.Tag

/*
 * TagUI is a composable that displays a tag.
 * tags: List of tags to display, fetch the most recent or the most common tags from the repository.
 */

@Composable
fun TagUI(
    tags: List<Tag>,
    selectedTagIds: List<String>,
    leftPadding: Dp = 0.dp,
    onTagClick: (Tag) -> Unit
) {
    val lazyListState = rememberLazyListState()

    Column(modifier = Modifier) {
        LazyRow(
            modifier = Modifier.padding(leftPadding),
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tags) { tag ->
                TagItem(
                    tag = tag,
                    isSelected = selectedTagIds.contains(tag.tagId),
                    onClick = { onTagClick(tag) }
                )
            }
        }
    }
}

/*
 * TagItem is a composable that highlights the selected tag.
 */
@Composable
fun TagItem(tag: Tag, isSelected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val backgroundColor = if (isSelected) colors.primary else colors.inverseOnSurface
    val contentColor = if (isSelected) colors.onPrimary else colors.onSurface

    FilledTonalButton(
        onClick = onClick,
        contentPadding = PaddingValues(5.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = contentColor,
            )
    ) {
        Text(
            text = tag.name,
            color = contentColor,
            fontSize = 16.sp,
        )
    }
}
// excluding this preview function
@ExcludeFromJacocoGeneratedReport
@Preview
@Composable
fun PreviewTagUI() {
    val tags =
        listOf(
            Tag("SP", "Sports"),
            Tag("MU", "Music"),
            Tag("SO", "Social"),
            Tag("AR", "Arts"),
            Tag("GA", "Games"),
            Tag("PO", "Politics"),
            Tag("TE", "Tech")
        )
    MaterialTheme { TagUI(tags = tags, emptyList()) {} }
}
