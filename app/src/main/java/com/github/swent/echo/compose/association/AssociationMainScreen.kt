package com.github.swent.echo.compose.association

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.viewmodels.association.AssociationPage

@Composable
fun AssociationMainScreen(
    events: List<Event>,
    goTo: (AssociationPage) -> Unit,
    addAssociationToFilter: (Association) -> Unit,
    followedAssociations: List<Association>,
    committeeAssociations: List<Association>,
    eventsFilter: List<Association>
) {
    // Will be supressed when true page is implemented
    Row(modifier = Modifier.fillMaxWidth().testTag("association_main_screen")) {
        Button(
            onClick = { goTo(AssociationPage.DETAILS) },
            modifier = Modifier.testTag("details_button")
        ) {
            Text("Details")
        }
    }
}
