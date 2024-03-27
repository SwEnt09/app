package com.github.swent.echo.classes_tests

import org.junit.Assert.*
import org.junit.Test
import com.github.swent.echo.classes.User_Profile

class User_ProfileTest {
    @Test
    fun testUser_Profile() {
        // Arrange
        val expectedUserId = "testId"
        val expectedName = "testName"

        // Act
        val userProfile = User_Profile(expectedUserId, expectedName)

        // Assert
        assertEquals(expectedUserId, userProfile.userId)
        assertEquals(expectedName, userProfile.name)
    }
}