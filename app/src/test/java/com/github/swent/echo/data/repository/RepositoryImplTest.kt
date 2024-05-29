package com.github.swent.echo.data.repository

import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.AssociationHeader
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.FileCache
import com.github.swent.echo.data.repository.datasources.LocalDataSource
import com.github.swent.echo.data.repository.datasources.RemoteDataSource
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.time.ZonedDateTime
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.function.ThrowingRunnable

class RepositoryImplTest {
    private lateinit var mockedRemoteDataSource: RemoteDataSource
    private lateinit var mockedLocalDataSource: LocalDataSource
    private lateinit var mockedNetworkService: NetworkService
    private lateinit var repositoryImpl: RepositoryImpl
    private lateinit var mockedFileCache: FileCache

    private val association =
        Association(
            "testAssoc",
            "Dummy Assoc",
            "blabla description",
            "url1",
            setOf(Tag("tag id", "tag description"))
        )
    private val association2 =
        Association("testAssoc2", "Dummy 2", "asdfg", "url2", setOf(Tag.EMPTY))
    private val tag = Tag("testTag", "Dummy Tag", "testTag")
    private val tag2 = Tag("testTag2", "Dummy 2", "testTag")
    private val event =
        Event(
            "testEvent",
            EventCreator("testCreator", ""),
            AssociationHeader.fromAssociation(association),
            "Dummy Event",
            "blabla description",
            Location("testLocation", 0.0, 0.0),
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            setOf(tag),
            0,
            0,
            0
        )
    private val event2 =
        Event(
            "testEvent2",
            EventCreator("testCrea", ""),
            AssociationHeader.fromAssociation(association),
            "Dummy ",
            "blabla asdf",
            Location("testLocation", 0.0, 0.0),
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            setOf(tag),
            0,
            0,
            0
        )
    private val userProfile =
        UserProfile("testUser", "Dummy User", null, null, emptySet(), emptySet(), emptySet())
    private val userProfile2 =
        UserProfile(
            "testUser2",
            "Dummy 2",
            null,
            null,
            emptySet(),
            setOf(AssociationHeader("assoc1", "assoc1name")),
            setOf(AssociationHeader("assoc2", "assoc2name"))
        )

    private val time = ZonedDateTime.parse("2024-01-01T00:00:00Z")

    private var isOnline: () -> Boolean = { true }

    private val userProfilePicture = ByteArray(50)

    @Before
    fun setUp() {
        mockedRemoteDataSource = mockk<RemoteDataSource>(relaxed = true)
        mockedLocalDataSource = mockk<LocalDataSource>(relaxed = true)
        mockedNetworkService = mockk<NetworkService>(relaxed = true)
        mockedFileCache = mockk<FileCache>(relaxed = true)
        repositoryImpl =
            RepositoryImpl(
                mockedRemoteDataSource,
                mockedLocalDataSource,
                mockedNetworkService,
                mockedFileCache
            )

        mockkStatic(ZonedDateTime::class)
        mockkStatic(HttpRequestBuilder::class)
    }

    @After
    fun tearDown() {
        unmockkStatic(ZonedDateTime::class)
    }

    @Test
    fun getAssociationTest() {
        every { ZonedDateTime.now() } returns time

        every { mockedNetworkService.isOnlineNow() } returns false
        coEvery { mockedLocalDataSource.getAssociation("testAssoc", any()) } returns association

        val associationResultOffline = runBlocking { repositoryImpl.getAssociation("testAssoc") }
        assertEquals(association, associationResultOffline)

        every { mockedNetworkService.isOnlineNow() } returns true
        coEvery {
            mockedLocalDataSource.getAssociation("testAssoc2", RepositoryImpl.ASSOCIATION_CACHE_TTL)
        } returns null
        coEvery { mockedRemoteDataSource.getAssociation("testAssoc2") } returns association2

        val associationResultOnline = runBlocking { repositoryImpl.getAssociation("testAssoc2") }
        assertEquals(association2, associationResultOnline)

        coEvery { mockedRemoteDataSource.getAssociation("testAssoc2") } returns null
        val associationResultOnlineDeleted = runBlocking {
            repositoryImpl.getAssociation("testAssoc2")
        }
        coVerify { mockedLocalDataSource.deleteAssociation("testAssoc2") }
        assertNull(associationResultOnlineDeleted)

        coEvery { mockedRemoteDataSource.getAssociation("testAssoc2") } throws
            HttpRequestException("test", HttpRequestBuilder())
        coEvery {
            mockedLocalDataSource.getAssociation("testAssoc2", RepositoryImpl.FETCH_ALL)
        } returns association2
        val associationResultOnlineThrows = runBlocking {
            repositoryImpl.getAssociation("testAssoc2")
        }
        assertEquals(association2, associationResultOnlineThrows)

        val associationResultOnlineFromCache = runBlocking {
            repositoryImpl.getAssociation("testAssoc")
        }
        assertEquals(association, associationResultOnlineFromCache)
    }

    @Test
    fun getAssociationsTest() {
        every { ZonedDateTime.now() } returns time

        every { mockedNetworkService.isOnlineNow() } returns false
        coEvery { mockedLocalDataSource.getAssociations(listOf("testAssoc"), any()) } returns
            listOf(association)

        val associationResultOffline = runBlocking {
            repositoryImpl.getAssociations(listOf("testAssoc"))
        }
        assertEquals(listOf(association), associationResultOffline)

        every { mockedNetworkService.isOnlineNow() } returns true
        coEvery {
            mockedLocalDataSource.getAssociations(
                listOf("testAssoc2"),
                RepositoryImpl.ASSOCIATION_CACHE_TTL
            )
        } returns emptyList()
        coEvery { mockedRemoteDataSource.getAssociations(listOf("testAssoc2")) } returns
            listOf(association2)

        val associationResultOnline = runBlocking {
            repositoryImpl.getAssociations(listOf("testAssoc2"))
        }
        assertEquals(listOf(association2), associationResultOnline)

        val associationResultOnlineFromCache = runBlocking {
            repositoryImpl.getAssociations(listOf("testAssoc"))
        }
        assertEquals(listOf(association), associationResultOnlineFromCache)

        coEvery {
            mockedLocalDataSource.getAssociations(
                listOf("throwsAssoc"),
                RepositoryImpl.ASSOCIATION_CACHE_TTL
            )
        } returns emptyList()
        coEvery { mockedRemoteDataSource.getAssociations(listOf("throwsAssoc")) } throws
            HttpRequestException("test", HttpRequestBuilder())
        coEvery {
            mockedLocalDataSource.getAssociations(listOf("throwsAssoc"), RepositoryImpl.FETCH_ALL)
        } returns listOf(association)

        val associationResultOnlineThrows = runBlocking {
            repositoryImpl.getAssociations(listOf("throwsAssoc"))
        }
        assertEquals(listOf(association), associationResultOnlineThrows)
    }

    @Test
    fun getAllAssociationsTest() {
        every { ZonedDateTime.now() } returns time

        every { mockedNetworkService.isOnlineNow() } returns false
        coEvery { mockedLocalDataSource.getAllAssociations(any()) } returns listOf(association)

        val resultOffline = runBlocking { repositoryImpl.getAllAssociations() }
        assertEquals(listOf(association), resultOffline)

        every { mockedNetworkService.isOnlineNow() } returns true

        val resultNotExpired = runBlocking { repositoryImpl.getAllAssociations() }
        assertEquals(listOf(association), resultNotExpired)

        every { ZonedDateTime.now() } returns
            time.plusSeconds(RepositoryImpl.ASSOCIATION_CACHE_TTL + 1)
        coEvery {
            mockedRemoteDataSource.getAssociationsNotIn(listOf(association.associationId))
        } returns listOf(association2)

        val resultOnline = runBlocking { repositoryImpl.getAllAssociations() }
        coVerify {
            mockedLocalDataSource.deleteAssociationsNotIn(listOf(association.associationId))
        }
        coVerify { mockedLocalDataSource.setAssociations(listOf(association2)) }
        assertEquals(
            ZonedDateTime.now().toEpochSecond(),
            RepositoryImpl.associations_last_cached_all
        )
        assertEquals(listOf(association, association2), resultOnline)

        coEvery {
            mockedLocalDataSource.getAllAssociations(RepositoryImpl.ASSOCIATION_CACHE_TTL)
        } returns listOf(association)
        coEvery {
            mockedRemoteDataSource.getAssociationsNotIn(listOf(association.associationId))
        } throws HttpRequestException("test", HttpRequestBuilder())
        coEvery { mockedLocalDataSource.getAllAssociations(RepositoryImpl.FETCH_ALL) } returns
            listOf(association, association2)
        val resultOnlineThrows = runBlocking { repositoryImpl.getAllAssociations() }
        assertEquals(listOf(association, association2), resultOnlineThrows)
    }

    @Test
    fun getEventTest() {
        every { ZonedDateTime.now() } returns time

        every { mockedNetworkService.isOnlineNow() } returns false
        coEvery { mockedLocalDataSource.getEvent("testEvent", any()) } returns event

        val eventResultOffline = runBlocking { repositoryImpl.getEvent("testEvent") }
        assertEquals(event, eventResultOffline)

        every { mockedNetworkService.isOnlineNow() } returns true
        coEvery {
            mockedLocalDataSource.getEvent("testEvent2", RepositoryImpl.EVENT_CACHE_TTL)
        } returns null
        coEvery { mockedRemoteDataSource.getEvent("testEvent2") } returns event2
        coEvery { mockedLocalDataSource.getEvent("deletedEvent", any()) } returns null
        coEvery { mockedRemoteDataSource.getEvent("deletedEvent") } returns null
        coEvery {
            mockedLocalDataSource.getEvent("throwsEvent", RepositoryImpl.EVENT_CACHE_TTL)
        } returns null
        coEvery { mockedRemoteDataSource.getEvent("throwsEvent") } throws
            HttpRequestException("test", HttpRequestBuilder())
        coEvery { mockedLocalDataSource.getEvent("throwsEvent", RepositoryImpl.FETCH_ALL) } returns
            event

        val eventResultOnline = runBlocking { repositoryImpl.getEvent("testEvent2") }
        assertEquals(event2, eventResultOnline)

        val eventResultOnlineFromCache = runBlocking { repositoryImpl.getEvent("testEvent") }
        assertEquals(event, eventResultOnlineFromCache)

        val eventResultOnlineDeleted = runBlocking { repositoryImpl.getEvent("deletedEvent") }
        coVerify { mockedLocalDataSource.deleteEvent("deletedEvent") }
        assertNull(eventResultOnlineDeleted)

        val eventResultOnlineThrows = runBlocking { repositoryImpl.getEvent("throwsEvent") }
        assertEquals(event, eventResultOnlineThrows)
    }

    @Test
    fun createEventTest() {
        every { mockedNetworkService.isOnlineNow() } returns false
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable { runBlocking { repositoryImpl.createEvent(event) } }
        )

        every { mockedNetworkService.isOnlineNow() } returns true
        runBlocking { repositoryImpl.createEvent(event) }
        coVerify { mockedRemoteDataSource.createEvent(event) }

        coEvery { mockedRemoteDataSource.createEvent(event) } throws
            HttpRequestException("test", HttpRequestBuilder())
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable { runBlocking { repositoryImpl.createEvent(event) } }
        )
    }

    @Test
    fun setEventTest() {
        every { mockedNetworkService.isOnlineNow() } returns false
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable { runBlocking { repositoryImpl.setEvent(event) } }
        )

        every { mockedNetworkService.isOnlineNow() } returns true
        runBlocking { repositoryImpl.setEvent(event) }
        coVerify {
            mockedRemoteDataSource.setEvent(event)
            mockedLocalDataSource.setEvent(event)
        }

        coEvery { mockedRemoteDataSource.setEvent(event) } throws
            HttpRequestException("test", HttpRequestBuilder())
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable { runBlocking { repositoryImpl.setEvent(event) } }
        )
    }

    @Test
    fun deleteEventTest() {
        every { mockedNetworkService.isOnlineNow() } returns false
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable { runBlocking { repositoryImpl.deleteEvent(event) } }
        )

        every { mockedNetworkService.isOnlineNow() } returns true
        runBlocking { repositoryImpl.deleteEvent(event) }
        coVerify {
            mockedRemoteDataSource.deleteEvent(event)
            mockedLocalDataSource.deleteEvent(event.eventId)
        }

        coEvery { mockedRemoteDataSource.deleteEvent(event) } throws
            HttpRequestException("test", HttpRequestBuilder())
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable { runBlocking { repositoryImpl.deleteEvent(event) } }
        )
    }

    @Test
    fun getAllEventsTest() {
        every { ZonedDateTime.now() } returns time

        every { mockedNetworkService.isOnlineNow() } returns false
        coEvery { mockedLocalDataSource.getAllEvents(any()) } returns listOf(event)

        val resultOffline = runBlocking { repositoryImpl.getAllEvents() }
        assertEquals(listOf(event), resultOffline)

        every { mockedNetworkService.isOnlineNow() } returns true

        val resultNotExpired = runBlocking { repositoryImpl.getAllEvents() }
        assertEquals(listOf(event), resultNotExpired)

        every { ZonedDateTime.now() } returns time.plusSeconds(RepositoryImpl.EVENT_CACHE_TTL + 1)
        coEvery { mockedRemoteDataSource.getEventsNotIn(listOf(event.eventId)) } returns
            listOf(event2)

        val resultOnline = runBlocking { repositoryImpl.getAllEvents() }
        coVerify { mockedLocalDataSource.deleteEventsNotIn(listOf(event.eventId)) }
        coVerify { mockedLocalDataSource.setEvents(listOf(event2)) }
        assertEquals(ZonedDateTime.now().toEpochSecond(), RepositoryImpl.events_last_cached_all)
        assertEquals(listOf(event, event2), resultOnline)

        coEvery { mockedLocalDataSource.getAllEvents(RepositoryImpl.EVENT_CACHE_TTL) } returns
            listOf(event)
        coEvery { mockedRemoteDataSource.getAllEvents() } throws
            HttpRequestException("test", HttpRequestBuilder())
        coEvery { mockedLocalDataSource.getAllEvents(RepositoryImpl.FETCH_ALL) } returns
            listOf(event, event2)
        val resultOnlineThrows = runBlocking { repositoryImpl.getAllEvents() }
        assertEquals(listOf(event, event2), resultOnlineThrows)
    }

    @Test
    fun joinEventTest() {
        every { mockedNetworkService.isOnlineNow() } returns false
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable { runBlocking { repositoryImpl.joinEvent(userProfile.userId, event) } }
        )

        every { mockedNetworkService.isOnlineNow() } returns true
        coEvery { mockedRemoteDataSource.joinEvent(userProfile.userId, event.eventId) } returns true
        coEvery { mockedRemoteDataSource.getEvent(event.eventId, any()) } returns event
        val result = runBlocking { repositoryImpl.joinEvent(userProfile.userId, event) }
        assertTrue(result)
        coVerify {
            mockedRemoteDataSource.joinEvent(userProfile.userId, event.eventId)
            mockedLocalDataSource.joinEvent(userProfile.userId, event.eventId)
            mockedRemoteDataSource.getEvent(event.eventId, any())
            mockedLocalDataSource.setEvent(event)
        }

        coEvery { mockedRemoteDataSource.getEvent(event.eventId, 10u) } throws
            HttpRequestException("test", HttpRequestBuilder())
        coEvery { mockedLocalDataSource.getEvent(event.eventId, RepositoryImpl.FETCH_ALL) } returns
            event
        runBlocking { repositoryImpl.joinEvent(userProfile.userId, event) }
        coVerify {
            mockedLocalDataSource.setEvent(
                event.copy(participantCount = event.participantCount + 1)
            )
        }

        coEvery { mockedRemoteDataSource.joinEvent(userProfile.userId, event.eventId) } throws
            HttpRequestException("test", HttpRequestBuilder())
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable { runBlocking { repositoryImpl.joinEvent(userProfile.userId, event) } }
        )
    }

    @Test
    fun leaveEventTest() {
        every { mockedNetworkService.isOnlineNow() } returns false
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable {
                runBlocking { repositoryImpl.leaveEvent(userProfile.userId, event) }
            }
        )

        every { mockedNetworkService.isOnlineNow() } returns true
        coEvery { mockedRemoteDataSource.leaveEvent(userProfile.userId, event.eventId) } returns
            true
        coEvery { mockedRemoteDataSource.getEvent(event.eventId) } returns null
        val result = runBlocking { repositoryImpl.leaveEvent(userProfile.userId, event) }
        assertTrue(result)
        coVerify {
            mockedRemoteDataSource.leaveEvent(userProfile.userId, event.eventId)
            mockedLocalDataSource.leaveEvent(userProfile.userId, event.eventId)
            mockedRemoteDataSource.getEvent(event.eventId, any())
        }

        coEvery { mockedRemoteDataSource.getEvent(event.eventId, 10u) } throws
            HttpRequestException("test", HttpRequestBuilder())
        coEvery { mockedLocalDataSource.getEvent(event.eventId, RepositoryImpl.FETCH_ALL) } returns
            event
        runBlocking { repositoryImpl.leaveEvent(userProfile.userId, event) }
        coVerify {
            mockedLocalDataSource.setEvent(
                event.copy(participantCount = event.participantCount - 1)
            )
        }

        coEvery { mockedRemoteDataSource.leaveEvent(userProfile.userId, event.eventId) } throws
            HttpRequestException("test", HttpRequestBuilder())
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable {
                runBlocking { repositoryImpl.leaveEvent(userProfile.userId, event) }
            }
        )
    }

    @Test
    fun getJoinedEventsTest() {
        every { ZonedDateTime.now() } returns time

        every { mockedNetworkService.isOnlineNow() } returns false
        coEvery { mockedLocalDataSource.getJoinedEvents(userProfile.userId, any()) } returns
            listOf(event)

        val resultOffline = runBlocking { repositoryImpl.getJoinedEvents(userProfile.userId) }
        assertEquals(listOf(event), resultOffline)

        every { mockedNetworkService.isOnlineNow() } returns true

        val resultNotExpired = runBlocking { repositoryImpl.getJoinedEvents(userProfile.userId) }
        assertEquals(listOf(event), resultNotExpired)

        every { ZonedDateTime.now() } returns time.plusSeconds(RepositoryImpl.EVENT_CACHE_TTL + 1)
        coEvery {
            mockedRemoteDataSource.getJoinedEventsNotIn(userProfile.userId, listOf(event.eventId))
        } returns listOf(event2)

        val resultOnline = runBlocking { repositoryImpl.getJoinedEvents(userProfile.userId) }
        coVerify {
            mockedLocalDataSource.leaveEventsNotIn(userProfile.userId, listOf(event.eventId))
        }
        coVerify { mockedLocalDataSource.joinEvents(userProfile.userId, listOf(event2.eventId)) }
        assertEquals(
            ZonedDateTime.now().toEpochSecond(),
            RepositoryImpl.events_last_cached_joined.get(userProfile.userId)
        )
        assertEquals(listOf(event, event2), resultOnline)

        coEvery {
            mockedLocalDataSource.getJoinedEvents(
                userProfile.userId,
                RepositoryImpl.EVENT_CACHE_TTL
            )
        } returns listOf(event)
        coEvery { mockedRemoteDataSource.getJoinedEvents(userProfile.userId) } throws
            HttpRequestException("test", HttpRequestBuilder())
        coEvery {
            mockedLocalDataSource.getJoinedEvents(userProfile.userId, RepositoryImpl.FETCH_ALL)
        } returns listOf(event, event2)
        val resultOnlineThrows = runBlocking { repositoryImpl.getJoinedEvents(userProfile.userId) }
        assertEquals(listOf(event, event2), resultOnlineThrows)
    }

    @Test
    fun getTagTest() {
        every { ZonedDateTime.now() } returns time

        every { mockedNetworkService.isOnlineNow() } returns false
        coEvery { mockedLocalDataSource.getTag("testTag", any()) } returns tag

        val tagResultOffline = runBlocking { repositoryImpl.getTag("testTag") }
        assertEquals(tag, tagResultOffline)

        every { mockedNetworkService.isOnlineNow() } returns true
        coEvery { mockedLocalDataSource.getTag("testTag2", RepositoryImpl.TAG_CACHE_TTL) } returns
            null
        coEvery { mockedRemoteDataSource.getTag("testTag2") } returns tag2

        val tagResultOnline = runBlocking { repositoryImpl.getTag("testTag2") }
        assertEquals(tag2, tagResultOnline)

        val tagResultOnlineFromCache = runBlocking { repositoryImpl.getTag("testTag") }
        assertEquals(tag, tagResultOnlineFromCache)

        coEvery { mockedLocalDataSource.getTag("throwsTag", RepositoryImpl.TAG_CACHE_TTL) } returns
            null
        coEvery { mockedRemoteDataSource.getTag("throwsTag") } throws
            HttpRequestException("test", HttpRequestBuilder())
        coEvery { mockedLocalDataSource.getTag("throwsTag", RepositoryImpl.FETCH_ALL) } returns tag
        val resultOnlineThrows = runBlocking { repositoryImpl.getTag("throwsTag") }
        assertEquals(tag, resultOnlineThrows)
    }

    @Test
    fun getSubTagsTest() {
        every { ZonedDateTime.now() } returns time

        RepositoryImpl.tags_last_cached_all = 0

        every { mockedNetworkService.isOnlineNow() } returns false
        coEvery { mockedLocalDataSource.getSubTags("randomTag", any()) } returns listOf(tag)

        val resultOffline = runBlocking { repositoryImpl.getSubTags("randomTag") }
        assertEquals(listOf(tag), resultOffline)

        every { mockedNetworkService.isOnlineNow() } returns true

        val resultNotExpired = runBlocking { repositoryImpl.getSubTags("randomTag") }
        assertEquals(listOf(tag), resultNotExpired)

        every { ZonedDateTime.now() } returns time.plusSeconds(RepositoryImpl.TAG_CACHE_TTL + 1)
        coEvery { mockedRemoteDataSource.getSubTagsNotIn("randomTag", listOf(tag.tagId)) } returns
            listOf(tag2)

        val resultOnline = runBlocking { repositoryImpl.getSubTags("randomTag") }
        coVerify { mockedLocalDataSource.deleteSubTagsNotIn("randomTag", listOf(tag.tagId)) }
        coVerify { mockedLocalDataSource.setTags(listOf(tag2)) }
        assertEquals(
            ZonedDateTime.now().toEpochSecond(),
            RepositoryImpl.tags_last_cached_subs.get("randomTag")
        )
        assertEquals(listOf(tag, tag2), resultOnline)

        coEvery {
            mockedLocalDataSource.getSubTags("throwsTag", RepositoryImpl.TAG_CACHE_TTL)
        } returns listOf(tag)
        coEvery { mockedRemoteDataSource.getSubTagsNotIn("throwsTag", listOf(tag.tagId)) } throws
            HttpRequestException("test", HttpRequestBuilder())
        coEvery { mockedLocalDataSource.getSubTags("throwsTag", RepositoryImpl.FETCH_ALL) } returns
            listOf(tag, tag2)
        val resultOnlineThrows = runBlocking { repositoryImpl.getSubTags("throwsTag") }
        assertEquals(listOf(tag, tag2), resultOnlineThrows)
    }

    @Test
    fun getAllTagsTest() {
        every { ZonedDateTime.now() } returns time

        every { mockedNetworkService.isOnlineNow() } returns false
        coEvery { mockedLocalDataSource.getAllTags(any()) } returns listOf(tag)

        val resultOffline = runBlocking { repositoryImpl.getAllTags() }
        assertEquals(listOf(tag), resultOffline)

        every { mockedNetworkService.isOnlineNow() } returns true

        val resultNotExpired = runBlocking { repositoryImpl.getAllTags() }
        assertEquals(listOf(tag), resultNotExpired)

        every { ZonedDateTime.now() } returns time.plusSeconds(RepositoryImpl.TAG_CACHE_TTL + 1)
        coEvery { mockedRemoteDataSource.getAllTagsNotIn(listOf(tag.tagId)) } returns listOf(tag2)

        val resultOnline = runBlocking { repositoryImpl.getAllTags() }
        coVerify { mockedLocalDataSource.deleteAllTagsNotIn(listOf(tag.tagId)) }
        coVerify { mockedLocalDataSource.setTags(listOf(tag2)) }
        assertEquals(ZonedDateTime.now().toEpochSecond(), RepositoryImpl.tags_last_cached_all)
        assertEquals(listOf(tag, tag2), resultOnline)

        coEvery { mockedLocalDataSource.getAllTags(RepositoryImpl.TAG_CACHE_TTL) } returns
            listOf(tag)
        coEvery { mockedRemoteDataSource.getAllTagsNotIn(listOf(tag.tagId)) } throws
            HttpRequestException("test", HttpRequestBuilder())
        coEvery { mockedLocalDataSource.getAllTags(RepositoryImpl.FETCH_ALL) } returns
            listOf(tag, tag2)
        val resultOnlineThrows = runBlocking { repositoryImpl.getAllTags() }
        assertEquals(listOf(tag, tag2), resultOnlineThrows)
    }

    @Test
    fun getUserProfileTest() {
        every { ZonedDateTime.now() } returns time

        every { mockedNetworkService.isOnlineNow() } returns false
        coEvery { mockedLocalDataSource.getUserProfile("testUser", any()) } returns userProfile

        val userProfileResultOffline = runBlocking { repositoryImpl.getUserProfile("testUser") }
        assertEquals(userProfile, userProfileResultOffline)

        every { mockedNetworkService.isOnlineNow() } returns true
        coEvery {
            mockedLocalDataSource.getUserProfile("testUser2", RepositoryImpl.USERPROFILE_CACHE_TTL)
        } returns null
        coEvery { mockedRemoteDataSource.getUserProfile("testUser2") } returns userProfile2
        coEvery { mockedLocalDataSource.getUserProfile("deletedUserProfile", any()) } returns null
        coEvery { mockedRemoteDataSource.getUserProfile("deletedUserProfile") } returns null
        coEvery {
            mockedLocalDataSource.getAssociationIds(
                (userProfile2.committeeMember + userProfile2.associationsSubscriptions).map {
                    it.associationId
                },
                RepositoryImpl.ASSOCIATION_CACHE_TTL
            )
        } returns userProfile2.committeeMember.map { it.associationId }
        coEvery {
            mockedRemoteDataSource.getAssociations(
                userProfile2.associationsSubscriptions.map { it.associationId }
            )
        } returns listOf(association2)

        val userProfileResultOnline = runBlocking { repositoryImpl.getUserProfile("testUser2") }
        assertEquals(userProfile2, userProfileResultOnline)
        coVerify { mockedLocalDataSource.setAssociations(listOf(association2)) }

        val userProfileResultOnlineFromCache = runBlocking {
            repositoryImpl.getUserProfile("testUser")
        }
        assertEquals(userProfile, userProfileResultOnlineFromCache)

        val userProfileResultOnlineDeleted = runBlocking {
            repositoryImpl.getUserProfile("deletedUserProfile")
        }
        coVerify { mockedLocalDataSource.deleteUserProfile("deletedUserProfile") }
        assertNull(userProfileResultOnlineDeleted)

        coEvery {
            mockedLocalDataSource.getUserProfile("throwsUser", RepositoryImpl.USERPROFILE_CACHE_TTL)
        } returns null

        coEvery { mockedRemoteDataSource.getAssociations(any()) } throws
            HttpRequestException("test", HttpRequestBuilder())
        coEvery {
            mockedLocalDataSource.getUserProfile("throwsUser", RepositoryImpl.FETCH_ALL)
        } returns userProfile
        val resultOnlineGetAssociationsThrows = runBlocking {
            repositoryImpl.getUserProfile("testUser")
        }
        assertEquals(userProfile, resultOnlineGetAssociationsThrows)

        coEvery { mockedRemoteDataSource.getUserProfile("throwsUser") } throws
            HttpRequestException("test", HttpRequestBuilder())
        coEvery {
            mockedLocalDataSource.getUserProfile("throwsUser", RepositoryImpl.FETCH_ALL)
        } returns userProfile2
        val resultOnlineThrows = runBlocking { repositoryImpl.getUserProfile("throwsUser") }
        assertEquals(userProfile2, resultOnlineThrows)
    }

    @Test
    fun setUserProfileTest() {
        every { mockedNetworkService.isOnlineNow() } returns false
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable { runBlocking { repositoryImpl.setUserProfile(userProfile) } }
        )

        every { mockedNetworkService.isOnlineNow() } returns true
        runBlocking { repositoryImpl.setUserProfile(userProfile) }
        coVerify {
            mockedRemoteDataSource.setUserProfile(userProfile)
            mockedLocalDataSource.setUserProfile(userProfile)
        }

        coEvery { mockedRemoteDataSource.setUserProfile(userProfile) } throws
            HttpRequestException("test", HttpRequestBuilder())
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable { runBlocking { repositoryImpl.setUserProfile(userProfile) } }
        )
    }

    @Test
    fun deleteUserProfileTest() {
        every { mockedNetworkService.isOnlineNow() } returns false
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable { runBlocking { repositoryImpl.deleteUserProfile(userProfile) } }
        )

        every { mockedNetworkService.isOnlineNow() } returns true
        runBlocking { repositoryImpl.deleteUserProfile(userProfile) }
        coVerify {
            mockedRemoteDataSource.deleteUserProfile(userProfile)
            mockedLocalDataSource.deleteUserProfile(userProfile.userId)
        }

        coEvery { mockedRemoteDataSource.deleteUserProfile(userProfile) } throws
            HttpRequestException("test", HttpRequestBuilder())
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable { runBlocking { repositoryImpl.deleteUserProfile(userProfile) } }
        )
    }

    @Test
    fun getUserProfilePictureTest() {
        every { mockedNetworkService.isOnlineNow() } returns true
        coEvery { mockedFileCache.get(any()) } returns null
        coEvery { mockedRemoteDataSource.getUserProfilePicture(userProfile.userId) } returns
            userProfilePicture
        var res: ByteArray? = null
        runBlocking { res = repositoryImpl.getUserProfilePicture(userProfile.userId) }
        assertEquals(userProfilePicture, res)

        coEvery { mockedRemoteDataSource.getUserProfilePicture(userProfile2.userId) } throws
            HttpRequestException("test", HttpRequestBuilder())
        assertNull(runBlocking { repositoryImpl.getUserProfilePicture(userProfile2.userId) })

        every { mockedNetworkService.isOnlineNow() } returns false
        runBlocking { res = repositoryImpl.getUserProfilePicture(userProfile.userId) }
        assertNull(res)
    }

    @Test
    fun setUserProfilePictureTest() {
        every { mockedNetworkService.isOnlineNow() } returns true
        coEvery { mockedFileCache.get(any()) } returns null
        runBlocking { repositoryImpl.setUserProfilePicture(userProfile.userId, userProfilePicture) }
        coVerify {
            mockedRemoteDataSource.setUserProfilePicture(userProfile.userId, userProfilePicture)
        }

        coEvery { mockedRemoteDataSource.setUserProfilePicture(userProfile2.userId, any()) } throws
            HttpRequestException("test", HttpRequestBuilder())
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable {
                runBlocking {
                    repositoryImpl.setUserProfilePicture(userProfile2.userId, userProfilePicture)
                }
            }
        )

        every { mockedNetworkService.isOnlineNow() } returns false
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable {
                runBlocking {
                    repositoryImpl.setUserProfilePicture(userProfile.userId, userProfilePicture)
                }
            }
        )
    }

    @Test
    fun deleteUserProfilePictureTest() {
        every { mockedNetworkService.isOnlineNow() } returns true
        coEvery { mockedFileCache.get(any()) } returns null
        runBlocking { repositoryImpl.deleteUserProfilePicture(userProfile.userId) }
        coVerify { mockedRemoteDataSource.deleteUserProfilePicture(userProfile.userId) }

        coEvery { mockedRemoteDataSource.deleteUserProfilePicture(userProfile2.userId) } throws
            HttpRequestException("test", HttpRequestBuilder())
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable {
                runBlocking { repositoryImpl.deleteUserProfilePicture(userProfile2.userId) }
            }
        )

        every { mockedNetworkService.isOnlineNow() } returns false
        assertThrows(
            RepositoryStoreWhileNoInternetException::class.java,
            ThrowingRunnable {
                runBlocking { repositoryImpl.deleteUserProfilePicture(userProfile.userId) }
            }
        )
    }
}
