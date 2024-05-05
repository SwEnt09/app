package com.github.swent.echo.data.repository.datasources

import io.mockk.spyk
import java.lang.reflect.InvocationTargetException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

// TODO: Remove the default implementation and delete the test once they are implemented.
class LocalDataSourceTest {

    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setUp() {
        localDataSource = spyk<LocalDataSource>()
    }

    @Test
    fun `deleteAssociations should throw NotImplementedError`() {
        assertThrows(InvocationTargetException::class.java) {
            runBlocking { localDataSource.getAllAssociationsSyncedBefore(0) }
        }
    }

    @Test
    fun `deleteEvents should throw NotImplementedError`() {
        assertThrows(InvocationTargetException::class.java) {
            runBlocking { localDataSource.getAllEventsSyncedBefore(0) }
        }
    }

    @Test
    fun `deleteTags should throw NotImplementedError`() {
        assertThrows(InvocationTargetException::class.java) {
            runBlocking { localDataSource.getAllTagsSyncedBefore(0) }
        }
    }

    @Test
    fun `deleteUserProfiles should throw NotImplementedError`() {
        assertThrows(InvocationTargetException::class.java) {
            runBlocking { localDataSource.getAllUserProfilesSyncedBefore(0) }
        }
    }
}
