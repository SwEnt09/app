package com.github.swent.echo.classes_tests

import com.github.swent.echo.classes.User
import org.junit.Assert.assertEquals
import org.junit.Test

class UserTest {
    @Test
    fun testUser() {
        // Arrange
        val expectedUserId = "testUserId"
        val expectedMetadata = "testMetadata"

        // Act
        val user = User(expectedUserId, expectedMetadata)

        // Assert
        assertEquals(expectedUserId, user.userId)
        assertEquals(expectedMetadata, user.metadata)
    }
}
