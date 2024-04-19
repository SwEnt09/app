package com.github.swent.echo.di

import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.authentication.AuthenticationServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.gotrue.auth
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationServiceModule {

    @Singleton
    @Provides
    fun provideAuthenticationService(supabaseClient: SupabaseClient): AuthenticationService {
        return AuthenticationServiceImpl(supabaseClient.auth, supabaseClient.composeAuth)
    }
}
