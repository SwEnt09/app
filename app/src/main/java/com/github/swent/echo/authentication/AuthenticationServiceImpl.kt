package com.github.swent.echo.authentication

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

/**
 * Implementation of [AuthenticationService] using Supabase.
 *
 * @param auth The Supabase authentication plugin.
 */
class AuthenticationServiceImpl(
    private val auth: Auth,
    private val composeAuth: ComposeAuth,
) : AuthenticationService {

    companion object {
        private const val TAG = "AuthenticationServiceImpl"
    }

    @Composable
    override fun startGoogleSignInCallback(onResult: (NativeSignInResult) -> Unit): () -> Unit {
        val action = composeAuth.rememberSignInWithGoogle(onResult)

        return { action.startFlow() }
    }

    override suspend fun signIn(email: String, password: String): AuthenticationResult {
        return supabaseAuthOperation("log in") {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    override suspend fun signUp(email: String, password: String): AuthenticationResult {
        return supabaseAuthOperation("register") {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    override suspend fun signOut(): AuthenticationResult {
        return supabaseAuthOperation("log out") { auth.signOut() }
    }

    override fun getCurrentUserID(): String? {
        return auth.currentSessionOrNull()?.user?.id
    }

    /**
     * Wraps a Supabase authentication operation in a try-catch block. If the operation fails, logs
     * the error and returns an [AuthenticationResult.Error] with an appropriate error message.
     * Otherwise, returns [AuthenticationResult.Success].
     *
     * @param name The name of the operation.
     * @param operation The operation to execute.
     * @return The result of the operation.
     */
    private suspend fun supabaseAuthOperation(
        name: String,
        operation: suspend () -> Unit,
    ): AuthenticationResult {
        try {
            operation()
        } catch (e: RestException) {
            // Convert the error code to a human-readable message.
            val message = e.error.replace('_', ' ').capitalize(Locale.current)
            Log.e(TAG, message, e)
            return AuthenticationResult.Error(message, e)
        } catch (e: Exception) {
            val message = "Unknown Exception: Failed to $name"
            Log.e(TAG, message, e)
            return AuthenticationResult.Error(message, e)
        }

        return AuthenticationResult.Success
    }
}
