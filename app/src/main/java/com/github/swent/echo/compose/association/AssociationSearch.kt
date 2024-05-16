package com.github.swent.echo.compose.association

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.viewmodels.association.AssociationPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssociationSearch(goTo: (AssociationPage) -> Unit, associations: List<Association>) {
    Text("Association Search", modifier = Modifier.testTag("association_search"))
}