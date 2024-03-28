package com.github.swent.echo.classes_tests

import com.github.swent.echo.classes.Association
import org.junit.Assert.*
import org.junit.Test

class AssociationTest {
    @Test
    fun testUser_Profile() {
        // Arrange
        val expectedAssociationId = "testId"
        val expectedName = "testName"
        val expectedDescription = "testDescription"

        // Act
        val association = Association(expectedAssociationId, expectedName, expectedDescription)

        // Assert
        assertEquals(expectedAssociationId, association.associationId)
        assertEquals(expectedName, association.name)
        assertEquals(expectedDescription, association.description)
    }
}
