package com.github.swent.echo.compose.association

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.ListDrawer
import com.github.swent.echo.compose.components.Pager
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event

@Composable
fun AssociationDetails(
    association: Association,
    isFollowed: Boolean,
    follow: (Association) -> Unit,
    events: List<Event>,
    isOnline: Boolean,
    refreshEvents: () -> Unit
) {
    val paddingValues = 10.dp
    val phoneVerticalCenter = (LocalConfiguration.current.screenWidthDp / 2).dp
    val followWidth = 150.dp
    val followHeight = 40.dp
    val followSpaceInside = 5.dp
    val verticalSpace = 20.dp
    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues).testTag("association_details")
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                association.name,
                modifier = Modifier.align(Alignment.CenterStart).widthIn(max = phoneVerticalCenter),
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
        Pager(
            listOf(
                Pair(stringResource(R.string.association_details_description)) {
                    AssociationDescription(association)
                },
                Pair(stringResource(R.string.association_details_events)) {
                    AssociationEvents(events, isOnline, refreshEvents)
                }
            )
        )
    }
}

@Composable
fun AssociationDescription(association: Association) {
    Text(association.description)
}

@Composable
fun AssociationEvents(events: List<Event>, isOnline: Boolean, refreshEvents: () -> Unit) {
    ListDrawer(events, isOnline, refreshEvents)
}
