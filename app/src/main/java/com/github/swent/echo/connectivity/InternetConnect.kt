package com.github.swent.echo.connectivity

sealed class ConnectionState {
    data object Available : ConnectionState()

    data object Unavailable : ConnectionState()
}

/*
Getting the current connectivity status.
Checks whether the current network has capability to connect to the internet.

val Context.currentConnectivityState: ConnectionState
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return getCurrentConnectivityState(connectivityManager)
    }

private fun getCurrentConnectivityState(connectivityManager: ConnectivityManager): ConnectionState {
    val connected =
        connectivityManager.allNetworks.any { network ->
            connectivityManager
                .getNetworkCapabilities(network)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
        }

    return if (connected) ConnectionState.Available else ConnectionState.Unavailable
} */
/*
Observe the connectivity status as a flow.

@ExperimentalCoroutinesApi
fun Context.observeConnectivityAsFlow(): Flow<ConnectionState> {
    return callbackFlow {
            Log.d(TAG, "Observing connectivity...")
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val defaultState = ConnectionState.Unavailable

            if (connectivityManager == null) {
                Log.e(TAG, "Failed to obtain ConnectivityManager")
                trySend(defaultState)
            } else {
                val callback = NetworkCallback { connectionState -> trySend(connectionState) }

                val networkRequest =
                    NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build()

                connectivityManager.registerNetworkCallback(networkRequest, callback)

                // Set current state
                val currentState = getCurrentConnectivityState(connectivityManager)
                trySend(currentState)

                // Remove callback when not used
                awaitClose {
                    // Remove listeners
                    connectivityManager.unregisterNetworkCallback(callback)
                }
            }
        }
        .catch { _ ->
            // Handle any other exceptions and emit a default state
            emit(ConnectionState.Unavailable)
        }
        .flowOn(Dispatchers.IO)
}

fun NetworkCallback(callback: (ConnectionState) -> Unit): ConnectivityManager.NetworkCallback {
    return object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            callback(ConnectionState.Available)
        }

        override fun onLost(network: Network) {
            callback(ConnectionState.Unavailable)
        }
    }
}*/
/*
@ExperimentalCoroutinesApi
@Composable
fun connectivityState(): State<ConnectionState> {
    val context = LocalContext.current

    // Creates a State<ConnectionState> with current connectivity state as initial value
    return produceState(initialValue = context.currentConnectivityState) {
        // In a coroutine, can make suspend calls
        context.observeConnectivityAsFlow().collect { value = it }
    }
}
*/
