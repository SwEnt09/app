package com.github.swent.echo.compose.association

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.swent.echo.data.model.Association

/** A screen which displays a list of associations */
@Composable
fun AssociationListScreen(
    associationList: List<Association>,
    onRowClicked: (Association) -> Unit = {},
    onAssociationClicked: (Association) -> Unit = {},
    eventsFilter: List<Association>
) {
    LazyColumn(modifier = Modifier.testTag("association_list_screen")) {
        items(associationList.size) { index ->
            AssociationListElement(
                association = associationList[index],
                onRowClicked = { onRowClicked(it) },
                onAssociationClicked = { onAssociationClicked(it) },
                eventsFilter
            )
        }
    }
}

/** An element in the association list */
@Composable
fun AssociationListElement(
    association: Association,
    onRowClicked: (Association) -> Unit,
    onAssociationClicked: (Association) -> Unit,
    eventsFilter: List<Association>
) {
    val boxCornerRadius = 5.dp
    val boxInsidePadding = 5.dp
    val tonalElevation = 5.dp
    val paddingSelectedIcon = 5.dp
    ListItem(
        modifier =
            Modifier.clickable { onRowClicked(association) }
                .testTag("association_list_${association.name}"),
        overlineContent = { Spacer(modifier = Modifier) }, // used to align the icon and the button
        headlineContent = {},
        leadingContent = {
            TextButton(
                onClick = { onAssociationClicked(association) },
                modifier = Modifier.testTag("association_name_button_${association.name}")
            ) {
                Text(
                    text = association.name,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(boxInsidePadding)
                )
            }
        },
        trailingContent = {
            if (eventsFilter.contains(association)) {
                Icon(
                    Icons.Filled.Done,
                    "Selected",
                    Modifier.padding(paddingSelectedIcon)
                        .testTag("selected_icon_${association.name}")
                )
            }
        },
        tonalElevation = tonalElevation,
    )
}
