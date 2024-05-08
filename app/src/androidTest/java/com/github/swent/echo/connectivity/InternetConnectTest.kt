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
import io.mockk.slot
import io.mockk.verifySequence
import junit.framework.TestCase.assertEquals
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
    // private lateinit var mockActivity: Activity
    // private lateinit var compositionContext: Composer
    // private lateinit var networkCallbackCaptor:
    // ArgumentCaptor<ConnectivityManager.NetworkCallback>/
    private val networkCallbackCaptor = slot<ConnectivityManager.NetworkCallback>()

    @get:Rule val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        // mockContext = mockk()
        // mockConnectivityManager = mockk()
        // mockNetwork = mockk()
        MockKAnnotations.init(this)
        //  mockkStatic(Context::class)
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
        assertEquals(listOf(ConnectionState.Unavailable, ConnectionState.Unavailable), values)
    }
}

/*
  @Test
  fun observeConnectivityAndUpdatesOnNetworkChanges() = runTest {
    Log.d(TAG, "Test started")
    val initialState = ConnectionState.Available
    val updatedState = ConnectionState.Unavailable
    every { mockContext.currentConnectivityState } returns initialState
    assertNotNull(initialState)

    composeTestRule.setContent {
      CompositionLocalProvider(LocalContext provides mockContext) {
        // No-op
        //   connectivityState()
      }
    }

    val latch = CountDownLatch(1)
    composeTestRule.mainClock.advanceTimeBy(2000)
    latch.await(2, TimeUnit.SECONDS)



    //networkCallbackCaptor.captured.onLost(mockNetwork)
    val flow = mockContext.observeConnectivityAsFlow()
    Log.d(TAG, "Collecting flow...")
      val states = flow.toList()
    Log.d(TAG, "Flow collected")
    Log.d(TAG, "Number of states collected: ${states.size}")
/*
    if (states.isNotEmpty()) {
      assert(states.size == 2)
      assert(states[1] == updatedState)
    } else {
      // Verify that the callback was not registered if ConnectivityManager is null
      verify(mockConnectivityManager, never()) { registerNetworkCallback(any(), any()) }
    }
 */

    assert(states.size == 2)
    verify {
      mockConnectivityManager.registerNetworkCallback(
        any<NetworkRequest>(), capture(networkCallbackCaptor))
    }
    networkCallbackCaptor.captured.onLost(mockNetwork)
    assert(states[1] == updatedState)
  }
}
/*
       val capabilities = mockk<NetworkCapabilities>()
       every { capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
       every { mockConnectivityManager.allNetworks } returns listOf(mockNetwork).toTypedArray()
       every { mockConnectivityManager.getNetworkCapabilities(mockNetwork) } returns capabilities


       var availableCallbackInvoked = false
       var unavailableCallbackInvoked = false

       val callback = NetworkCallback { state ->
           when (state) {
               ConnectionState.Available -> availableCallbackInvoked = true
               ConnectionState.Unavailable -> unavailableCallbackInvoked = true
           }
       }

       callback.onAvailable(mockk())
       assertTrue(availableCallbackInvoked)
       assertFalse(unavailableCallbackInvoked)

       callback.onLost(mockk())
       assertTrue(unavailableCallbackInvoked)


*/

// class FakeActivity : ComponentActivity()
/*
   @Test
   fun `connectivityState composable returns current connectivity state`() {
       val initialState = ConnectionState.Available
       mockkStatic(LocalContext::class)
       every { LocalContext.current } returns mockContext
       every { mockContext.currentConnectivityState } returns initialState

       val state = connectivityState()

       assert(state.value == initialState)
   }

*/
/*
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockContext: Context
    private lateinit var mockConnectivityManager: ConnectivityManager
    private lateinit var mockNetwork: Network

    @Before
    fun setup() {
        mockContext = mock(Context::class.java, object : Answer<Context> {
            override fun answer(invocation: InvocationOnMock): Context {
                return mock(Context::class.java)
            }
        })
        mockConnectivityManager = mock(ConnectivityManager::class.java)
        mockNetwork = mock(Network::class.java)

        `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
            .thenReturn(mockConnectivityManager)
    }

    @Test
    fun getCurrentConnectivityStateReturnsAvailable() {
        val networkCapabilities = mock(NetworkCapabilities::class.java)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            .thenReturn(true)
        `when`(mockConnectivityManager.getNetworkCapabilities(mockNetwork))
            .thenReturn(networkCapabilities)
        `when`(mockConnectivityManager.allNetworks).thenReturn(listOf(mockNetwork).toTypedArray())

        val state = mockContext.currentConnectivityState

        assert(state is ConnectionState.Available)
    }

    @Test
    fun getCurrentConnectivityStateReturnsUnavailable() {
        val networkCapabilities = mock(NetworkCapabilities::class.java)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            .thenReturn(false)
        `when`(mockConnectivityManager.getNetworkCapabilities(mockNetwork))
            .thenReturn(networkCapabilities)
        `when`(mockConnectivityManager.allNetworks).thenReturn(listOf(mockNetwork).toTypedArray())
        val state = mockContext.currentConnectivityState
        assert(state is ConnectionState.Unavailable)
    }

    @Test
    fun currentConnectivityStateReturnsCorrectState() {
        val availableState = ConnectionState.Available
        `when`(mockContext.currentConnectivityState).thenReturn(availableState)
        val state = mockContext.currentConnectivityState
        assert(state == availableState)
    }


    @Test
    fun observeConnectivity() = runBlocking {
        val availableState = ConnectionState.Available
        `when`(mockContext.currentConnectivityState).thenReturn(availableState)

        val flow = mockContext.observeConnectivityAsFlow()
        val states = flow.take(1).toList()

        assert(states.single() == availableState)
    }

    @Test
    fun connectivityAsFlowSendsCorrectStatesOnNetworkChanges() = runBlocking {
        val mockCallback = mock(ConnectivityManager.NetworkCallback::class.java)
        `when`(mockConnectivityManager.registerNetworkCallback(any(), eq(mockCallback))).thenReturn(Unit)

        val flow = mockContext.observeConnectivityAsFlow()
        val states = flow.take(2).toList()

        assert(states[0] is ConnectionState.Unavailable)

        val availableCaptor = ArgumentCaptor.forClass(Network::class.java)
        verify(mockCallback).onAvailable(availableCaptor.capture())
        assert(states[1] is ConnectionState.Available)

        val lostCaptor = ArgumentCaptor.forClass(Network::class.java)
        verify(mockCallback).onLost(lostCaptor.capture())
        assert(states[2] is ConnectionState.Unavailable)
    }
/*
    @Test
    fun connectivityStateReturnsCorrectInitialState() {
        val availableState = ConnectionState.Available
        `when`(mockContext.currentConnectivityState).thenReturn(availableState)

        val state = connectivityState()

        assert(state.value == availableState)
    }

 */


private lateinit var mockContext: Context
    private lateinit var mockConnectivityManager: ConnectivityManager
    private lateinit var mockNetwork: Network

    @Before
    fun setup() {
        mockContext = mock(Context::class.java)
        mockConnectivityManager = mock(ConnectivityManager::class.java)
        mockNetwork = mock(Network::class.java)

        `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
            .thenReturn(mockConnectivityManager)
    }

    @Test
    fun getCurrentConnectivityStateReturnsAvailable() {
        val networkCapabilities = mock(NetworkCapabilities::class.java)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
            .thenReturn(true)
        `when`(mockConnectivityManager.getNetworkCapabilities(mockNetwork))
            .thenReturn(networkCapabilities)
        `when`(mockConnectivityManager.allNetworks).thenReturn(listOf(mockNetwork).toTypedArray())

        val state = mockContext.currentConnectivityState

        assert(state is ConnectionState.Available)
    }
    */
 */
