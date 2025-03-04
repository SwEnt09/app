package com.github.swent.echo

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.compose.components.ConnectivityStatus
import com.github.swent.echo.compose.navigation.AppNavigationHost
import com.github.swent.echo.connectivity.GPSService
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.ui.theme.EchoTheme
import com.github.swent.echo.viewmodels.AppTheme
import com.github.swent.echo.viewmodels.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var authenticationService: AuthenticationService
    @Inject lateinit var repository: Repository
    @Inject lateinit var networkService: NetworkService
    @Inject lateinit var gpsService: GPSService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContent {
            val isOnline by networkService.isOnline.collectAsState()
            val viewModel = ViewModelProvider(this)[ThemeViewModel::class.java]
            val themeUserSetting = viewModel.themeUserSetting.collectAsState()
            val darkTheme = themeUserSetting.value == AppTheme.MODE_NIGHT
            EchoTheme(darkTheme = darkTheme) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        ConnectivityStatus(isConnected = isOnline)
                        AppNavigationHost(
                            authenticationService = authenticationService,
                            repository = repository,
                        )
                    }
                }
            }
        }
    }
}
