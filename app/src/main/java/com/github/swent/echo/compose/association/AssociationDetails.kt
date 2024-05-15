package com.github.swent.echo.compose.association

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event

@Composable
fun AssociationDetails(
    follow: (Association) -> Unit,
    association: Association,
    isFollowed: Boolean,
    events: List<Event>
) {
    Text("Association Details", modifier = Modifier.testTag("association_details"))
}
