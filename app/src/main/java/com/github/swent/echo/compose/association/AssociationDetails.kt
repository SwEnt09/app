package com.github.swent.echo.compose.association

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.Hypertext
import com.github.swent.echo.compose.components.ListDrawer
import com.github.swent.echo.compose.components.Pager
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Tag

// This Composable function displays the details of an association.
@Composable
fun AssociationDetails(
    association: Association, // The association to display
    isFollowed: Boolean, // Whether the association is followed by the user
    follow: (Association) -> Unit, // Function to follow/unfollow the association
    events: List<Event>, // List of events related to the association
    isOnline: Boolean, // Whether the user is online
    refreshEvents: () -> Unit, // Function to refresh the list of events
    onTagPressed: (Tag) -> Unit = {}, // Function to handle tag press events
    userId: String? = null,
    modify: (Event) -> Unit = {}
) {
    // Define layout parameters
    val paddingValues = 10.dp
    val phoneHorizontalCenter = (LocalConfiguration.current.screenWidthDp / 2).dp
    val followWidth = 150.dp
    val followHeight = 40.dp
    val followSpaceInside = 5.dp
    val verticalSpace = 12.dp

    // Start of the layout
    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues).testTag("association_details")
    ) {
        // Association name and follow/unfollow button
        Box(modifier = Modifier.fillMaxWidth()) {
            // Association name
            Text(
                association.name,
                modifier =
                    Modifier.align(Alignment.CenterStart).widthIn(max = phoneHorizontalCenter),
                style = MaterialTheme.typography.titleLarge
            )
            // Follow/unfollow button
            Button(
                enabled = isOnline,
                onClick = { follow(association) },
                modifier =
                    Modifier.align(Alignment.CenterEnd)
                        .width(followWidth)
                        .height(followHeight)
                        .testTag("association_details_follow_button")
            ) {
                // Icon for follow/unfollow
                Icon(
                    if (isFollowed) Icons.Filled.Clear else Icons.Filled.Add,
                    "Follow/Unfollow association"
                )
                Spacer(modifier = Modifier.padding(followSpaceInside))
                // Text for follow/unfollow
                Text(
                    if (isFollowed) stringResource(R.string.association_details_unfollow)
                    else stringResource(R.string.association_details_follow),
                    modifier = Modifier.testTag("association_details_follow_button_text")
                )
            }
        }
        Spacer(modifier = Modifier.height(verticalSpace))
        // Pager for association description and events
        Pager(
            listOf(
                Pair(stringResource(R.string.association_details_description)) {
                    AssociationDescription(association, verticalSpace, onTagPressed)
                },
                Pair(stringResource(R.string.association_details_events)) {
                    ListDrawer(events, isOnline, refreshEvents, userId = userId, modify = modify)
                }
            )
        )
    }
}

// This Composable function displays the description of an association.
@Composable
fun AssociationDescription(
    association: Association, // The association to display
    verticalSpace: Dp, // The vertical space to use in the layout
    onTagPressed: (Tag) -> Unit // Function to handle tag press events
) {
    // Convert the set of related tags to a list
    val tags = association.relatedTags.toList()
    val spaceBetweenTagChips = 6.dp

    // Start of the layout
    Column {
        // Display the related tags in a horizontal scrollable row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spaceBetweenTagChips)
        ) {
            items(tags) { tag ->
                // Each tag is displayed as a chip
                AssistChip(
                    onClick = { onTagPressed(tag) },
                    label = { Text(tag.name) },
                    border =
                        AssistChipDefaults.assistChipBorder(
                            enabled = true,
                            borderColor = MaterialTheme.colorScheme.primary,
                            borderWidth = 1.dp,
                        ),
                    colors =
                        AssistChipDefaults.assistChipColors(
                            labelColor = MaterialTheme.colorScheme.primary,
                        ),
                )
            }
        }
        // Display the association description
        Text(association.description)
        // If the association has a URL, display it as a hyperlink
        if (association.url != null) {
            Spacer(modifier = Modifier.height(verticalSpace))
            Text(
                stringResource(R.string.association_details_contact),
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(verticalSpace))
            Hypertext(association.url)
        }
    }
}
