package com.github.swent.echo.di

import android.app.Application
import com.github.swent.echo.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

@Module
@InstallIn(SingletonComponent::class)
object SupabaseClientModule {

    @Provides
    fun provideSupabaseClient(application: Application): SupabaseClient {
        val supabaseUrl = application.resources.getString(R.string.supabase_url)
        val supabasePublicKey = application.resources.getString(R.string.supabase_public_key)
        val googleWebClientId = application.resources.getString(R.string.google_web_client_id)
        return createSupabaseClient(supabaseUrl, supabasePublicKey) {
            install(Auth)
            install(Postgrest)
            install(ComposeAuth) { googleNativeLogin(googleWebClientId) }
        }
    }
}
