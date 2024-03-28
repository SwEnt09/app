package com.github.swent.echo.di

import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.authentication.AuthenticationServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth

@Module
@InstallIn(SingletonComponent::class)
class AuthenticationServiceModule {

    @Provides
    fun provideAuthenticationService(supabaseClient: SupabaseClient): AuthenticationService {
        return AuthenticationServiceImpl(supabaseClient.auth)
    }
}
