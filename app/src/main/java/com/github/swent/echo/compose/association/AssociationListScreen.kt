package com.github.swent.echo.compose.association

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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
    actionSnackbarMessage: String = "",
    actionSnackbarUndoMessage: String = "",
    onActionButtonClicked: (association: Association) -> Unit = {},
    onUndoActionButtonClicked: (association: Association) -> Unit = {},
    onAssociationClicked: (association: Association) -> Unit = {}
) {
    val snackBarHostState by remember { mutableStateOf(SnackbarHostState()) }
    var lastActionAssociation by remember { mutableStateOf(Association.EMPTY) }
    if (lastActionAssociation != Association.EMPTY) {
        LaunchedEffect(lastActionAssociation) {
            val result =
                snackBarHostState.showSnackbar(
                    actionSnackbarMessage + lastActionAssociation.name,
                    actionSnackbarUndoMessage,
                    true,
                    SnackbarDuration.Long
                )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    onUndoActionButtonClicked(lastActionAssociation)
                }
                SnackbarResult.Dismissed -> {
                    // do nothing
                }
            }
        }
    }
    Scaffold(
        topBar = { EventTitleAndBackButton(title, onBackButtonClicked) },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState, modifier = Modifier.testTag("snackbar"))
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(associationList.size) { index ->
                AssociationListElement(
                    association = associationList[index],
                    hasActionButton = hasActionButton,
                    actionButtonName = actionButtonName,
                    onActionButtonClicked = {
                        onActionButtonClicked(associationList[index])
                        lastActionAssociation = associationList[index]
                    },
                    onAssociationClicked = { onAssociationClicked(associationList[index]) }
                )
            }
        }
    }
}

/** An element in the association list */
@Composable
fun AssociationListElement(
    association: Association,
    hasActionButton: Boolean,
    actionButtonName: String,
    onActionButtonClicked: () -> Unit,
    onAssociationClicked: () -> Unit
) {
    ListItem(
        modifier =
            Modifier.padding(vertical = 7.dp, horizontal = 15.dp)
                .clip(RoundedCornerShape(5.dp))
                .clickable { onAssociationClicked() },
        overlineContent = { Spacer(modifier = Modifier) }, // used to align the icon and the button
        headlineContent = {
            Text(
                text = association.name,
                modifier = Modifier.testTag("${association.associationId}-name"),
                style = MaterialTheme.typography.titleMedium
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
