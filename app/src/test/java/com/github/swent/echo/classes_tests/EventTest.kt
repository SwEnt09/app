package com.github.swent.echo.classes_tests

import com.github.swent.echo.classes.Event
import org.junit.Assert.assertEquals
import org.junit.Test

class EventTest {
    @Test
    fun testEvent() {
        // Arrange
        val expectedEventId = "testEventId"
        val expectedOrganizerId = "testOrganizerId"
        val expectedTitle = "testTitle"
        val expectedDescription = "testDescription"
        val expectedLocation = "testLocation"
        val expectedStartDate = "testStartDate"
        val expectedEndDate = "testEndDate"

        // Act
        val event =
            Event(
                expectedEventId,
                expectedOrganizerId,
                expectedTitle,
                expectedDescription,
                expectedLocation,
                expectedStartDate,
                expectedEndDate
            )

        // Assert
        assertEquals(expectedEventId, event.eventId)
        assertEquals(expectedOrganizerId, event.organizerId)
        assertEquals(expectedTitle, event.title)
        assertEquals(expectedDescription, event.description)
        assertEquals(expectedLocation, event.location)
        assertEquals(expectedStartDate, event.startDate)
        assertEquals(expectedEndDate, event.endDate)
    }
}
