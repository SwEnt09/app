package com.github.swent.echo.data.repository

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.User
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Arrays
import java.util.Date
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RepositoryImplTest {
    private lateinit var mockedRemoteDataSource: RemoteDataSource
    private lateinit var repositoryImpl: RepositoryImpl

    private val association = Association("testAssoc", "Dummy Assoc", "blabla description")
    private val tag = Tag("testTag", "Dummy Tag")
    private val event =
        Event(
            "testEvent",
            "testCreator",
            "testOrganizer",
            "Dummy Event",
            "blabla description",
            Location("testLocation", 0.0, 0.0),
            Date(0),
            Date(1),
            HashSet<Tag>(Arrays.asList(tag))
        )
    private val userProfile = UserProfile("testUser", "Dummy User")
    private val user = User("testUser", userProfile)

    @Before
    fun setUp() {
        mockedRemoteDataSource = mockk<RemoteDataSource>(relaxed = true)
        repositoryImpl = RepositoryImpl(mockedRemoteDataSource)
    }

    @Test
    fun getAssociationTest() {
        every { runBlocking { mockedRemoteDataSource.getAssociation("testAssoc") } } returns
            association
        val associationResult = runBlocking { repositoryImpl.getAssociation("testAssoc") }
        assertEquals(association, associationResult)
    }

    @Test
    fun setAssociationTest() {
        runBlocking { repositoryImpl.setAssociation(association) }
        verify { runBlocking { repositoryImpl.setAssociation(association) } }
    }

    @Test
    fun getAllAssociationsTest() {
        every { runBlocking { mockedRemoteDataSource.getAllAssociations() } } returns
            Arrays.asList(association)
        val associationsResult = runBlocking { repositoryImpl.getAllAssociations() }
        assertEquals(Arrays.asList(association), associationsResult)
    }

    @Test
    fun getEventTest() {
        every { runBlocking { mockedRemoteDataSource.getEvent("testEvent") } } returns event
        val eventResult = runBlocking { repositoryImpl.getEvent("testEvent") }
        assertEquals(event, eventResult)
    }

    @Test
    fun setEventTest() {
        runBlocking { repositoryImpl.setEvent(event) }
        verify { runBlocking { repositoryImpl.setEvent(event) } }
    }

    @Test
    fun getAllEventsTest() {
        every { runBlocking { mockedRemoteDataSource.getAllEvents() } } returns Arrays.asList(event)
        val eventsResult = runBlocking { repositoryImpl.getAllEvents() }
        assertEquals(Arrays.asList(event), eventsResult)
    }

    @Test
    fun getTagTest() {
        every { runBlocking { mockedRemoteDataSource.getTag("testTag") } } returns tag
        val tagResult = runBlocking { repositoryImpl.getTag("testTag") }
        assertEquals(tag, tagResult)
    }

    @Test
    fun setTagTest() {
        runBlocking { repositoryImpl.setTag(tag) }
        verify { runBlocking { repositoryImpl.setTag(tag) } }
    }

    @Test
    fun getAllTagsTest() {
        every { runBlocking { mockedRemoteDataSource.getAllTags() } } returns Arrays.asList(tag)
        val tagsResult = runBlocking { repositoryImpl.getAllTags() }
        assertEquals(Arrays.asList(tag), tagsResult)
    }

    @Test
    fun getUserTest() {
        every { runBlocking { mockedRemoteDataSource.getUser("testUser") } } returns user
        val userResult = runBlocking { repositoryImpl.getUser("testUser") }
        assertEquals(user, userResult)
    }

    @Test
    fun setUserTest() {
        runBlocking { repositoryImpl.setUser(user) }
        verify { runBlocking { repositoryImpl.setUser(user) } }
    }

    @Test
    fun getUserProfileTest() {
        every { runBlocking { mockedRemoteDataSource.getUserProfile("testUser") } } returns
            userProfile
        val userProfileResult = runBlocking { repositoryImpl.getUserProfile("testUser") }
        assertEquals(userProfile, userProfileResult)
    }

    @Test
    fun setUserProfileTest() {
        runBlocking { repositoryImpl.setUserProfile(userProfile) }
        verify { runBlocking { repositoryImpl.setUserProfile(userProfile) } }
    }
}
