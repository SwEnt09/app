package com.github.swent.echo.connectivity

import kotlinx.coroutines.flow.StateFlow

interface NetworkService {
    val isOnline: StateFlow<Boolean>

    fun isOnlineNow(): Boolean
}
