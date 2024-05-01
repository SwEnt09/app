package com.github.swent.echo.di

import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.data.repository.Repository
import com.github.swent.echo.data.repository.SimpleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class],
)
object FakeRepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(authenticationService: AuthenticationService): Repository {
        return SimpleRepository(authenticationService)
    }
}
