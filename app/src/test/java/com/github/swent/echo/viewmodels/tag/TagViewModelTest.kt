package com.github.swent.echo.viewmodels.tag

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
    val allTagList = listOf(Tag("a", "a"), Tag("b", "b"))
    val topTagsList = listOf(Tag("w", "w"), Tag("e", "e"))
    val subTagsList = listOf(Tag("x", "x"), Tag("y", "y"))
    val tagExample = Tag("z", "z")
    val rootTagId = ""
    private val scheduler = TestCoroutineScheduler()

    @Before
    fun init() {
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        runBlocking { tagViewModel = TagViewModel(mockedRepository) }
        scheduler.runCurrent()
    }

    @Test
    fun allTagsShouldReturnAllTheTags() {
        coEvery { mockedRepository.getAllTags() } returns allTagList
        runBlocking { tagViewModel = TagViewModel(mockedRepository) }
        scheduler.runCurrent()
        coVerify { mockedRepository.getAllTags() }
        assertEquals(tagViewModel.allTags.value, allTagList)
    }

    @Test
    fun getSubTagsShouldReturnTheSubTags() {
        coEvery { mockedRepository.getSubTags(tagExample.tagId) } returns subTagsList
        val subTags = tagViewModel.getSubTags(tagExample)
        scheduler.runCurrent()
        coVerify { mockedRepository.getSubTags(tagExample.tagId) }
        assertEquals(subTags.value, subTagsList)
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
        val depth = tagViewModel.currentDepth.value
        tagViewModel.goDown(tagExample)
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
