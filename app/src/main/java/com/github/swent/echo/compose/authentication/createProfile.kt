package com.github.swent.echo.compose.authentication

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.github.swent.echo.R
import com.github.swent.echo.data.model.Tag

@Composable
fun ProfileCreationUI(saveOnClick: () -> Unit, addTagOnClick: () -> Unit, sectionList: List<String>, semList: List<String>, tagList: List<Tag>) {
  Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
      var firstName by remember { mutableStateOf("") }
      var lastName by remember { mutableStateOf("") }
      var showDropdown by remember { mutableStateOf(false) }
      var selectedSection by remember { mutableStateOf("") }
      var selectedSemester by remember { mutableStateOf("") }
      var sectionFieldSize by remember { mutableStateOf(Size.Zero) }
        var semesterFieldSize by remember { mutableStateOf(Size.Zero) }

        val icon = if (showDropdown)
            Icons.Filled.KeyboardArrowUp
        else
            Icons.Filled.KeyboardArrowDown

        OutlinedTextField(
          value = firstName,
          onValueChange = { firstName = it },
          label = { Text(text = stringResource (id = R.string.profile_creation_first_name)) })

      Spacer(modifier = Modifier.height(5.dp))

      OutlinedTextField(
          value = lastName,
          onValueChange = { lastName = it },
          label = { Text(text = stringResource (id = R.string.profile_creation_last_name)) })

      Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(
            value = selectedSection,
            onValueChange = { selectedSection = it },
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    sectionFieldSize = coordinates.size.toSize()
                },
            label = {Text(stringResource (id = R.string.profile_creation_section))},
            trailingIcon = {
                Icon(icon,"section dropdown",
                    Modifier.clickable { showDropdown = !showDropdown})
            }
        )

        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(
            value = selectedSemester,
            onValueChange = { selectedSemester = it },
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    semesterFieldSize = coordinates.size.toSize()
                },
            label = {Text(stringResource (id = R.string.profile_creation_semester))},
            trailingIcon = {
                Icon(icon,"semester dropdown",
                    Modifier.clickable { showDropdown = !showDropdown})
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(stringResource (id = R.string.profile_creation_tags), fontSize = 20.sp,
            fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(10.dp))


        for (tag in tagList){
            InputChipFun(tag.name) {}
        }

        SmallFloatingActionButton(
            onClick = { addTagOnClick() },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(Icons.Filled.Add, "Small floating action button.")
        }

        OutlinedButton(
          onClick = { saveOnClick() }, modifier = Modifier.fillMaxWidth()
          // .align(Alignment.End)
          ) {
          Text(text = stringResource (id = R.string.profile_creation_save_button))
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
        selected = enabled,
        avatar = {
            Icon(
                Icons.Filled.Person,
                contentDescription = "Localized description",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        },
        trailingIcon = {
            Icon(
                Icons.Default.Close,
                contentDescription = "Localized description",
                Modifier.size(InputChipDefaults.AvatarSize)
            )
        },
    )
}

@Preview
@Composable
fun ProfileCreationPreview() {
  ProfileCreationUI({},{}, listOf("Section 1", "Section 2"), listOf("Semester 1", "Semester 2"), listOf(Tag("1", "Tag 1"), Tag("2", "Tag 2")))
}
