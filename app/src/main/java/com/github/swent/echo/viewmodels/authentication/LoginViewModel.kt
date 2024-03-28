package com.github.swent.echo.viewmodels.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationResult
import com.github.swent.echo.authentication.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the login screen.
 *
 * @param auth The authentication service to use.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(private val auth: AuthenticationService) : ViewModel() {

    private val _state = MutableStateFlow<AuthenticationState>(AuthenticationState.SignedOut)

    /** The current state of the view model. */
    val state = _state.asStateFlow()

    /**
     * Sign in with email and password.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     */
    fun login(email: String, password: String) {
        _state.value = AuthenticationState.SigningIn
        viewModelScope.launch {
            when (val result = auth.signIn(email, password)) {
                is AuthenticationResult.Success -> _state.value = AuthenticationState.SignedIn
                is AuthenticationResult.Error ->
                    _state.value = AuthenticationState.Error(result.message)
            }
        }
    }
}
