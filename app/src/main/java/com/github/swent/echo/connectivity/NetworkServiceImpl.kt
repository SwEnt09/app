package com.github.swent.echo.connectivity

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkServiceImpl(connectivityManager: ConnectivityManager) : NetworkService {
    private val _isOnline = MutableStateFlow(false)

    init {
        val caps = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        _isOnline.value = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false

        val request =
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

        val networkCallback =
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _isOnline.value = true
                }

                override fun onLost(network: Network) {
                    _isOnline.value = false
                }
            }

        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    override val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    override fun isOnlineNow(): Boolean = _isOnline.value
}
