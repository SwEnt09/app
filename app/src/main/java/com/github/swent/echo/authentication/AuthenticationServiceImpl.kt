package com.github.swent.echo.authentication

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

/**
 * Implementation of [AuthenticationService] using Supabase.
 *
 * @param supabase The Supabase client to use for authentication.
 */
class AuthenticationServiceImpl(private val supabase: SupabaseClient) : AuthenticationService {

    override suspend fun signIn(email: String, password: String): AuthenticationResult {
        try {
            supabase.auth.signInWith(Email) {
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
            supabase.auth.signUpWith(Email) {
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
            supabase.auth.signOut()
        } catch (e: Exception) {
            return AuthenticationResult.Error("Failed to sign out", e)
        }

        return AuthenticationResult.Success
    }

    override suspend fun getCurrentUserID(): String? {
        return supabase.auth.currentSessionOrNull()?.user?.id
    }
}
