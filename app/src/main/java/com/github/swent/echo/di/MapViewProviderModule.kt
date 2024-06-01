package com.github.swent.echo.di

import android.view.View
import com.github.swent.echo.compose.map.MapViewProvider
import com.github.swent.echo.compose.map.MapViewProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapViewProviderModule {

    @Singleton
    @Provides
    fun provideMapViewProvider(): MapViewProvider<View> {
        return MapViewProviderImpl() as MapViewProvider<View>
    }
}
