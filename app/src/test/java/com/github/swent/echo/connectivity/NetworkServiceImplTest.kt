package com.github.swent.echo.connectivity

import android.net.ConnectivityManager
import android.net.NetworkRequest
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NetworkServiceImplTest {
    private lateinit var networkService: NetworkServiceImpl
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    fun setUp(hasInternetCapability: Boolean) {
        connectivityManager = mockk()

        every { connectivityManager.activeNetwork } returns mockk()
        every { connectivityManager.getNetworkCapabilities(any()) } returns
            mockk { every { hasCapability(any()) } returns hasInternetCapability }

        every {
            connectivityManager.registerNetworkCallback(
                any<NetworkRequest>(),
                any<ConnectivityManager.NetworkCallback>()
            )
        } answers { networkCallback = secondArg() }
    }

    @Test
    fun `should not be online when no network`() {
        setUp(hasInternetCapability = false)

        networkService = NetworkServiceImpl(connectivityManager)
        assertFalse(networkService.isOnlineNow())
    }

    @Test
    fun `should be online when network is available`() {
        setUp(hasInternetCapability = true)

        networkService = NetworkServiceImpl(connectivityManager)
        assertTrue(networkService.isOnlineNow())
    }

    @Test
    fun `should be online when onAvailable is called`() {
        setUp(hasInternetCapability = false)

        networkService = NetworkServiceImpl(connectivityManager)
        assertFalse(networkService.isOnlineNow())

        networkCallback.onAvailable(mockk())
        assertTrue(networkService.isOnlineNow())
    }

    @Test
    fun `should be offline when onLost is called`() {
        setUp(hasInternetCapability = true)

        networkService = NetworkServiceImpl(connectivityManager)
        assertTrue(networkService.isOnlineNow())

        networkCallback.onLost(mockk())
        assertFalse(networkService.isOnlineNow())
    }
}
