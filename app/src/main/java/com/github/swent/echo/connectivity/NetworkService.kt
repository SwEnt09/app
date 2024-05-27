package com.github.swent.echo.connectivity

import kotlinx.coroutines.flow.StateFlow

/** Service to check if the device is connected to the internet. */
interface NetworkService {
    /**
     * A [StateFlow] that emits `true` when the device is connected to the internet and `false`
     * otherwise. Should be used in the UI to display a message to the user when they are offline.
     */
    val isOnline: StateFlow<Boolean>

    /**
     * Checks if the device is connected to the internet.
     *
     * @return `true` if the device is connected to the internet and `false` otherwise.
     */
    fun isOnlineNow(): Boolean
}
