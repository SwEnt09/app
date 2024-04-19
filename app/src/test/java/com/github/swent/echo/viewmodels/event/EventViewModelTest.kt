package com.github.swent.echo.viewmodels.event

import android.util.Log
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import java.time.ZonedDateTime
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

class EventViewModelTest {

    private val TEST_EVENT =
        Event(
            eventId = "testid",
            creator = UserProfile("testid", "testname", null, null, emptySet()),
            organizer = Association("testid", "testname", "testdesc"),
            title = "test title",
            description = "test description",
            location = Location("test location", 100.0, 100.0),
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            tags = setOf(Tag("1", "tag1")),
            0,
            0,
            0
        )

    private fun mockLog() {
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any() as String) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

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
    fun deleteTagFromEventTest() {
        val event = TEST_EVENT
        val eventViewModel = EventViewModel()
        eventViewModel.setEvent(event)
        eventViewModel.deleteTag(Tag("1", "tag1"))
        assertEquals(eventViewModel.getEvent().tags, setOf<Tag>())
    }

    @Test
    fun modifyEventWhileSavingLogWarningAndDoesNotChangeEvent() {
        mockLog()
        val event = TEST_EVENT
        val eventModified = event.copy(title = "change title")
        val eventViewModel = EventViewModel()
        eventViewModel.setEvent(event)
        eventViewModel.saveEvent()
        eventViewModel.setEvent(eventModified)
        verify { Log.w(any(), any() as String) }
        assertEquals(event, eventViewModel.getEvent())
    }

    @Test
    fun saveEventWhileSavingLogWarning() {
        mockLog()
        val eventViewModel = EventViewModel()
        eventViewModel.setEvent(TEST_EVENT)
        eventViewModel.saveEvent()
        eventViewModel.saveEvent()
        verify { Log.w(any(), any() as String) }
    }

    @Test
    fun getAndAddTagFromStringReturnNullWithWrongTag() {
        val wrongTag = "not a valid tag"
        val eventViewModel = EventViewModel()
        val ret = eventViewModel.getAndAddTagFromString(wrongTag)
        assertNull(ret)
    }

    @Test
    fun saveWithEndDateBeforeStartDateChangeStatusToError() {
        val event =
            TEST_EVENT.copy(
                startDate = ZonedDateTime.now(),
                endDate = ZonedDateTime.now().minusDays(1)
            )
        val eventViewModel = EventViewModel()
        eventViewModel.setEvent(event)
        eventViewModel.saveEvent()
        assertTrue(eventViewModel.getStatus() is EventStatus.Error)
    }

    @Test
    fun saveWithBlankTitleChangeStatusToError() {
        val event = TEST_EVENT.copy(title = " ")
        val eventViewModel = EventViewModel()
        eventViewModel.setEvent(event)
        eventViewModel.saveEvent()
        assertTrue(eventViewModel.getStatus() is EventStatus.Error)
    }
}
