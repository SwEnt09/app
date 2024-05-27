package com.github.swent.echo.data.repository

import android.app.Application
import com.github.swent.echo.data.repository.datasources.FileCache
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileCacheImpl
@Inject
constructor(
    application: Application,
) : FileCache {

    private val cacheDir: File = application.cacheDir

    override suspend fun get(name: String): ByteArray? {
        val file = File(cacheDir, name)
        return if (file.exists()) {
            withContext(Dispatchers.IO) { file.readBytes() }
        } else {
            null
        }
    }

    override suspend fun set(name: String, content: ByteArray) {
        val writer = File(cacheDir, name).outputStream()
        withContext(Dispatchers.IO) { writer.write(content) }
    }

    override suspend fun delete(name: String) {
        val file = File(cacheDir, name)
        if (file.exists()) file.delete()
    }
}
