package com.github.swent.echo.compose.authentication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.swent.echo.viewmodels.authentication.RegisterViewModel

@Composable
fun RegisterScreen(registerViewModel: RegisterViewModel) {
    val state by registerViewModel.state.collectAsState()
    AuthenticationScreen(
        action = "Register",
        state = state,
        onAuthenticate = registerViewModel::register,
    )
}
