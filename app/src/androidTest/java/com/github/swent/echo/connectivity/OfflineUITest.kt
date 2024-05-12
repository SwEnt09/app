package com.github.swent.echo.connectivity

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.compose.components.ConnectivityStatus
import com.github.swent.echo.compose.components.ConnectivityStatusBox
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfflineUITest {
    @get:Rule val composeTestRule = createComposeRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun connectivityStatus_initialRendering_connectedState() {
        composeTestRule.setContent { ConnectivityStatus(ConnectionState.Available) }
        composeTestRule.onNodeWithTag("ConnectivityStatusBox").assertDoesNotExist()
    }

    @Test
    fun connectivityStatus_showsConnectivityStatusBox() {
        var visibility by mutableStateOf(false)

        composeTestRule.setContent {
            AnimatedVisibility(
                visible = visibility,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                SideEffect { Log.d("ConnectivityStatus", "ConnectivityStatusBox rendered") }
                ConnectivityStatusBox(
                    isConnected = false,
                    modifier = Modifier.testTag("ConnectivityStatusBox")
                )
            }
        }

        runBlocking { composeTestRule.awaitIdle() }

        // Simulate no internet connectivity
        visibility = true
        runBlocking { composeTestRule.awaitIdle() }
        composeTestRule.onNodeWithText("Offline Mode").assertExists()

        // Simulate internet connectivity
        visibility = false
        runBlocking { composeTestRule.awaitIdle() }
        composeTestRule
            .onNodeWithTag("ConnectivityStatusBox", useUnmergedTree = true)
            .assertDoesNotExist()
    }
}
