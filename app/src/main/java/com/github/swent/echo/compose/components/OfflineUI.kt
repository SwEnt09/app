package com.github.swent.echo.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.swent.echo.R
import kotlinx.coroutines.delay

@Composable
fun ConnectivityStatus(isConnected: Boolean) {
    var visibility by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = visibility,
        enter = expandHorizontally(),
        exit = shrinkHorizontally()
    ) {
        ConnectivityStatusBox(isConnected = isConnected)
    }

    LaunchedEffect(isConnected) {
        visibility =
            if (!isConnected) {
                true
                // Show UI when connectivity is available
            } else {
                delay(5000) // in ms
                false
                // Show UI for No Internet Connectivity
            }
    }
}

@Composable
fun ConnectivityStatusBox(isConnected: Boolean, modifier: Modifier = Modifier) {
    val backgroundColor by animateColorAsState(if (!isConnected) Color.DarkGray else Color.Green)
    val message = if (isConnected) R.string.Online_mode else R.string.Offline_mode
    SmallTopAppBarFunc(text = stringResource(id = message), color = backgroundColor)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarFunc(text: String, color: Color) {
    Box {
        CenterAlignedTopAppBar(
            modifier = Modifier.fillMaxWidth().height(24.dp),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = color),
            title = { Text(text, fontSize = 15.sp) }
        )
    }
}
