package com.github.swent.echo.di

import android.view.View
import com.github.swent.echo.compose.map.IMapViewProvider
import com.github.swent.echo.compose.map.OsmdroidMapViewProvider
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
    fun provideMapViewProvider(): IMapViewProvider<View> {
        // TODO: Ideally, we would create a fake implementation of the `IMapViewProvider` interface
        // which would simply draw the events on some white background.
        return OsmdroidMapViewProvider() as IMapViewProvider<View>
    }
}
