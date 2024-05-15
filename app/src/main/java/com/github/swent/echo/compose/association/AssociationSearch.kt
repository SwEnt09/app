package com.github.swent.echo.compose.association

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.github.swent.echo.data.model.Association

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssociationSearch(goTo: (AssociationPage) -> Unit, associations: List<Association>) {}
