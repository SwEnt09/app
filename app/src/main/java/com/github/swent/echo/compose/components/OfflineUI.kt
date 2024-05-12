package com.github.swent.echo.compose.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import com.github.swent.echo.connectivity.NetworkService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay

@ExperimentalCoroutinesApi
@Composable
fun ConnectivityStatus(connection: NetworkService) {
    Log.d("ConnectivityStatus", "ConnectivityStatus started")
    // This will cause re-composition on every network state change
    //  val connection by connectivityState()

    val isConnected = connection.isOnlineNow()

    var visibility by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = visibility,
        enter = expandHorizontally(),
        exit = shrinkHorizontally()
    ) {
        Log.d("ConnectivityStatus", "ConnectivityStatusBox rendered")

        ConnectivityStatusBox(isConnected = isConnected)
    }

    LaunchedEffect(isConnected) {
        visibility =
            if (!isConnected) {
                true
                // Show UI for No Internet Connectivity
            } else {
                delay(5000) // in ms
                false
                // Show UI when connectivity is available for 5 seconds
            }
    }
}

@Composable
fun ConnectivityStatusBox(isConnected: Boolean, modifier: Modifier = Modifier) {
    val backgroundColor by animateColorAsState(if (!isConnected) Color.LightGray else Color.Green)

    val message = if (isConnected) R.string.Online_mode else R.string.Offline_mode
    SmallTopAppBarExample(text = stringResource(id = message), color = backgroundColor)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarExample(text: String, color: Color) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(20.dp),
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = color,
                    ),
                title = { Text(text, fontSize = 15.sp) }
            )
        }
    ) { innerPadding ->
        ScrollContent(innerPadding)
    }
}

@Composable
fun ScrollContent(innerPadding: PaddingValues) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {}
}
