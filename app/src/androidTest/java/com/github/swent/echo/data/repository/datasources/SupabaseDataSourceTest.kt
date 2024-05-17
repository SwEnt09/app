package com.github.swent.echo.data.repository.datasources

import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.authentication.AuthenticationServiceImpl
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.EventCreator
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.supabase.SupabaseDataSource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.gotrue.auth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Arrays
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@Ignore
@HiltAndroidTest
class SupabaseDataSourceTest {
    @get:Rule val hiltRule = HiltAndroidRule(this)
    @Inject lateinit var supabaseClient: SupabaseClient

    lateinit var authenticationService: AuthenticationService

    lateinit var source: SupabaseDataSource

    private val association =
        Association(
            "b0122e3e-82ed-4409-83f9-dbfb9761db20",
            "Dummy Assoc",
            "DO NOT DELETE/MODIFY: tests in repository.datasources.SupabaseTest will fail"
        )
    private val tag = Tag("daba142a-a276-4b7e-824d-43ca088633ff", "Dummy Tag")
    private val rootTagId = "1d253a7e-eb8c-4546-bc98-1d3adadcffe8"
    private val event =
        Event(
            "3bcf6f25-81d4-4a14-9caa-c05feb593da0",
            EventCreator("4792cb7e-894e-4dad-bf7c-7f0660d0b648", "SubapaseDataSourceTest User"),
            association,
            "Dummy Event",
            "blabla description",
            Location("testLocation", 0.0, 0.0),
            ZonedDateTime.of(2024, 4, 25, 14, 54, 51, 0, ZoneId.systemDefault()),
            ZonedDateTime.of(2024, 4, 25, 14, 54, 51, 0, ZoneId.systemDefault()),
            HashSet<Tag>(Arrays.asList(tag)),
            0,
            0,
            0
        )
    private val userProfile =
        UserProfile(
            "4792cb7e-894e-4dad-bf7c-7f0660d0b648",
            "SubapaseDataSourceTest User",
            null,
            null,
            setOf(tag),
            setOf(association),
            setOf(association)
        )

    @Before
    fun instanciate() {
        hiltRule.inject()

        authenticationService =
            AuthenticationServiceImpl(supabaseClient.auth, supabaseClient.composeAuth)

        runBlocking { authenticationService.signIn("test@testing.com", "123456") }

        source = SupabaseDataSource(supabaseClient)
    }

    @Test
    fun getAssociationTest() {
        val associationFetched = runBlocking {
            source.getAssociation("b0122e3e-82ed-4409-83f9-dbfb9761db20")
        }
        assertEquals(association, associationFetched)
    }

    @Test
    fun getAssociationsNotInTest() {
        val associationsFetched = runBlocking {
            source.getAssociationsNotIn(listOf(association.associationId))
        }
        assertNotNull(associationsFetched)
    }

    @Test
    fun getAllAssociationsTest() {
        val associations = runBlocking { source.getAllAssociations() }
        assertNotNull(associations)
    }

    @Test
    fun getEventTest() {
        val eventFetched = runBlocking { source.getEvent("3bcf6f25-81d4-4a14-9caa-c05feb593da0") }
        assertEquals(event, eventFetched)
    }

    @Test
    fun createEventTest() {
        val eventFetched = runBlocking { source.createEvent(event.copy(title = "Autre")) }
        assertNotNull(eventFetched)
    }

    @Test
    fun setEventTest() {
        runBlocking { source.setEvent(event) }
    }

    @Test
    fun getEventsNotInTest() {
        val result = runBlocking { source.getEventsNotIn(listOf(event.eventId)) }
        assertNotNull(result)
    }

    @Test
    fun deleteEventTest() {
        runBlocking { source.deleteEvent(event) }
    }

    @Test
    fun getAllEventsTest() {
        val associations = runBlocking { source.getAllEvents() }
        assertNotNull(associations)
    }

    @Test
    fun joinEventTest() {
        val joined = runBlocking { source.joinEvent(userProfile.userId, event.eventId) }
        assertTrue(joined)
    }

    @Test
    fun leaveEventTest() {
        val left = runBlocking { source.leaveEvent(userProfile.userId, event.eventId) }
        assertTrue(left)
    }

    @Test
    fun getJoinedEventsTest() {
        val joinedEvents = runBlocking { source.getJoinedEvents(userProfile.userId) }
        assertEquals(listOf(event.copy(participantCount = 1)), joinedEvents)
    }

    @Test
    fun getJoinedEventsNotInTest() {
        val joinedEvents = runBlocking {
            source.getJoinedEventsNotIn(userProfile.userId, listOf(event.eventId))
        }
        assertNotNull(joinedEvents)
    }

    @Test
    fun getTagTest() {
        val tagFetched = runBlocking { source.getTag("daba142a-a276-4b7e-824d-43ca088633ff") }
        assertEquals(tag, tagFetched)
    }

    @Test
    fun getSubTagsTest() {
        val tagsFetched = runBlocking { source.getSubTags(rootTagId) }
        assertNotNull(tagsFetched)
    }

    @Test
    fun getSubTagsNotIn() {
        val tagsFetched = runBlocking {
            source.getSubTagsNotIn(
                rootTagId,
                listOf(
                    "d84bb3e8-3c07-49a2-8281-f149c4922870",
                    "dd25a9e5-e3bc-48ee-a931-332d38336253"
                )
            )
        }
        assertEquals(3, tagsFetched.size)
    }

    @Test
    fun getAllTagsTest() {
        val tagsFetched = runBlocking { source.getAllTags() }
        assertNotNull(tagsFetched)
    }

    @Test
    fun getAllTagsNotIn() {
        val tagsFetched = runBlocking { source.getAllTagsNotIn(listOf(rootTagId)) }
        assertNotNull(tagsFetched)
    }

    @Test
    fun getUserProfileTest() {
        val userProfileFetched = runBlocking { source.getUserProfile(userProfile.userId) }
        assertEquals(userProfile, userProfileFetched)
    }

    @Test
    fun setUserProfileTest() {
        runBlocking { source.setUserProfile(userProfile) }
    }

    @Test
    fun deleteUserProfileTest() {
        runBlocking { source.deleteUserProfile(userProfile) }
    }
}
