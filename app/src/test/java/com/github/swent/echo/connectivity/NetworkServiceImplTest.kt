package com.github.swent.echo.connectivity

import android.net.ConnectivityManager
import android.net.NetworkRequest
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NetworkServiceImplTest {
    private lateinit var networkService: NetworkServiceImpl
    private lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setUp() {
        connectivityManager = mockk()
    }

    @Test
    fun `should not be online when no network`() {
        every { connectivityManager.activeNetwork } returns mockk()
        every { connectivityManager.getNetworkCapabilities(any()) } returns
            mockk { every { hasCapability(any()) } returns false }

        every {
            connectivityManager.registerNetworkCallback(
                any<NetworkRequest>(),
                any<ConnectivityManager.NetworkCallback>()
            )
        } returns Unit

        networkService = NetworkServiceImpl(connectivityManager)
        assertFalse(networkService.isOnlineNow())
    }

    @Test
    fun `should be online when network is available`() {
        every { connectivityManager.activeNetwork } returns mockk()
        every { connectivityManager.getNetworkCapabilities(any()) } returns
            mockk { every { hasCapability(any()) } returns true }

        every {
            connectivityManager.registerNetworkCallback(
                any<NetworkRequest>(),
                any<ConnectivityManager.NetworkCallback>()
            )
        } returns Unit

        networkService = NetworkServiceImpl(connectivityManager)
        assertTrue(networkService.isOnlineNow())
    }

    @Test
    fun `should be online when onAvailable is called`() {
        every { connectivityManager.activeNetwork } returns mockk()
        every { connectivityManager.getNetworkCapabilities(any()) } returns
            mockk { every { hasCapability(any()) } returns false }

        lateinit var networkCallback: ConnectivityManager.NetworkCallback
        every {
            connectivityManager.registerNetworkCallback(
                any<NetworkRequest>(),
                any<ConnectivityManager.NetworkCallback>()
            )
        } answers { networkCallback = secondArg() }

        networkService = NetworkServiceImpl(connectivityManager)
        assertFalse(networkService.isOnlineNow())

        networkCallback.onAvailable(mockk())
        assertTrue(networkService.isOnlineNow())
    }

    @Test
    fun `should be offline when onLost is called`() {
        every { connectivityManager.activeNetwork } returns mockk()
        every { connectivityManager.getNetworkCapabilities(any()) } returns
            mockk { every { hasCapability(any()) } returns true }

        lateinit var networkCallback: ConnectivityManager.NetworkCallback
        every {
            connectivityManager.registerNetworkCallback(
                any<NetworkRequest>(),
                any<ConnectivityManager.NetworkCallback>()
            )
        } answers { networkCallback = secondArg() }

        networkService = NetworkServiceImpl(connectivityManager)
        assertTrue(networkService.isOnlineNow())

        networkCallback.onLost(mockk())
        assertFalse(networkService.isOnlineNow())
    }
}
