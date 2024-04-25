package com.github.swent.echo.di

import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.data.repository.SimpleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /*
    @Singleton
    @Provides
    fun provideRepository(supabaseClient: SupabaseClient): Repository {
        val remoteDataSource: RemoteDataSource = Supabase(supabaseClient)

        return RepositoryImpl(remoteDataSource)
    }
    */

    @Singleton
    @Provides
    fun provideRepository(authenticationService: AuthenticationService): Repository {
        return SimpleRepository(authenticationService)
    }
}
