package com.github.swent.echo.compose.authentication

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import com.github.swent.echo.R
import com.github.swent.echo.compose.components.TagSelectionDialog
import com.github.swent.echo.data.model.Section
import com.github.swent.echo.data.model.SectionEPFL
import com.github.swent.echo.data.model.Semester
import com.github.swent.echo.data.model.SemesterEPFL
import com.github.swent.echo.data.model.Tag
import com.github.swent.echo.ui.navigation.NavigationActions
import com.github.swent.echo.ui.navigation.Routes
import com.github.swent.echo.viewmodels.authentication.CreateProfileViewModel
import com.github.swent.echo.viewmodels.tag.TagViewModel
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
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val semesterSelected by viewModel.selectedSemester.collectAsState()
    val sectionSelected by viewModel.selectedSection.collectAsState()

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
        onLastNameChange = viewModel::setLastName
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
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navAction.goBack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "go back button",
                            modifier = Modifier.testTag("Back")
                        )
                    }
                },
                title = { Text(text = "Create Profile") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = modifier.fillMaxSize().padding(innerPadding).testTag("profile-creation"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier =
                    modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
            ) {
                // First name and last name fields
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { onFirstNameChange(it) },
                    modifier = modifier.fillMaxWidth().testTag("FirstName"),
                    label = {
                        Text(text = stringResource(id = R.string.profile_creation_first_name))
                    },
                    singleLine = true,
                    isError = firstName.isBlank()
                )

                Spacer(modifier = modifier.height(5.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { onLastNameChange(it) },
                    modifier = modifier.fillMaxWidth().testTag("LastName"),
                    label = {
                        Text(text = stringResource(id = R.string.profile_creation_last_name))
                    },
                    singleLine = true,
                    isError = lastName.isBlank()
                )

                Spacer(modifier = modifier.height(5.dp))

                // Section and semester dropdown menus
                DropDownListFunctionWrapper(
                    sectionList,
                    R.string.profile_creation_section,
                    selectedSec ?: "",
                    onSecChange
                )
                Spacer(modifier = modifier.height(5.dp))
                DropDownListFunctionWrapper(
                    semList,
                    R.string.profile_creation_semester,
                    selectedSem ?: "",
                    onSemChange
                )

                Spacer(modifier = modifier.height(10.dp))

                // Tags
                Text(
                    stringResource(id = R.string.profile_creation_tags),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = modifier.height(10.dp))

                FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    for (tag in tagList) {
                        InputChipFun(tag.name) { tagDelete(tag) }
                    }

                    // Add tag button
                    SmallFloatingActionButton(
                        onClick = { onAdd() },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.secondary,
                        modifier = modifier.testTag("AddTag")
                    ) {
                        Icon(Icons.Default.Add, "Add tags")
                    }
                }
                Spacer(modifier = modifier.weight(1f))
                val errorLN = stringResource(R.string.profile_creation_empty_LN)
                val errorFN = stringResource(R.string.profile_creation_empty_FN)

                // Save button
                OutlinedButton(
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
                            //  return@OutlinedButton
                        } else {
                            onSave(firstName, lastName)
                            navAction.navigateTo(Routes.MAP)
                        }
                    },
                    modifier = modifier.fillMaxWidth().testTag("Save")
                ) {
                    Text(text = stringResource(id = R.string.profile_creation_save_button))
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
                            // selectedField = elem.toString()
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
        onClick = {
            onDismiss()
            selected = !selected
        },
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
