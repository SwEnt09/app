package com.github.swent.echo.di

import androidx.compose.runtime.Composable
import com.github.swent.echo.authentication.AuthenticationResult
import com.github.swent.echo.authentication.AuthenticationService
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import javax.inject.Inject

/** A simple implementation of [AuthenticationService] which will be injected in android tests. */
class SimpleAuthenticationService @Inject constructor() : AuthenticationService {

    companion object {
        private const val USER_ID = "test-user-id"
    }

    private var userId: String? = null
    private var email: String? = null
    private var password: String? = null

    override suspend fun initialize() {}

    @Composable
    override fun startGoogleSignInCallback(onResult: (NativeSignInResult) -> Unit): () -> Unit {
        return { onResult(NativeSignInResult.Success) }
    }

    override suspend fun signIn(email: String, password: String): AuthenticationResult {
        return if (this.email == email && this.password == password) {
            userId = USER_ID
            AuthenticationResult.Success
        } else {
            AuthenticationResult.Error("Invalid email or password")
        }
    }

    override suspend fun signUp(email: String, password: String): AuthenticationResult {
        this.email = email
        this.password = password
        userId = USER_ID

        return AuthenticationResult.Success
    }

    override suspend fun signOut(): AuthenticationResult {
        userId = null

        return AuthenticationResult.Success
    }

    override fun getCurrentUserID(): String? {
        return userId
    }
}
