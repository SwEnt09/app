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
import androidx.compose.material3.Button
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
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
fun ProfileCreationScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateProfileViewModel,
    navAction: NavigationActions,
    tagviewModel: TagViewModel
) {
    var dialogVisible by remember { mutableStateOf(false) }
    ProfileCreationUI(
        modifier = modifier,
        sectionList = SectionEPFL.entries,
        semList = SemesterEPFL.entries,
        tagList = viewModel.tagList.collectAsState().value,
        onSave = viewModel::profileSave,
        onAdd = { dialogVisible = true },
        tagDelete = viewModel::removeTag,
        navAction = navAction,
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
@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("RestrictedApi")
@Composable
fun ProfileCreationUI(
    modifier: Modifier = Modifier,
    sectionList: List<Section>,
    semList: List<Semester>,
    tagList: Set<Tag>,
    onSave: () -> Unit,
    onAdd: () -> Unit,
    tagDelete: (Tag) -> Unit,
    navAction: NavigationActions,
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize().padding(16.dp).testTag("profile-creation"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            var firstName by remember { mutableStateOf("") }
            var lastName by remember { mutableStateOf("") }
            var showError by remember { mutableStateOf(false) }
            var showErrorMessage by remember { mutableStateOf(0) }

            // Back button
            IconButton(onClick = { navAction.goBack() }, modifier = modifier.testTag("Back")) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "go back",
                    modifier = Modifier.size(35.dp)
                )
            }

            // First name and last name fields
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                modifier = modifier.testTag("FirstName"),
                label = { Text(text = stringResource(id = R.string.profile_creation_first_name)) },
                singleLine = true,
                isError = firstName.isBlank()
            )

            Spacer(modifier = modifier.height(5.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                modifier = modifier.testTag("LastName"),
                label = { Text(text = stringResource(id = R.string.profile_creation_last_name)) },
                singleLine = true,
                isError = lastName.isBlank()
            )

            Spacer(modifier = modifier.height(5.dp))

            // Section and semester dropdown menus
            DropDownListFunctionWrapper(sectionList, R.string.profile_creation_section)
            Spacer(modifier = modifier.height(5.dp))
            DropDownListFunctionWrapper(semList, R.string.profile_creation_semester)

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

            // Save button
            OutlinedButton(
                onClick = {
                    if (firstName.isBlank() || lastName.isBlank()) {
                        showError = true
                         showErrorMessage = if (firstName.isBlank()) {
                            ProfileCreationErrorKind.EMPTY_FIRST_NAME.errorMess
                        } else {
                            ProfileCreationErrorKind.EMPTY_LAST_NAME.errorMess
                        }
                        return@OutlinedButton
                    } else {
                        onSave()
                        navAction.navigateTo(Routes.MAP)
                    }
                },
                modifier = modifier.fillMaxWidth().testTag("Save")
            ) {
                Text(text = stringResource(id = R.string.profile_creation_save_button))
            }
            if (showError) {
                Snackbar(
                    modifier = modifier.padding(16.dp),
                    action = {
                        Button(onClick = { showError = false }) {
                            Text(stringResource(id = R.string.profile_creation_dismiss))
                        }
                    }
                ) {
                    Text(stringResource(id = showErrorMessage))
                }
            }
        }
    }
}

@Composable
fun DropDownListFunctionWrapper(elementList: List<Any>, label: Int) {
    var showDropdown by remember { mutableStateOf(false) }
    var selectedField by remember { mutableStateOf("") }
    var selectedFieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (showDropdown) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    Column {
        Box() {
            OutlinedTextField(
                value = selectedField,
                onValueChange = { selectedField = it },
                modifier =
                    Modifier.onGloballyPositioned { coordinates ->
                            selectedFieldSize = coordinates.size.toSize()
                        }
                        .clickable { showDropdown = !showDropdown }
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
                            selectedField = elem.toString()
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

enum class ProfileCreationErrorKind(val errorMess: Int) {
    EMPTY_FIRST_NAME(R.string.profile_creation_empty_FN),
    EMPTY_LAST_NAME(R.string.profile_creation_empty_LN)
}
