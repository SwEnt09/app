package com.github.swent.echo.viewmodels.authentication

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.ui.navigation.Routes
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Provides Google sign in functionality. */
abstract class AbstractAuthenticationViewModel : ViewModel() {
    protected abstract val auth: AuthenticationService
    protected abstract val repository: Repository

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
            is NativeSignInResult.Success ->
                viewModelScope.launch {
                    _state.value = AuthenticationState.SignedIn(getNextRoute())
                }
        }
    }

    /**
     * Returns the next route depending on the value of the user profile. If the profile is null, it
     * returns [Routes.PROFILE_CREATION] and [Routes.MAP] otherwise. Note you should only call this
     * function if the user has sucessfully logged as as it makes the assumption that the user id is
     * not null.
     */
    protected suspend fun getNextRoute(): Routes {
        val userId = auth.getCurrentUserID()!!
        return if (repository.getUserProfile(userId) == null) {
            Routes.PROFILE_CREATION
        } else {
            Routes.MAP
        }
    }
}
