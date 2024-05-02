package com.github.swent.echo.di

import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.data.repository.RepositoryImpl
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import com.github.swent.echo.data.supabase.SupabaseDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(supabaseClient: SupabaseClient): Repository {
        val remoteDataSource: RemoteDataSource = SupabaseDataSource(supabaseClient)

        return RepositoryImpl(remoteDataSource)
    }

    /*
    @Singleton
    @Provides
    fun provideRepository(authenticationService: AuthenticationService): Repository {
        return SimpleRepository(authenticationService)
    } */
}
