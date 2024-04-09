package com.github.swent.echo.compose.authentication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.authentication.AuthenticationState
import com.github.swent.echo.viewmodels.authentication.LoginViewModel

@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navActions: NavigationActions) {
    val state by loginViewModel.state.collectAsState()

    if (state is AuthenticationState.SignedIn) {
        LaunchedEffect(state) { navActions.navigateTo(Routes.MAP) }
    }

    Column(
        modifier = Modifier.testTag("login-screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AuthenticationScreen(
            action = "Login",
            state = state,
            onAuthenticate = loginViewModel::login,
        )
        if (state is AuthenticationState.SignedOut || state is AuthenticationState.Error) {
            Row {
                Text("Don't have an account? ")
                Text(
                    "Register",
                    color = MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier.clickable { navActions.navigateTo(Routes.REGISTER) }
                            .testTag("register-button")
                )
            }
        }
    }
}
