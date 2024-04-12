package com.github.swent.echo.viewmodels.authentication

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.github.swent.echo.authentication.AuthenticationService
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Provides Google sign in functionality. */
abstract class AbstractAuthenticationViewModel : ViewModel() {
    protected abstract val auth: AuthenticationService

    protected val _state = MutableStateFlow<AuthenticationState>(AuthenticationState.SignedOut)

    /** The current state of the view model. */
    val state = _state.asStateFlow()

    /**
     * Returns a callback to start the Google sign in process.
     *
     * @return The callback to start the Google sign in process.
     */
    @Composable
    fun startGoogleSignInCallback(): () -> Unit {
        val startGoogleSignIn = auth.startGoogleSignInCallback(this::onGoogleSignInResult)

        return {
            _state.value = AuthenticationState.SigningIn
            startGoogleSignIn()
        }
    }

    private fun onGoogleSignInResult(result: NativeSignInResult) {
        when (result) {
            is NativeSignInResult.ClosedByUser ->
                _state.value = AuthenticationState.Error("Google sign in canceled.")
            is NativeSignInResult.Error ->
                _state.value = AuthenticationState.Error("Google sign in failed.")
            is NativeSignInResult.NetworkError ->
                _state.value = AuthenticationState.Error("A network error occurred.")
            is NativeSignInResult.Success -> _state.value = AuthenticationState.SignedIn
        }
    }
}
