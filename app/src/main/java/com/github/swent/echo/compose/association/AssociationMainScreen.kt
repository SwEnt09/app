package com.github.swent.echo.compose.association

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.swent.echo.compose.components.ListDrawer
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
    eventsFilter: List<Association>,
    isOnline: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AssociationExpandableList(
            "Followed Associations",
            goTo,
            addAssociationToFilter,
            followedAssociations,
            eventsFilter
        )
        AssociationExpandableList(
            "My Associations",
            goTo,
            addAssociationToFilter,
            committeeAssociations,
            eventsFilter
        )
        ListDrawer(events, "", "", isOnline)
    }
}

@Composable
fun AssociationExpandableList(
    title: String,
    goTo: (AssociationPage) -> Unit,
    addAssociationToFilter: (Association) -> Unit,
    associationList: List<Association>,
    eventsFilter: List<Association>
) {
    var expanded by remember { mutableStateOf(false) }
    val paddingValues = 5.dp
    val cornerRadius = 5.dp
    val topBoxHeight = 80.dp
    val paddingTextStart = 10.dp
    val paddingIconEnd = 10.dp
    Column(
        modifier =
            Modifier.padding(paddingValues)
                .clip(RoundedCornerShape(cornerRadius))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(
            modifier =
                Modifier.height(topBoxHeight).fillMaxWidth().clickable { expanded = !expanded }
        ) {
            Text(
                title,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = paddingTextStart)
            )
            Icon(
                if (!expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                contentDescription = "Expand",
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = paddingIconEnd)
            )
        }
        if (expanded) {
            AssociationListScreen(
                associationList,
                { addAssociationToFilter(it) },
                {
                    val nextPage = AssociationPage.DETAILS
                    nextPage.association = it
                    goTo(nextPage)
                },
                eventsFilter
            )
        }
    }
}
