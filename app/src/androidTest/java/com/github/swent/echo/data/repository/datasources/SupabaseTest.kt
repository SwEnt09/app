package com.github.swent.echo.data.repository.datasources

import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.authentication.AuthenticationServiceImpl
import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.Supabase as SupabaseSource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.gotrue.auth
import java.time.ZonedDateTime
import java.util.Arrays
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.function.ThrowingRunnable

@Ignore
@HiltAndroidTest
class SupabaseTest {
    @get:Rule val hiltRule = HiltAndroidRule(this)
    @Inject lateinit var supabaseClient: SupabaseClient

    lateinit var authenticationService: AuthenticationService

    lateinit var source: SupabaseSource

    private val association =
        Association(
            "b0122e3e-82ed-4409-83f9-dbfb9761db20",
            "Dummy Assoc",
            "DO NOT DELETE/MODIFY: tests in repository.datasources.SupabaseTest will fail"
        )
    private val tag = Tag("daba142a-a276-4b7e-824d-43ca088633ff", "Dummy Tag")
    private val event =
        Event(
            "3bcf6f25-81d4-4a14-9caa-c05feb593da0",
            UserProfile("e65e9435-a9f2-4474-be11-9054305f1a54", ""),
            association,
            "Dummy Event",
            "blabla description",
            Location("testLocation", 0.0, 0.0),
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            HashSet<Tag>(Arrays.asList(tag)),
            0,
            0,
            0
        )
    private val userProfile = UserProfile("39ed9088-73b9-4ad1-ad0f-bbc1f8dbe759", "Dummy User")

    @Before
    fun instanciate() {
        hiltRule.inject()

        authenticationService =
            AuthenticationServiceImpl(supabaseClient.auth, supabaseClient.composeAuth)

        runBlocking { authenticationService.signIn("test@example.com", "123456") }

        source = SupabaseSource(supabaseClient)
    }

    @Test
    fun getAssociationTest() {
        val associationFetched = runBlocking {
            source.getAssociation("b0122e3e-82ed-4409-83f9-dbfb9761db20")
        }
        assertEquals(association, associationFetched)
    }

    @Test
    fun getAllAssociationsTest() {
        val associations = runBlocking { source.getAllAssociations() }
        assertNotNull(associations)
    }

    @Test
    fun getEventTest() {
        assertThrows(
            NoSuchElementException::class.java,
            ThrowingRunnable {
                runBlocking { source.getEvent("3bcf6f25-81d4-4a14-9caa-c05feb593da0") }
            }
        )
    }

    @Test
    fun setEventTest() {
        assertThrows(
            SerializationException::class.java,
            ThrowingRunnable { runBlocking { source.setEvent(event) } }
        )
    }

    @Test
    fun getAllEventsTest() {
        val associations = runBlocking { source.getAllEvents() }
        assertNotNull(associations)
    }

    @Test
    fun getTagTest() {
        val tagFetched = runBlocking { source.getTag("daba142a-a276-4b7e-824d-43ca088633ff") }
        assertEquals(tag, tagFetched)
    }

    @Test
    fun getAllTagsTest() {
        val associations = runBlocking { source.getAllTags() }
        assertNotNull(associations)
    }

    @Test
    fun getUserProfileTest() {
        val userProfileFetched = runBlocking {
            source.getUserProfile("39ed9088-73b9-4ad1-ad0f-bbc1f8dbe759")
        }
        assertEquals(userProfile, userProfileFetched)
    }

    @Test
    fun setUserProfileTest() {
        runBlocking { source.setUserProfile(userProfile) }
    }
}
