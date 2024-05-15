package com.github.swent.echo.viewmodels.tag

import androidx.lifecycle.SavedStateHandle
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.Repository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class TagViewModelTest {

    private val mockedRepository = mockk<Repository>(relaxed = true)
    private lateinit var tagViewModel: TagViewModel
    val topTagsList = listOf(Tag("w", "w"), Tag("e", "e"))
    val subTagsList = listOf(Tag("x", "x"), Tag("y", "y"))
    val tagExample = Tag("z", "z")
    val rootTagId = "1d253a7e-eb8c-4546-bc98-1d3adadcffe8"
    private val scheduler = TestCoroutineScheduler()
    private val rootTagHandle = SavedStateHandle(mapOf())

    @Before
    fun init() {
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        runBlocking { tagViewModel = TagViewModel(mockedRepository, rootTagHandle) }
        scheduler.runCurrent()
    }

    @Test
    fun getTagShouldReturnTheCorrectTag() {
        coEvery { mockedRepository.getTag(tagExample.tagId) } returns tagExample
        val tag = tagViewModel.getTag(tagExample.tagId)
        scheduler.runCurrent()
        coVerify { mockedRepository.getTag(tagExample.tagId) }
        assertEquals(tag.value, tagExample)
    }

    @Test
    fun goDownThenUpShouldNotMoveTheCurrentLevel() {
        coEvery { mockedRepository.getSubTags(tagExample.tagId) } returns subTagsList
        val depth = tagViewModel.currentDepth.value
        tagViewModel.goDown(tagExample)
        scheduler.runCurrent()
        assertEquals(depth + 1, tagViewModel.currentDepth.value)
        tagViewModel.goUp()
        scheduler.runCurrent()
        assertEquals(depth, tagViewModel.currentDepth.value)
    }

    @Test
    fun goUpShouldQueryTopTags() {
        coEvery { mockedRepository.getSubTags(rootTagId) } returns topTagsList
        coEvery { mockedRepository.getSubTags(topTagsList.first().tagId) } returns subTagsList
        tagViewModel.goDown(topTagsList.first())
        tagViewModel.goUp()
        scheduler.runCurrent()
        coVerify { mockedRepository.getSubTags(rootTagId) }
        assertEquals(topTagsList, tagViewModel.tags.value)
    }
}
