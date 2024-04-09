package com.github.swent.echo.compose.authentication

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.authentication.AuthenticationState
import com.github.swent.echo.viewmodels.authentication.RegisterViewModel

@Composable
fun RegisterScreen(registerViewModel: RegisterViewModel, navActions: NavigationActions) {
    val state by registerViewModel.state.collectAsState()
    val context = LocalContext.current

    if (state is AuthenticationState.SignedIn) {
        LaunchedEffect(state) {
            Toast.makeText(context, "Confirm your email to continue", Toast.LENGTH_LONG).show()
            navActions.navigateTo(Routes.LOGIN)
        }
    }

    Column(
        modifier = Modifier.testTag("register-screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AuthenticationScreen(
            action = "Register",
            state = state,
            onAuthenticate = registerViewModel::register,
        )
        if (state is AuthenticationState.SignedOut || state is AuthenticationState.Error) {
            Row {
                Text("Do you have an account? ")
                Text(
                    "Login",
                    color = MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier.clickable { navActions.navigateTo(Routes.LOGIN) }
                            .testTag("login-button")
                )
            }
        }
    }
}
