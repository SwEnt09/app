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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Tag

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

            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "go back",
                modifier = Modifier.size(35.dp).clickable(onClick = navBack)
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text(text = stringResource(id = R.string.profile_creation_first_name)) }
            )

            Spacer(modifier = Modifier.height(5.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text(text = stringResource(id = R.string.profile_creation_last_name)) }
            )

            Spacer(modifier = Modifier.height(5.dp))

            DropDownListFunctionWrapper(sectionList, R.string.profile_creation_section)
            Spacer(modifier = Modifier.height(5.dp))
            DropDownListFunctionWrapper(semList, R.string.profile_creation_semester)

            Spacer(modifier = Modifier.height(10.dp))

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
                SmallFloatingActionButton(
                    onClick = { addTagOnClick() },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(Icons.Default.Add, "Add tags")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(onClick = { saveOnClick() }, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.profile_creation_save_button))
            }
        }
    }
}

@Composable
fun DropDownListFunctionWrapper(elementList: List<String>, label: Int) {
    var showDropdown by rememberSaveable { mutableStateOf(false) }
    var selectedField by remember { mutableStateOf("") }
    var sectionFieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (showDropdown) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    Box() {
        OutlinedTextField(
            value = selectedField,
            onValueChange = {},
            modifier =
                Modifier.onGloballyPositioned { coordinates ->
                    sectionFieldSize = coordinates.size.toSize()
                },
            label = { Text(stringResource(id = label)) },
            trailingIcon = {
                Icon(icon, "section dropdown", Modifier.clickable { showDropdown = !showDropdown })
            }
        )

        DropdownMenu(
            properties = PopupProperties(focusable = false),
            expanded = showDropdown,
            onDismissRequest = { showDropdown = false },
            modifier =
                Modifier.align(Alignment.TopStart)
                    .heightIn(max = 200.dp)
                    .width(with(LocalDensity.current) { sectionFieldSize.width.toDp() })
        ) {
            elementList.forEach { label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        selectedField = label
                        showDropdown = false
                    }
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
