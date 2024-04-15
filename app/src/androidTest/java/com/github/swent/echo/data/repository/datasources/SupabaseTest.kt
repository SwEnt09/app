package com.github.swent.echo.data.repository.datasources

import com.github.swent.echo.data.model.Association
import com.github.swent.echo.data.model.Event
import com.github.swent.echo.data.model.Location
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.datasources.Supabase as SupabaseSource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.exceptions.UnauthorizedRestException
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import java.time.ZonedDateTime
import java.util.Arrays
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.function.ThrowingRunnable

class SupabaseTest {
    private val supabaseUrl = "ulejnivguxeiibkbpwnb.supabase.co"
    private val supabasePublicKey =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVsZWpuaXZndXhlaWlia2Jwd25iIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTA4MzgxODQsImV4cCI6MjAyNjQxNDE4NH0.9Hkj-Gox2XHcHfs_U2GyQFc9sZ_nu2Xs16-KYBri32g"
    private lateinit var supabaseClient: SupabaseClient
    private lateinit var source: SupabaseSource

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
            "e65e9435-a9f2-4474-be11-9054305f1a54",
            "b0122e3e-82ed-4409-83f9-dbfb9761db20",
            "Dummy Organizer",
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
    private val userProfile = UserProfile("b0122e3e-82ed-4409-83f9-dbfb9761db20", "Dummy User")

    @Before
    fun setUp() {
        supabaseClient =
            createSupabaseClient(supabaseUrl, supabasePublicKey) {
                install(Auth)
                install(Postgrest)
            }
        source = SupabaseSource(supabaseClient)
    }

    @Test
    fun getAssociationTest() {
        assertThrows(
            NoSuchElementException::class.java,
            ThrowingRunnable {
                runBlocking { source.getAssociation("b0122e3e-82ed-4409-83f9-dbfb9761db20") }
            }
        )
    }

    @Test
    fun setAssociationTest() {
        assertThrows(
            UnauthorizedRestException::class.java,
            ThrowingRunnable { runBlocking { source.setAssociation(association) } }
        )
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
        assertThrows(
            NoSuchElementException::class.java,
            ThrowingRunnable {
                runBlocking { source.getTag("daba142a-a276-4b7e-824d-43ca088633ff") }
            }
        )
    }

    @Test
    fun setTagTest() {
        assertThrows(
            UnauthorizedRestException::class.java,
            ThrowingRunnable { runBlocking { source.setTag(tag) } }
        )
    }

    @Test
    fun getAllTagsTest() {
        val associations = runBlocking { source.getAllTags() }
        assertNotNull(associations)
    }

    @Test
    fun getUserProfileTest() {
        assertThrows(
            NoSuchElementException::class.java,
            ThrowingRunnable {
                runBlocking { source.getUserProfile("b0122e3e-82ed-4409-83f9-dbfb9761db20") }
            }
        )
    }

    @Test
    fun setUserProfileTest() {
        assertThrows(
            UnauthorizedRestException::class.java,
            ThrowingRunnable { runBlocking { source.setUserProfile(userProfile) } }
        )
    }
}
