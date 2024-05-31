package com.github.swent.echo.compose.association

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.swent.echo.data.model.Association

/**
 * A screen which displays a list of associations. This Composable function takes a list of
 * associations and a function to handle row clicks. It uses a LazyColumn to efficiently display a
 * potentially large list of associations.
 */
@Composable
fun AssociationListScreen(
    associationList: List<Association>, // The list of associations to display
    onRowClicked: (Association) -> Unit = {}, // Function to handle row clicks
) {
    // LazyColumn is a vertically scrolling list that only composes and lays out the currently
    // visible items
    LazyColumn(modifier = Modifier.testTag("association_list_screen")) {
        // For each association in the list, create an AssociationListElement
        items(associationList.size) { index ->
            AssociationListElement(
                association = associationList[index], // The association to display
                onRowClicked = onRowClicked // The function to call when the row is clicked
            )
        }
    }
}

/**
 * An element in the association list. This Composable function takes an association and a function
 * to handle clicks. It uses a ListItem to display the association's name and handle click events.
 */
@Composable
fun AssociationListElement(
    association: Association, // The association to display
    onRowClicked: (Association) -> Unit, // The function to call when the row is clicked
) {
    val boxInsidePadding = 5.dp // The padding inside the box
    val tonalElevation = 5.dp // The elevation of the box

    // ListItem is a Material Design implementation of [list
    // items](https://material.io/components/lists)
    ListItem(
        headlineContent = {
            // Display the association's name
            Text(
                text = association.name, // The association's name
                textAlign = TextAlign.Center, // Center the text
                modifier =
                    Modifier.padding(boxInsidePadding) // Add padding
                        .testTag("association_name_button_${association.name}") // Add a test tag
            )
        },
        modifier =
            Modifier.clickable { onRowClicked(association) } // Make the ListItem clickable
                .testTag("association_list_${association.name}"), // Add a test tag
        tonalElevation = tonalElevation, // Set the elevation
    )
}
