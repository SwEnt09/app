package com.github.swent.echo.compose.authentication

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import com.github.swent.echo.ExcludeFromJacocoGeneratedReport
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Tag

/**
 * A composable function that displays the UI for creating a user profile.
 *
 * @param saveOnClick The callback to be invoked when the user clicks the save button.
 * @param navBack The callback to be invoked when the user clicks the back button.
 * @param addTagOnClick The callback to be invoked when the user clicks the add tag button.
 * @param sectionList The list of sections to be displayed in the dropdown menu.
 * @param semList The list of semesters to be displayed in the dropdown menu.
 * @param tagList The list of tags to be displayed as chips.
 */
@Composable
fun ProfileCreationUI(
    saveOnClick: () -> Unit,
    navBack: () -> Unit,
    addTagOnClick: () -> Unit,
    sectionList: List<String>,
    semList: List<String>,
    tagList: List<Tag>
) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            var firstName by remember { mutableStateOf("") }
            var lastName by remember { mutableStateOf("") }

            // Back button
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "go back",
                modifier = Modifier.size(35.dp).clickable(onClick = navBack).testTag("Back")
            )

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
                    onClick = { addTagOnClick() },
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
                onClick = { saveOnClick() },
                modifier = Modifier.fillMaxWidth().testTag("Save")
            ) {
                Text(text = stringResource(id = R.string.profile_creation_save_button))
            }
        }
    }
}

@Composable
fun DropDownListFunctionWrapper(elementList: List<String>, label: Int) {
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    var selectedField by remember { mutableStateOf("") }
    var selectedFieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (showDropdown) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    Box() {
        OutlinedTextField(
            value = selectedField,
            onValueChange = {},
            modifier =
                Modifier.onGloballyPositioned { coordinates ->
                        selectedFieldSize = coordinates.size.toSize()
                    }
                    .testTag(stringResource(id = label)),
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
                    .width(with(LocalDensity.current) { selectedFieldSize.width.toDp() })
        ) {
            elementList.forEach { elem ->
                DropdownMenuItem(
                    text = { Text(elem) },
                    onClick = {
                        selectedField = elem
                        showDropdown = false
                    },
                    modifier = Modifier.testTag(elem)
                )
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
    var enabled by remember { mutableStateOf(true) }
    if (!enabled) return

    InputChip(
        onClick = {
            onDismiss()
            enabled = !enabled
        },
        label = { Text(text) },
        modifier = Modifier.testTag(text),
        colors =
            InputChipDefaults.inputChipColors(
                selectedContainerColor = Color.Transparent,
            ),
        border =
            InputChipDefaults.inputChipBorder(
                selectedBorderColor = MaterialTheme.colorScheme.primary,
            ),
        selected = enabled,
        trailingIcon = {
            Icon(
                Icons.Default.Close,
                contentDescription = "Tags",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        }
    )
}

@ExcludeFromJacocoGeneratedReport
@Preview
@Composable
fun ProfileCreationPreview() {
    ProfileCreationUI(
        {},
        {},
        {},
        listOf("CS", "SC", "Math", "SV", "SIE", "Architecture"),
        listOf("Semester 1", "Semester 2"),
        listOf(Tag("1", "Tag 1"), Tag("2", "Tag 2"))
    )
}