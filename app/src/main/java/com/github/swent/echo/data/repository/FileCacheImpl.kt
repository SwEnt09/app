package com.github.swent.echo.data.repository

import com.github.swent.echo.data.repository.datasources.FileCache
import java.io.File
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Implementation of [FileCache] that uses the file system for caching.
 *
 * @param cacheDir The directory to store the cached files.
 * @param dispatcher The dispatcher to use for file operations.
 */
class FileCacheImpl(
    private val cacheDir: File,
    private val dispatcher: CoroutineDispatcher,
) : FileCache {

    override suspend fun get(name: String): ByteArray? {
        val file = File(cacheDir, name)
        return if (file.exists()) {
            withContext(dispatcher) { file.readBytes() }
        } else {
            null
        }
    }

    override suspend fun set(name: String, content: ByteArray) {
        val writer = File(cacheDir, name).outputStream()
        withContext(dispatcher) { writer.write(content) }
    }

    override suspend fun delete(name: String): Boolean {
        val file = File(cacheDir, name)
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }
}
