package com.github.swent.echo

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.swent.echo.viewmodels.AppTheme
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class ThemePreferenceManager @Inject constructor(context: Context) {
    private val dataStore = context.dataStore
    private val THEME_KEY = stringPreferencesKey("theme")

    val theme: Flow<AppTheme> =
        dataStore.data.map { preferences ->
            val themeString = preferences[THEME_KEY] ?: AppTheme.MODE_NIGHT.name
            AppTheme.valueOf(themeString)
        }

    suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { preferences -> preferences[THEME_KEY] = theme.name }
    }
}
