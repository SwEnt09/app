package com.github.swent.echo.di

import com.github.swent.echo.connectivity.NetworkService
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkServiceModule::class],
)
object FakeNetworkServiceModule {

    @Singleton
    @Provides
    fun provideNetworkService(): NetworkService {
        return SimpleNetworkService()
    }
}
