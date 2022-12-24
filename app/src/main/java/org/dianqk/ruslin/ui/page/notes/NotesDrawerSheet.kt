package org.dianqk.ruslin.ui.page.notes

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.dianqk.ruslin.R
import org.dianqk.ruslin.ui.component.SubTitle
import uniffi.ruslin.FfiFolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesDrawerSheet(
    folders: List<FfiFolder>,
    selectedFolder: FfiFolder?,
    onSelectedFolderChanged: (FfiFolder?) -> Unit,
    openCreateFolderDialog: Boolean,
    onCreateFolder: (String) -> Unit,
    onChangeOpenCreateFolderDialogVisible: (Boolean) -> Unit,
) {
    val createFolderTitle: MutableState<String> = remember { mutableStateOf("") }

    if (openCreateFolderDialog) {
        CreateFolderDialog(
            onDismissRequest = {
                onChangeOpenCreateFolderDialogVisible(false)
                createFolderTitle.value = ""
            },
            onConfirmRequest = {
                onChangeOpenCreateFolderDialogVisible(false)
                onCreateFolder(createFolderTitle.value)
                createFolderTitle.value = ""
            },
            title = createFolderTitle.value,
            onTitleChanged = {
                createFolderTitle.value = it
            }
        )
    }
    ModalDrawerSheet {
        Spacer(Modifier.height(12.dp))
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.Article, contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.all_notes)) },
            selected = selectedFolder == null,
            onClick = {
                onSelectedFolderChanged(null)
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        Spacer(modifier = Modifier.height(12.dp))
        SubTitle(
            modifier = Modifier.padding(horizontal = 30.dp),
            text = stringResource(id = R.string.folders)
        )
        folders.forEach { folder ->
            NavigationDrawerItem(
                icon = { Icon(Icons.Outlined.Folder, contentDescription = null) },
                label = { Text(folder.title) },
                selected = folder.id == selectedFolder?.id,
                onClick = {
                    onSelectedFolderChanged(folder)
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.CreateNewFolder, contentDescription = null) },
            label = { Text(text = stringResource(id = R.string.new_folder)) },
            selected = false,
            onClick = {
                onChangeOpenCreateFolderDialogVisible(true)
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFolderDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    title: String,
    onTitleChanged: (String) -> Unit,
    focusRequest: FocusRequester = remember { FocusRequester() }
) {
    LaunchedEffect(Unit) {
        focusRequest.requestFocus()
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { Icon(Icons.Filled.CreateNewFolder, contentDescription = null) },
        title = {
            Text(text = stringResource(id = R.string.new_folder))
        },
        text = {
            TextField(
                modifier = Modifier.focusRequester(focusRequest),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent
                ),
                value = title,
                onValueChange = onTitleChanged,
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotEmpty(),
                onClick = onConfirmRequest
            ) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}