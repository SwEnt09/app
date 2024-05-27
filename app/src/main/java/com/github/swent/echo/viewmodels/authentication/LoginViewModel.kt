package com.github.swent.echo.viewmodels.authentication

import androidx.lifecycle.viewModelScope
import com.github.swent.echo.authentication.AuthenticationResult
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

/**
 * ViewModel for the login screen.
 *
 * @param auth The authentication service to use.
 * @param repository The repository to use.
 * @param networkService The network service to use.
 */
@HiltViewModel
class LoginViewModel
@Inject
constructor(
    override val auth: AuthenticationService,
    override val repository: Repository,
    override val networkService: NetworkService,
) : AbstractAuthenticationViewModel() {

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
                is AuthenticationResult.Success ->
                    _state.value = AuthenticationState.SignedIn(getNextRoute())
                is AuthenticationResult.Error ->
                    _state.value = AuthenticationState.Error(result.message)
            }
        }
    }
}
