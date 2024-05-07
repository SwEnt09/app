package com.github.swent.echo.connectivity

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule

class OfflineUI {
    @get:Rule val composeTestRule = createComposeRule()
    /*
     @Test
     fun connectivityStatus_showsConnectivityStatusBox() {
       composeTestRule.setContent { ConnectivityStatus() }
       composeTestRule.awaitIdle()
       composeTestRule.onNodeWithTag("ConnectivityStatusBox").assertExists()
     }

     @Test
     fun connectivityStatusBox_showsCorrectText() {
       val isConnected = true
       val expectedBackgroundColor = Color.Green
       val expectedMessage = composeTestRule.activity.getString(R.string.Online_mode)

       composeTestRule.setContent { ConnectivityStatusBox(isConnected = isConnected) }

       composeTestRule.awaitIdle()
       composeTestRule.onNodeWithTag("ConnectivityStatusBox").assertTextEquals(expectedMessage)

       val isNotConnected = false
       val expectedNotConnectedBackgroundColor = Color.LightGray
       val expectedNotConnectedMessage = composeTestRule.activity.getString(R.string.Offline_mode)

       composeTestRule.setContent { ConnectivityStatusBox(isConnected = isNotConnected) }

       composeTestRule.awaitIdle()
       composeTestRule
           .onNodeWithTag("ConnectivityStatusBox")
           // .assertBackgroundColor(expectedNotConnectedBackgroundColor)
           .assertTextEquals(expectedNotConnectedMessage)
     }


    */
}
