package com.github.swent.echo.compose.authentication

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.github.swent.echo.viewmodels.authentication.CreateProfileViewModel
import com.github.swent.echo.viewmodels.tag.TagViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ProfileCreationScreen(
    viewModel: CreateProfileViewModel,
    navAction: NavigationActions,
    tagviewModel: TagViewModel
) {
    var dialogVisible by remember { mutableStateOf(false) }
    ProfileCreationUI(
        sectionList = SectionEPFL.entries,
        semList = SemesterEPFL.entries,
        tagList = viewModel.tagList.value,
        onSave = viewModel::profilesave,
        onAdd = { dialogVisible = true },
        navAction = navAction,
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
@Composable
fun ProfileCreationUI(
    sectionList: List<Section>,
    semList: List<Semester>,
    tagList: List<Tag>,
    onSave: () -> Unit,
    onAdd: () -> Unit,
    navAction: NavigationActions,
) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            var firstName by remember { mutableStateOf("") }
            var lastName by remember { mutableStateOf("") }

            // Back button
            IconButton(onClick = { navAction.goBack() }, modifier = Modifier.testTag("Back")) {
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
                modifier = Modifier.testTag("FirstName"),
                label = { Text(text = stringResource(id = R.string.profile_creation_first_name)) }
            )

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                modifier = Modifier.testTag("LastName"),
                label = { Text(text = stringResource(id = R.string.profile_creation_last_name)) }
            )

            Spacer(modifier = Modifier.height(5.dp))

            // Section and semester dropdown menus
            DropDownListFunctionWrapper(sectionList, R.string.profile_creation_section)
            Spacer(modifier = Modifier.height(5.dp))
            DropDownListFunctionWrapper(semList, R.string.profile_creation_semester)

            Spacer(modifier = Modifier.height(10.dp))

            // Tags
            Text(
                stringResource(id = R.string.profile_creation_tags),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row() {
                for (tag in tagList) {
                    InputChipFun(tag.name) {}
                    Spacer(modifier = Modifier.width(5.dp))
                }
                Spacer(modifier = Modifier.width(5.dp))

                // Add tag button
                SmallFloatingActionButton(
                    onClick = { onAdd() },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.testTag("AddTag")
                ) {
                    Icon(Icons.Default.Add, "Add tags")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            // Save button
            OutlinedButton(
                onClick = { onSave() },
                modifier = Modifier.fillMaxWidth().testTag("Save")
            ) {
                Text(text = stringResource(id = R.string.profile_creation_save_button))
            }
        }
    }
}

@Composable
fun DropDownListFunctionWrapper(elementList: List<Any>, label: Int) {
    var showDropdown by rememberSaveable { mutableStateOf(false) }
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
    var enabled1 by remember { mutableStateOf(true) }
    if (!enabled1) return

    InputChip(
        selected = enabled1,
        onClick = {
            onDismiss()
            enabled1 = !enabled1
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
/*
@ExcludeFromJacocoGeneratedReport
@Preview
@Composable
fun ProfileCreationPreview() {
  ProfileCreationUI(
      sectionList = SectionEPFL.entries,
      semList = listOf(SemesterEPFL.BA1, SemesterEPFL.BA2),
      tagList = listOf(Tag("1", "Tag 1"), Tag("2", "Tag 2")),
      onSave = {},
      onAdd = {},
      navAction = NavigationActions(navController = rememberNavController()))
}

 */
