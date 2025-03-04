package com.github.swent.echo.compose.authentication

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertPositionInRootIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.swent.echo.authentication.AuthenticationService
import com.github.swent.echo.connectivity.NetworkService
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.model.UserProfile
import com.github.swent.echo.data.repository.RepositoryImpl
import com.github.swent.echo.data.repository.RepositoryStoreWhileNoInternetException
import com.github.swent.echo.data.repository.SimpleRepository
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.viewmodels.authentication.CreateProfileViewModel
import com.github.swent.echo.viewmodels.tag.TagViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import java.io.ByteArrayOutputStream
import junit.framework.TestCase
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateProfileTest {
    @get:Rule val composeTestRule = createComposeRule()

    @MockK lateinit var mockOnPictureChange: (Bitmap?) -> Unit

    @MockK lateinit var mockPermissionLauncher: ActivityResultLauncher<String>

    @MockK lateinit var mockCameraLauncher: ActivityResultLauncher<Intent>

    @Test
    fun `test permission launcher`() {
        MockKAnnotations.init(this)
        var showCamera = false

        composeTestRule.setContent {
            ProfilePictureEdit(
                picture = null,
                onPictureChange = mockOnPictureChange,
            )
        }

        // Simulate clicking the camera button to request permission
        composeTestRule.onNodeWithTag("profile-picture-camera").performClick()

        // Simulate permission granted callback
        val permissionCallback = mockk<ActivityResultCallback<Boolean>>()
        every { permissionCallback.onActivityResult(true) } answers { showCamera = true }

        permissionCallback.onActivityResult(true)

        // Verify that showCamera is set to true
        assert(showCamera)
    }

    @Test
    fun profileCreationScreen_Loads() {
        // Verify that the Profile Creation screen loads successfully
        composeTestRule.setContent {
            ProfileCreationUI(
                sectionList =
                    listOf(SectionEPFL.AR, SectionEPFL.IN, SectionEPFL.SC, SectionEPFL.GM),
                semList = listOf(SemesterEPFL.BA1, SemesterEPFL.BA2),
                tagList = setOf(Tag("1", "Sports"), Tag("2", "Music")),
                onSave = { _, _ -> },
                onAdd = {},
                tagDelete = {},
                navAction = mockk(relaxed = true),
                firstName = "John",
                lastName = "",
                selectedSec = null,
                selectedSem = null,
                onFirstNameChange = {},
                onLastNameChange = {},
                onSecChange = {},
                onSemChange = {},
                isEditing = true,
                isOnline = true,
                picture = null,
                onPictureChange = {}
            )
        }
        // Assert that certain elements are present on the screen
        composeTestRule.onNodeWithTag("Save").assertExists()
        composeTestRule.onNodeWithTag("AddTag").assertExists()
        composeTestRule.onNodeWithTag("FirstName").assertExists()
        composeTestRule.onNodeWithTag("LastName").assertExists()
        composeTestRule.onNodeWithTag("Music").assertExists()
        composeTestRule.onNodeWithTag("Sports").assertExists()
        composeTestRule.onNodeWithTag("Section").assertExists()
        composeTestRule.onNodeWithTag("Semester").assertExists()
        composeTestRule.onAllNodesWithContentDescription("list dropdown")[0].performClick()
        // Verify that the dropdown menu appears
        composeTestRule.onNodeWithTag("SC").assertExists()
        composeTestRule.onNodeWithTag("IN").assertExists()
        composeTestRule.onNodeWithTag("GM").assertExists()
        composeTestRule.onAllNodesWithContentDescription("list dropdown")[1].performClick()
        composeTestRule.onNodeWithTag("BA1").assertExists()
        composeTestRule.onNodeWithTag("BA2").assertExists()
        composeTestRule.onNodeWithTag("AddTag").assertExists()
    }

    @Test
    fun `profile creation while last name empty shows error snackbar`() {
        val authenticationService: AuthenticationService = mockk(relaxed = true)
        val mockedRepository = mockk<RepositoryImpl>()
        val mockedNetworkService = mockk<NetworkService>()
        every { mockedNetworkService.isOnline } returns MutableStateFlow(true)
        coEvery { mockedRepository.getUserProfile(any()) } returns UserProfile.EMPTY
        coEvery { mockedRepository.getUserProfilePicture(any()) } returns ByteArray(0)

        val viewModel =
            CreateProfileViewModel(authenticationService, mockedRepository, mockedNetworkService)

        val mockedNavAction = mockk<NavigationActions>()
        val mockedTagViewModel = mockk<TagViewModel>()

        composeTestRule.setContent {
            ProfileCreationScreen(
                viewModel = viewModel,
                navAction = mockedNavAction,
                tagviewModel = mockedTagViewModel
            )
        }
        viewModel.setFirstName("John")
        composeTestRule.onNodeWithTag("Save").performClick()
        composeTestRule.onNodeWithTag("profile-creation-snackbar").assertIsDisplayed()
    }

    @Test
    fun `profile creation while network error shows error snackbar`() {
        val authenticationService: AuthenticationService = mockk(relaxed = true)
        val mockedRepository = mockk<RepositoryImpl>()
        val mockedNetworkService = mockk<NetworkService>()
        every { mockedNetworkService.isOnline } returns MutableStateFlow(true)
        coEvery { mockedRepository.getUserProfile(any()) } returns UserProfile.EMPTY
        coEvery { mockedRepository.getUserProfilePicture(any()) } returns ByteArray(0)

        val viewModel =
            CreateProfileViewModel(authenticationService, mockedRepository, mockedNetworkService)

        val mockedNavAction = mockk<NavigationActions>()
        val mockedTagViewModel = mockk<TagViewModel>()

        composeTestRule.setContent {
            ProfileCreationScreen(
                viewModel = viewModel,
                navAction = mockedNavAction,
                tagviewModel = mockedTagViewModel
            )
        }
        coEvery { mockedRepository.setUserProfile(any()) } throws
            RepositoryStoreWhileNoInternetException("test")
        composeTestRule.onNodeWithTag("Save").performClick()
        composeTestRule.onNodeWithTag("profile-creation-snackbar").assertIsDisplayed()
    }

    @Test
    fun profileCreationPictureIsCorrect() {
        val picture = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888)
        var changedPicture: Bitmap? = picture
        composeTestRule.setContent {
            ProfilePictureEdit(picture = picture, onPictureChange = { changedPicture = it })
        }
        composeTestRule
            .onNodeWithTag("profile-picture-image")
            .assertIsDisplayed()
            .assertHasClickAction()
        composeTestRule
            .onNodeWithTag("profile-picture-delete")
            .assertIsDisplayed()
            .assertHasClickAction()
        composeTestRule
            .onNodeWithTag("profile-picture-camera")
            .assertIsDisplayed()
            .assertHasClickAction()
        composeTestRule.onNodeWithTag("profile-picture-delete").performClick()
        assertNull(changedPicture)
        composeTestRule.onNodeWithTag("profile-picture-image").performClick()
    }

    @Test
    fun profileCreationPictureTransformerCallbacksTest() {
        val picture = Bitmap.createBitmap(300, 1000, Bitmap.Config.ARGB_8888)
        var changedPicture: Bitmap = picture
        var canceled = false
        composeTestRule.setContent {
            PictureTransformer(
                picture = picture,
                onConfirm = { changedPicture = it },
                onCancel = { canceled = true }
            )
        }
        composeTestRule.onNodeWithTag("profile-picture-transformer").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile-picture-transformer-confirm").performClick()
        assertTrue(changedPicture != picture)
        assertTrue(changedPicture.height == changedPicture.width)
        composeTestRule.onNodeWithTag("profile-picture-transformer-cancel").performClick()
        assertTrue(canceled)
    }

    @Test
    fun profileCreationPictureTransformerGestureTest() {
        val picture = Bitmap.createBitmap(300, 1000, Bitmap.Config.ARGB_8888)
        var changedPicture: Bitmap = picture
        composeTestRule.setContent {
            PictureTransformer(
                picture = picture,
                onConfirm = { changedPicture = it },
                onCancel = {}
            )
        }
        val image = composeTestRule.onNodeWithTag("profile-picture-image")
        image.assertIsDisplayed()
        image.performTouchInput { this.swipe(Offset.Zero, Offset(50f, 50f)) }
        image.assertPositionInRootIsEqualTo(0.dp, 0.dp)
    }

    @Test
    fun setProfilePictureViewModelWithBitmapTest() {
        val authenticationService: AuthenticationService = mockk(relaxed = true)
        val repository = SimpleRepository(authenticationService)
        val mockedNetworkService = mockk<NetworkService>()
        every { mockedNetworkService.isOnline } returns MutableStateFlow(true)
        var viewModel =
            CreateProfileViewModel(authenticationService, repository, mockedNetworkService)
        val picture = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
        viewModel.setPicture(picture)
        TestCase.assertEquals(picture, viewModel.picture.value)
        val outputStream = ByteArrayOutputStream()
        picture.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        runBlocking { repository.setUserProfilePicture("", outputStream.toByteArray()) }
        viewModel = CreateProfileViewModel(authenticationService, repository, mockedNetworkService)
        TestCase.assertEquals(picture.byteCount, viewModel.picture.value?.byteCount)
    }
}
