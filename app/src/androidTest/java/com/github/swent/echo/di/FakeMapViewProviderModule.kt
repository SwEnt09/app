package com.github.swent.echo.di

import android.view.View
import com.github.swent.echo.compose.map.MapViewProvider
import com.github.swent.echo.compose.map.SimpleMapViewProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [MapViewProviderModule::class],
)
object FakeMapViewProviderModule {

    @Singleton
    @Provides
    fun provideMapViewProvider(): MapViewProvider<View> {
        return SimpleMapViewProvider() as MapViewProvider<View>
    }
}
