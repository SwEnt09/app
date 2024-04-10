package com.github.swent.echo.viewmodels.event

import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import java.time.Instant
import java.util.Date
import junit.framework.TestCase.assertEquals
import org.junit.Test

class EventViewModelTest {

    private val TEST_EVENT =
        Event(
            eventId = "testid",
            organizerId = "testid",
            title = "test title",
            description = "test description",
            location = Location("test location", 100.0, 100.0),
            startDate = Date.from(Instant.now()),
            endDate = Date.from(Instant.now()),
            tags = setOf(Tag("1", "tag1"))
        )

    @Test
    fun modifyEventTest() {
        val event = TEST_EVENT
        val eventViewModel = EventViewModel()
        eventViewModel.setEvent(event)
        assertEquals(eventViewModel.getEvent(), event)
    }

    @Test
    fun addTagToEventTest() {
        val eventViewModel = EventViewModel()
        val newTag = Tag("2", "tag2")
        val addedTag = eventViewModel.getAndAddTagFromString(newTag.name)
        assertEquals(addedTag, newTag)
        assertEquals(eventViewModel.getEvent().tags, setOf(newTag))
    }

    @Test
    fun deleteTagOfEventTest() {
        val event = TEST_EVENT
        val eventViewModel = EventViewModel()
        eventViewModel.setEvent(event)
        eventViewModel.deleteTag(Tag("1", "tag1"))
        assertEquals(eventViewModel.getEvent().tags, setOf<Tag>())
    }
    
}
