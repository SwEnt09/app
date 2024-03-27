package com.github.swent.echo.classes_tests

import com.github.swent.echo.classes.Tag
import org.junit.Assert.assertEquals
import org.junit.Test

class TagTest {
    @Test
    fun testTag() {
        // Arrange
        val expectedTagId = "testTagId"
        val expectedName = "testName"

        // Act
        val tag = Tag(expectedTagId, expectedName)

        // Assert
        assertEquals(expectedTagId, tag.tagId)
        assertEquals(expectedName, tag.name)
    }
}