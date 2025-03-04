package com.github.swent.echo.data.repository

import com.github.swent.echo.data.repository.datasources.FileCache
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class FileCacheImplTest {

    companion object {
        const val FILE_NAME = "test.txt"
        val FILE_CONTENT = "Hello, World!".toByteArray()
    }

    @Inject lateinit var fileCache: FileCache

    @get:Rule val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        runBlocking { fileCache.delete(FILE_NAME) }
    }

    @Test
    fun testSetAndGetFile() {
        runBlocking {
            fileCache.set(FILE_NAME, FILE_CONTENT)
            val retrievedFile = fileCache.get(FILE_NAME)
            assertNotNull(retrievedFile)
            assertArrayEquals(FILE_CONTENT, retrievedFile)
        }
    }

    @Test
    fun testDeleteFile() {
        runBlocking {
            fileCache.set(FILE_NAME, FILE_CONTENT)
            val success = fileCache.delete(FILE_NAME)
            assertTrue(success)
            val retrievedFile = fileCache.get(FILE_NAME)
            assertNull(retrievedFile)
        }
    }
}
