package com.github.swent.echo.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.ui.test.junit4.createComposeRule
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verifySequence
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class InternetConnectTest {
    @MockK private lateinit var mockContext: Context
    @MockK private lateinit var mockConnectivityManager: ConnectivityManager
    @MockK private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    @MockK private lateinit var mockNetwork: Network

    @get:Rule val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns
            mockConnectivityManager

        networkCallback = NetworkCallback {}
    }

    @Test
    fun getCurrentConnectivityStateReturnsAvailable() {
        val capabilities = mockk<NetworkCapabilities>()
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns
            true
        every { mockConnectivityManager.allNetworks } returns listOf(mockNetwork).toTypedArray()
        every { mockConnectivityManager.getNetworkCapabilities(mockNetwork) } returns capabilities

        val state = mockContext.currentConnectivityState

        assert(state is ConnectionState.Available)
    }

    @Test
    fun getCurrentConnectivityStateReturnsUnavailable() {
        val capabilities = mockk<NetworkCapabilities>()
        every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns
            false
        every { mockConnectivityManager.allNetworks } returns listOf(mockNetwork).toTypedArray()
        every { mockConnectivityManager.getNetworkCapabilities(mockNetwork) } returns capabilities

        val state = mockContext.currentConnectivityState

        assert(state is ConnectionState.Unavailable)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun observeConnectivityAsFlowEmitsCorrectValues() = runBlocking {
        // Arrange
        val mockContext = mockk<Context>()
        val mockConnectivityManager = mockk<ConnectivityManager>()
        val network1 = mockk<Network>()
        val network2 = mockk<Network>()
        val networkCapabilities1 = mockk<NetworkCapabilities>()
        val networkCapabilities2 = mockk<NetworkCapabilities>()
        every {
            networkCapabilities1.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns true
        every {
            networkCapabilities2.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns false

        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns
            mockConnectivityManager
        every { mockConnectivityManager.allNetworks } returns arrayOf(network1, network2)
        every { mockConnectivityManager.getNetworkCapabilities(network1) } returns
            networkCapabilities1
        every { mockConnectivityManager.getNetworkCapabilities(network2) } returns
            networkCapabilities2

        // Act
        val flow = mockContext.observeConnectivityAsFlow()
        val values = flow.toList()

        // Assert
        assertEquals(listOf(ConnectionState.Unavailable), values)
    }

    @Test
    fun networkCallbackWithCorrectValues() {
        // Arrange
        val callback = mockk<(ConnectionState) -> Unit>()
        val networkCallback = NetworkCallback(callback)
        val network = mockk<Network>()

        every { callback.invoke(ConnectionState.Available) } just runs
        every { callback.invoke(ConnectionState.Unavailable) } just runs

        // Act
        networkCallback.onAvailable(network)
        networkCallback.onLost(network)

        verifySequence {
            callback.invoke(ConnectionState.Available)
            callback.invoke(ConnectionState.Unavailable)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun connectivityStateTesting() = runBlocking {
        // Arrange
        val mockContext = mockk<Context>()
        val mockConnectivityManager = mockk<ConnectivityManager>()
        val network1 = mockk<Network>()
        val network2 = mockk<Network>()
        val networkCapabilities1 = mockk<NetworkCapabilities>()
        val networkCapabilities2 = mockk<NetworkCapabilities>()
        every {
            networkCapabilities1.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns true
        every {
            networkCapabilities2.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns false

        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns
            mockConnectivityManager
        every { mockContext.currentConnectivityState } returns ConnectionState.Unavailable
        every { mockConnectivityManager.allNetworks } returns arrayOf(network1, network2)
        every { mockConnectivityManager.getNetworkCapabilities(network1) } returns
            networkCapabilities1
        every { mockConnectivityManager.getNetworkCapabilities(network2) } returns
            networkCapabilities2

        // Act
        val flow = mockContext.observeConnectivityAsFlow()
        val values = flow.toList()

        // Assert
        assertEquals(listOf(ConnectionState.Unavailable), values)
    }
}
