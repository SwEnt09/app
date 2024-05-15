package com.github.swent.echo.compose.association

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssociationSearchBottomSheet(
    onFullyExtended: () -> Unit,
    onDismiss: () -> Unit,
    searchEntryCallback: (String) -> Unit,
    searched: String
) {}
