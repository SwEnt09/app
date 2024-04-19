package com.github.swent.echo.data.repository

import io.mockk.spyk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RepositoryTest {

    @Test
    fun `getSubTags should return an empty list`() {
        val repository = spyk<Repository>()
        val subTags = runBlocking { repository.getSubTags("tagId") }
        assert(subTags.isEmpty())
    }
}
