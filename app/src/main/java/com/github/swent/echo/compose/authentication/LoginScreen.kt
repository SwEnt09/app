package com.github.swent.echo.compose.authentication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.swent.echo.viewmodels.authentication.LoginViewModel

@Composable
fun LoginScreen(loginViewModel: LoginViewModel) {
    val state by loginViewModel.state.collectAsState()
    AuthenticationScreen(
        action = "Login",
        state = state,
        onAuthenticate = loginViewModel::login,
    )
}
