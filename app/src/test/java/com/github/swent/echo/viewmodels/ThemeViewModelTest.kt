package com.github.swent.echo.viewmodels

import app.cash.turbine.test
import com.github.swent.echo.ThemePreferenceManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class ThemeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var themePreferenceManager: ThemePreferenceManager
    private lateinit var viewModel: ThemeViewModel
    private val themeFlow = MutableStateFlow(AppTheme.MODE_NIGHT)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        themePreferenceManager = mockk(relaxed = true) { coEvery { theme } returns themeFlow }
        viewModel = ThemeViewModel(themePreferenceManager)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialstateIsDark() =
        runTest(testDispatcher) {
            viewModel.themeUserSetting.test { assert(awaitItem() == AppTheme.MODE_NIGHT) }
        }

    @Test
    fun switchesTheme() =
        runTest(testDispatcher) {
            viewModel.toggleTheme()
            testDispatcher.scheduler.advanceUntilIdle()
            coVerify { themePreferenceManager.setTheme(AppTheme.MODE_DAY) }
            themeFlow.value = AppTheme.MODE_DAY
            viewModel.toggleTheme()
            testDispatcher.scheduler
                .advanceUntilIdle() // ensures that the coroutine inside toggleTheme() is executed
            // fully before proceeding: crucial for the test to pass
            coVerify { themePreferenceManager.setTheme(AppTheme.MODE_NIGHT) }
        }
}
