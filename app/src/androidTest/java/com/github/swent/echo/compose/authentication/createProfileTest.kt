package com.github.swent.echo.compose.authentication

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.data.model.Tag
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class createProfileTest {
    @get:Rule val composeTestRule = createComposeRule()
    private var tagClicked: Tag? = null

    @Before
    fun setUp() {
        tagClicked = null
    }
}
