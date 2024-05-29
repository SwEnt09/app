package com.github.swent.echo.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.swent.echo.ThemePreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppTheme {
    MODE_DAY,
    MODE_NIGHT
}

@HiltViewModel
class ThemeViewModel
@Inject
constructor(
    private val themePreferenceManager: ThemePreferenceManager,
    private val application: Application
) : ViewModel() {
    private val _themeUserSetting =
        MutableStateFlow(
            themePreferenceManager.getSystemDefaultTheme(application.applicationContext)
        )
    val themeUserSetting: StateFlow<AppTheme> = _themeUserSetting.asStateFlow()

    init {
        viewModelScope.launch {
            themePreferenceManager.theme.collect { theme -> _themeUserSetting.value = theme }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val newTheme =
                if (_themeUserSetting.value == AppTheme.MODE_NIGHT) {
                    AppTheme.MODE_DAY
                } else {
                    AppTheme.MODE_NIGHT
                }
            themePreferenceManager.setTheme(newTheme)
            _themeUserSetting.value = newTheme
        }
    }
}
