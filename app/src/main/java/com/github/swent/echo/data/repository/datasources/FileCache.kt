package com.github.swent.echo.data.repository.datasources

/** Interface for file caching. */
interface FileCache {

    /**
     * Get the file content by name.
     *
     * @param name The name of the file to get.
     */
    suspend fun get(name: String): ByteArray?

    /**
     * Set the file content by name.
     *
     * @param name The name of the file to set.
     * @param content The content of the file to set.
     */
    suspend fun set(name: String, content: ByteArray)

    /**
     * Delete the file by name.
     *
     * @param name The name of the file to delete.
     * @return `true` if the file is deleted, `false` otherwise.
     */
    suspend fun delete(name: String): Boolean
}
