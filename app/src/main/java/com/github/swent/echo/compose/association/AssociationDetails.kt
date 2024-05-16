package com.github.swent.echo.compose.association

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.ListDrawer
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event

enum class AssociationDetailsTab {
    DESCRIPTION,
    EVENTS
}

@Composable
fun AssociationDetails(
    follow: (Association) -> Unit,
    association: Association,
    isFollowed: Boolean,
    events: List<Event>,
    isOnline: Boolean
) {
    var associationDetailsTab by remember { mutableStateOf(AssociationDetailsTab.DESCRIPTION) }
    val paddingValues = 5.dp
    val followWidth = 150.dp
    val followHeight = 40.dp
    val followSpaceInside = 5.dp
    val verticalSpace = 20.dp
    val paddingItems = 2.dp
    val tabHeight = 50.dp
    val weightItems = 1f
    val underlineShape = Modifier.height(1.dp).width(200.dp)
    val underlinePadding = 10.dp
    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues).testTag("association_details")
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                association.name,
                modifier = Modifier.align(Alignment.CenterStart),
                style = MaterialTheme.typography.titleLarge
            )
            Button(
                enabled = isOnline,
                onClick = { follow(association) },
                modifier =
                    Modifier.align(Alignment.CenterEnd)
                        .width(followWidth)
                        .height(followHeight)
                        .testTag("association_details_follow_button")
            ) {
                Icon(
                    if (isFollowed) Icons.Filled.Clear else Icons.Filled.Add,
                    "Follow/Unfollow association"
                )
                Spacer(modifier = Modifier.padding(followSpaceInside))
                Text(
                    if (isFollowed) stringResource(R.string.association_details_unfollow)
                    else stringResource(R.string.association_details_follow),
                    modifier = Modifier.testTag("association_details_follow_button_text")
                )
            }
        }
        Spacer(modifier = Modifier.height(verticalSpace))
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier =
                    Modifier.weight(weightItems)
                        .padding(paddingItems)
                        .height(tabHeight)
                        .clickable { associationDetailsTab = AssociationDetailsTab.DESCRIPTION }
                        .testTag("association_details_description_tab")
            ) {
                Text(
                    stringResource(R.string.association_details_description),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Box(
                modifier =
                    Modifier.weight(weightItems)
                        .padding(paddingItems)
                        .height(tabHeight)
                        .clickable { associationDetailsTab = AssociationDetailsTab.EVENTS }
                        .testTag("association_details_events_tab")
            ) {
                Text(
                    stringResource(R.string.association_details_events),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            when (associationDetailsTab) {
                AssociationDetailsTab.DESCRIPTION -> {
                    Box(
                        modifier =
                            underlineShape
                                .align(Alignment.CenterStart)
                                .padding(start = underlinePadding)
                                .background(MaterialTheme.colorScheme.primary)
                                .testTag("association_details_underline_description")
                    )
                }
                AssociationDetailsTab.EVENTS -> {
                    Box(
                        modifier =
                            underlineShape
                                .align(Alignment.CenterEnd)
                                .padding(end = underlinePadding)
                                .background(MaterialTheme.colorScheme.primary)
                                .testTag("association_details_underline_events")
                    )
                }
            }
        }
        when (associationDetailsTab) {
            AssociationDetailsTab.DESCRIPTION -> {
                Spacer(modifier = Modifier.height(verticalSpace))
                // Text(association.largeDescription)
                Text(
                    association.description,
                    modifier = Modifier.testTag("association_details_description_text")
                )
                /*
                Spacer(modifier = Modifier.height(verticalSpace))
                Text(
                    "Contacts:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(5.dp)
                )
                Text(association.url, modifier = Modifier.padding(5.dp))
                 */
            }
            AssociationDetailsTab.EVENTS -> {
                Spacer(modifier = Modifier.height(verticalSpace))
                ListDrawer(events, "", "", isOnline)
            }
        }
    }
}
