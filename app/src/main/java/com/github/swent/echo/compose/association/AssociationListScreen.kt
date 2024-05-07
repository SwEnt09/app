package com.github.swent.echo.compose.association

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.swent.echo.R
import com.github.swent.echo.compose.event.EventTitleAndBackButton
import com.github.swent.echo.data.model.Association

/** A screen which displays a list of associations */
@Composable
fun AssociationListScreen(
    title: String,
    onBackButtonClicked: () -> Unit,
    associationList: List<Association>,
    hasActionButton: Boolean = false,
    actionButtonName: String = "",
    onActionButtonClicked: (association: Association) -> Unit = {}
) {
    Scaffold(topBar = { EventTitleAndBackButton(title, onBackButtonClicked) }) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(associationList.size) { index ->
                AssociationListElement(
                    association = associationList[index],
                    hasActionButton = hasActionButton,
                    actionButtonName = actionButtonName,
                    onActionButtonClicked = { onActionButtonClicked(associationList[index]) }
                )
            }
        }
    }
}

const val SHORT_DESCRIPTION_LINES = 2
/** An element in the association list */
@Composable
fun AssociationListElement(
    association: Association,
    hasActionButton: Boolean,
    actionButtonName: String,
    onActionButtonClicked: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ListItem(
        modifier =
            Modifier.padding(vertical = 7.dp, horizontal = 15.dp).clickable {
                expanded = !expanded
            },
        overlineContent = { Spacer(modifier = Modifier) }, // used to align the icon and the button
        headlineContent = {
            Text(
                text = association.name,
                modifier = Modifier.testTag("${association.associationId}-name"),
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Text(
                modifier = Modifier.testTag("${association.associationId}-description"),
                text = association.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines =
                    if (expanded) {
                        Int.MAX_VALUE
                    } else {
                        SHORT_DESCRIPTION_LINES
                    },
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingContent = { Icon(painter = painterResource(R.drawable.confirmed), null) },
        tonalElevation = 5.dp,
        trailingContent = {
            if (hasActionButton) {
                OutlinedButton(
                    onClick = onActionButtonClicked,
                    modifier = Modifier.testTag("${association.associationId}-action-button")
                ) {
                    Text(
                        text = actionButtonName,
                        style = MaterialTheme.typography.labelLarge,
                        modifier =
                            Modifier.testTag("${association.associationId}-action-button-text")
                    )
                }
            }
        }
    )
}
