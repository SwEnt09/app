package com.github.swent.echo.di

import com.github.swent.echo.connectivity.GPSService
import com.github.swent.echo.connectivity.SimpleGPSService
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [GPSServiceModule::class],
)
object FakeGPSServiceModule {
    @Singleton
    @Provides
    fun provideGPSService(): GPSService {
        return SimpleGPSService()
    }
}
