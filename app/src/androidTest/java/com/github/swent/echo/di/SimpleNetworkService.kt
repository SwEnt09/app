package com.github.swent.echo.di

import com.github.swent.echo.connectivity.NetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** A simple implementation of [NetworkService] that always reports the device as online. */
class SimpleNetworkService : NetworkService {
    override val isOnline = MutableStateFlow(true).asStateFlow()

    override fun isOnlineNow(): Boolean = true
}
