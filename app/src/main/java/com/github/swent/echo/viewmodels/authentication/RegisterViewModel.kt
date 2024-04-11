package com.github.swent.echo.viewmodels.authentication

import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationResult
import com.github.swent.echo.authentication.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

/**
 * ViewModel for the register screen.
 *
 * @param auth The authentication service to use.
 */
@HiltViewModel
class RegisterViewModel
@Inject
constructor(
    override val auth: AuthenticationService,
) : AbstractAuthenticationViewModel() {

    /**
     * Sign up with email and password.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     */
    fun register(email: String, password: String) {
        _state.value = AuthenticationState.SigningIn
        viewModelScope.launch {
            when (val result = auth.signUp(email, password)) {
                is AuthenticationResult.Success -> _state.value = AuthenticationState.SignedIn
                is AuthenticationResult.Error ->
                    _state.value = AuthenticationState.Error(result.message)
            }
        }
    }
}
