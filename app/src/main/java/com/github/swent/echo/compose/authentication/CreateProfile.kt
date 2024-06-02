package com.github.swent.echo.compose.authentication

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.Companion.isPhotoPickerAvailable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties
import androidx.core.graphics.scale
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.TagSelectionDialog
import com.github.swent.echo.data.model.Section
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.Semester
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.data.repository.RepositoryStoreWhileNoInternetException
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.authentication.CreateProfileState
import com.github.swent.echo.viewmodels.authentication.CreateProfileViewModel
import com.github.swent.echo.viewmodels.tag.TagViewModel
import kotlin.math.min
import kotlinx.coroutines.launch

/**
 * A composable function that displays the screen for creating a user profile.
 *
 * @param modifier The modifier to be applied to the layout.
 * @param viewModel The view model for creating a user profile.
 * @param navAction The navigation actions to be performed.
 * @param tagviewModel The view model for tags.
 */
@Composable
fun ProfileCreationScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateProfileViewModel,
    navAction: NavigationActions,
    tagviewModel: TagViewModel
) {
    var dialogVisible by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val semesterSelected by viewModel.selectedSemester.collectAsState()
    val sectionSelected by viewModel.selectedSection.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val picture by viewModel.picture.collectAsState()

    if (state == CreateProfileState.SAVED) {
        LaunchedEffect(state) { navAction.navigateTo(Routes.MAP) }
    }

    ProfileCreationUI(
        modifier = modifier,
        sectionList = SectionEPFL.entries,
        semList = SemesterEPFL.entries,
        tagList = viewModel.tagList.collectAsState().value,
        onSave = viewModel::profileSave,
        onAdd = { dialogVisible = true },
        tagDelete = viewModel::removeTag,
        navAction = navAction,
        firstName = firstName,
        lastName = lastName,
        selectedSec = sectionSelected?.name,
        onSecChange = { secName ->
            val section = SectionEPFL.entries.firstOrNull { it.name == secName }
            viewModel.setSelectedSection(section)
        },
        selectedSem = semesterSelected?.name,
        onSemChange = { semName ->
            val semester = SemesterEPFL.entries.firstOrNull { it.name == semName }
            viewModel.setSelectedSemester(semester)
        },
        onFirstNameChange = viewModel::setFirstName,
        onLastNameChange = viewModel::setLastName,
        isEditing = isEditing,
        isOnline = isOnline && state != CreateProfileState.SAVING,
        picture = picture,
        onPictureChange = viewModel::setPicture
    )

    if (dialogVisible) {
        TagSelectionDialog(
            onDismissRequest = { dialogVisible = false },
            tagViewModel = tagviewModel,
            onTagSelected = { tag ->
                viewModel.addTag(tag)
                dialogVisible = false
            }
        )
    }

    if (state == CreateProfileState.SAVING) {
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .testTag("saving-overlay"),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
/**
 * A composable function that displays the UI for creating a user profile.
 *
 * @param sectionList The list of sections to be displayed in the dropdown menu.
 * @param semList The list of semesters to be displayed in the dropdown menu.
 * @param tagList The list of tags to be displayed as chips.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("RestrictedApi")
@Composable
fun ProfileCreationUI(
    modifier: Modifier = Modifier.fillMaxWidth(),
    sectionList: List<Section>,
    semList: List<Semester>,
    tagList: Set<Tag>,
    onSave: (firstname: String, lastname: String) -> Unit,
    onAdd: () -> Unit,
    tagDelete: (Tag) -> Unit,
    navAction: NavigationActions,
    firstName: String,
    lastName: String,
    selectedSec: String?,
    onSecChange: (String) -> Unit,
    selectedSem: String?,
    onSemChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    isEditing: Boolean,
    isOnline: Boolean,
    picture: Bitmap?,
    onPictureChange: (newPicture: Bitmap?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val spacerHeight = 5.dp
    val spaceBetweenTags = 5.dp
    val contentPadding = 16.dp

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (isEditing) {
                        IconButton(onClick = { navAction.goBack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "go back button",
                                modifier = Modifier.testTag("Back")
                            )
                        }
                    }
                },
                title = { Text(text = stringResource(id = R.string.my_profile)) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.testTag("profile-creation-snackbar")
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier.fillMaxSize().padding(innerPadding).testTag("profile-creation"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier =
                    modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .verticalScroll(rememberScrollState())
            ) {
                Row {
                    Column {
                        // First name and last name fields
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { onFirstNameChange(it) },
                            modifier =
                                modifier
                                    // .fillMaxWidth()
                                    .testTag("FirstName"),
                            label = {
                                Text(
                                    text = stringResource(id = R.string.profile_creation_first_name)
                                )
                            },
                            singleLine = true,
                            isError = firstName.isBlank()
                        )

                        Spacer(modifier = modifier.height(spacerHeight))

                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { onLastNameChange(it) },
                            modifier =
                                modifier
                                    // .fillMaxWidth()
                                    .testTag("LastName"),
                            label = {
                                Text(
                                    text = stringResource(id = R.string.profile_creation_last_name)
                                )
                            },
                            singleLine = true,
                            isError = lastName.isBlank()
                        )
                    }
                    ProfilePictureEdit(picture, onPictureChange)
                }
                Spacer(modifier = modifier.height(spacerHeight))

                // Section and semester dropdown menus
                DropDownListFunctionWrapper(
                    sectionList,
                    R.string.section,
                    selectedSec ?: "",
                    onSecChange
                )
                Spacer(modifier = modifier.height(spacerHeight))
                DropDownListFunctionWrapper(
                    semList,
                    R.string.select_semester,
                    selectedSem ?: "",
                    onSemChange
                )

                Spacer(modifier = modifier.height(spacerHeight.times(2)))

                // Tags
                Text(
                    stringResource(id = R.string.tags),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = modifier.height(spacerHeight.times(2)))

                FlowRow(horizontalArrangement = Arrangement.spacedBy(spaceBetweenTags)) {
                    for (tag in tagList) {
                        InputChipFun(tag.name) { tagDelete(tag) }
                    }

                    // Add tag button
                    SmallFloatingActionButton(
                        onClick = { onAdd() },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = modifier.testTag("AddTag")
                    ) {
                        Icon(Icons.Default.Add, "Add tags")
                    }
                }
                Spacer(modifier = modifier.weight(1f))
                val errorLN = stringResource(R.string.profile_creation_empty_LN)
                val errorFN = stringResource(R.string.profile_creation_empty_FN)
                val errorNetwork = stringResource(R.string.profile_creation_error_network_failure)

                // Save button
                Button(
                    onClick = {
                        if (firstName.isBlank() || lastName.isBlank()) {
                            scope.launch {
                                if (firstName.isBlank()) {
                                    snackbarHostState.showSnackbar(
                                        errorFN,
                                        withDismissAction = true
                                    )
                                } else {
                                    snackbarHostState.showSnackbar(
                                        errorLN,
                                        withDismissAction = true
                                    )
                                }
                            }
                        } else {
                            try {
                                onSave(firstName, lastName)
                                navAction.navigateTo(Routes.MAP)
                            } catch (e: RepositoryStoreWhileNoInternetException) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        errorNetwork,
                                        withDismissAction = true
                                    )
                                }
                            }
                        }
                    },
                    modifier = modifier.fillMaxWidth().testTag("Save"),
                    enabled = isOnline
                ) {
                    Text(text = stringResource(id = R.string.save_button))
                }
            }
        }
    }
}

@Composable
fun DropDownListFunctionWrapper(
    elementList: List<Any>,
    label: Int,
    selectedField: String,
    onSelectedFieldChange: (String) -> Unit
) {

    var showDropdown by remember { mutableStateOf(false) }
    var selectedFieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (showDropdown) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    Column {
        Box() {
            OutlinedTextField(
                value = selectedField,
                onValueChange = {},
                modifier =
                    Modifier.onGloballyPositioned { coordinates ->
                            selectedFieldSize = coordinates.size.toSize()
                        }
                        .clickable { showDropdown = !showDropdown }
                        .fillMaxWidth()
                        .testTag(stringResource(id = label)),
                readOnly = true,
                label = { Text(stringResource(id = label)) },
                trailingIcon = {
                    Icon(icon, "list dropdown", Modifier.clickable { showDropdown = !showDropdown })
                }
            )
            DropdownMenu(
                properties = PopupProperties(focusable = false),
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                modifier =
                    Modifier.align(Alignment.TopStart)
                        .heightIn(max = 200.dp)
                        .widthIn(with(LocalDensity.current) { selectedFieldSize.width.toDp() }),
                offset = DpOffset(0.dp, 0.dp)
            ) {
                elementList.forEach { elem ->
                    DropdownMenuItem(
                        text = { Text(elem.toString()) },
                        onClick = {
                            onSelectedFieldChange(elem.toString())
                            showDropdown = false
                        },
                        modifier = Modifier.testTag(elem.toString())
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputChipFun(
    text: String,
    onDismiss: () -> Unit,
) {
    var selected by remember { mutableStateOf(true) }
    if (!selected) return

    InputChip(
        selected = selected,
        onClick = { onDismiss() },
        label = { Text(text) },
        modifier = Modifier.testTag(text),
        enabled = true,
        trailingIcon = {
            Icon(
                Icons.Default.Close,
                contentDescription = "Tags",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        }
    )
}

/**
 * Profile picture composable with edition and deletion
 *
 * @param picture: the picture to display
 * @param onPictureChange: callback called when the picture change
 */
@Composable
fun ProfilePictureEdit(picture: Bitmap?, onPictureChange: (newPicture: Bitmap?) -> Unit) {
    var showPictureDialog by remember { mutableStateOf(false) }
    val localContext = LocalContext.current
    if (!isPhotoPickerAvailable(localContext)) {
        Log.e("CreateProfile", "Photo picker not available")
    }
    var rawPicture by remember { mutableStateOf<Bitmap?>(null) }
    val pickPhotoActivity =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null) {
                Log.w("photo picker", "photo not found")
            } else {
                rawPicture =
                    BitmapFactory.decodeStream(localContext.contentResolver.openInputStream(uri))
                if (rawPicture == null) {
                    Log.w("photo picker", "cannot read the file")
                } else {
                    showPictureDialog = true
                }
            }
        }
    val pictureDisplaySize = 100.dp
    val pictureStartPadding = 5.dp
    val pictureAlpha = 0.5f
    val deleteButtonOffset = 10.dp
    Column(modifier = Modifier.padding(start = pictureStartPadding)) {
        Box {
            Image(
                modifier =
                    Modifier.size(pictureDisplaySize)
                        .clip(CircleShape)
                        .clickable {
                            pickPhotoActivity.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                        .testTag("profile-picture-image"),
                painter =
                    if (picture != null) {
                        BitmapPainter(picture!!.asImageBitmap())
                    } else {
                        painterResource(R.drawable.echologoround)
                    },
                contentDescription = "",
                alpha = pictureAlpha
            )
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = stringResource(R.string.profile_creation_edit_picture),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        IconButton(
            modifier =
                Modifier.align(Alignment.End)
                    .offset(x = deleteButtonOffset, y = -deleteButtonOffset)
                    .testTag("profile-picture-delete"),
            onClick = { onPictureChange(null) }
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = stringResource(R.string.profile_creation_delete_picture),
            )
        }
    }

    if (showPictureDialog) {
        Dialog(onDismissRequest = { showPictureDialog = false }) {
            PictureTransformer(
                picture = rawPicture!!,
                onCancel = { showPictureDialog = false },
                onConfirm = { newPicture ->
                    showPictureDialog = false
                    onPictureChange(newPicture)
                }
            )
        }
    }
}

/**
 * Picture edition dialog which allow the user to center the picture
 *
 * @param picture: the picture to center
 * @param onConfirm: callback called when the picture is centered
 * @param onCancel: callback when the action is canceled
 */
@Composable
fun PictureTransformer(
    picture: Bitmap,
    onConfirm: (picture: Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenPicture = picture.scale(screenWidth, picture.height * screenWidth / picture.width)
    val circleRadius = min(screenPicture.width, screenPicture.height) * 1 / 2
    val minMaxScale = Pair(1f, 3f)
    val maxResolution = 500 // the picture is cropped to output a square
    var scale by remember { mutableStateOf(1f) }
    val centerTextPadding = 5.dp
    val centerTextCardOffset = 20.dp
    val buttonsVerticalPadding = 15.dp

    val state = rememberTransformableState { zoomChange, _, _ ->
        val newScale = scale * zoomChange
        if (newScale > minMaxScale.first && newScale < minMaxScale.second) {
            scale = newScale
        }
    }
    Box(modifier = Modifier.fillMaxSize().testTag("profile-picture-transformer")) {
        Image(
            modifier =
                Modifier.fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        rotationZ = 0f,
                        translationX = 0f,
                        translationY = 0f
                    )
                    .transformable(state = state)
                    .testTag("profile-picture-image"),
            painter = BitmapPainter(screenPicture.asImageBitmap()),
            contentDescription = ""
        )
        val strokeSize = 10f
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.Black,
                radius = (circleRadius * 2).toFloat(),
                style = Stroke(strokeSize)
            )
        }
        Card(modifier = Modifier.align(Alignment.TopCenter).offset(y = centerTextCardOffset)) {
            Text(
                modifier = Modifier.padding(centerTextPadding),
                text = stringResource(R.string.profile_creation_center_picture),
                style = MaterialTheme.typography.titleLarge
            )
        }
        Row(
            modifier =
                Modifier.align(Alignment.BottomEnd).padding(vertical = buttonsVerticalPadding)
        ) {
            val button_Padding = 5.dp
            FilledTonalButton(
                modifier =
                    Modifier.padding(button_Padding).testTag("profile-picture-transformer-cancel"),
                onClick = onCancel
            ) {
                Text(stringResource(R.string.edit_event_screen_cancel))
            }
            FilledTonalButton(
                modifier =
                    Modifier.padding(button_Padding).testTag("profile-picture-transformer-confirm"),
                onClick = {
                    val pictureCenter = Pair((screenPicture.width / 2), (screenPicture.height / 2))
                    val scaledCircleRadius = (circleRadius / scale).toInt()
                    var newPicture =
                        Bitmap.createBitmap(
                            screenPicture,
                            pictureCenter.first - scaledCircleRadius,
                            pictureCenter.second - scaledCircleRadius,
                            2 * scaledCircleRadius,
                            2 * scaledCircleRadius
                        )
                    // reduce resolution
                    newPicture =
                        Bitmap.createScaledBitmap(newPicture, maxResolution, maxResolution, true)
                    onConfirm(newPicture)
                }
            ) {
                Text(stringResource(R.string.edit_event_screen_confirm))
            }
        }
    }
}
