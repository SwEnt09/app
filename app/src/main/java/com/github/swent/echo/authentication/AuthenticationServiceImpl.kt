package com.github.swent.echo.authentication

import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

/**
 * Implementation of [AuthenticationService] using Supabase.
 *
 * @param auth The Supabase authentication plugin.
 */
class AuthenticationServiceImpl(private val auth: Auth) : AuthenticationService {

    override suspend fun signIn(email: String, password: String): AuthenticationResult {
        try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        } catch (e: Exception) {
            return AuthenticationResult.Error("Failed to sign in", e)
        }

        return AuthenticationResult.Success
    }

    override suspend fun signUp(email: String, password: String): AuthenticationResult {
        try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
        } catch (e: Exception) {
            return AuthenticationResult.Error("Failed to sign up", e)
        }

        return AuthenticationResult.Success
    }

    override suspend fun signOut(): AuthenticationResult {
        try {
            auth.signOut()
        } catch (e: Exception) {
            return AuthenticationResult.Error("Failed to sign out", e)
        }

        return AuthenticationResult.Success
    }

    override fun getCurrentUserID(): String? {
        return auth.currentSessionOrNull()?.user?.id
    }
}
