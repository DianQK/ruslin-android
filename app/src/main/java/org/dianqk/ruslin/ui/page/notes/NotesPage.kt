package org.dianqk.ruslin.ui.page.notes

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.dianqk.ruslin.R
import org.dianqk.ruslin.ui.component.BottomDrawer
import org.dianqk.ruslin.ui.component.FilledTonalButtonWithIcon
import org.dianqk.ruslin.ui.component.OutlinedButtonWithIcon

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun NotesPage(
    viewModel: NotesViewModel = hiltViewModel(),
    navigateToNoteDetail: (parentId: String?, noteId: String?) -> Unit,
    navigateToLogin: () -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showActionBottomDrawerState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val openCreateFolderDialog = remember { mutableStateOf(false) }

    val syncAngle by rememberInfiniteTransition().animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NotesDrawerSheet(
                folders = uiState.folders,
                selectedFolder = uiState.selectedFolder,
                onSelectedFolderChanged = {
                    scope.launch { drawerState.close() }
                    viewModel.selectFolder(it)
                },
                onCreateFolder = viewModel::createFolder,
                openCreateFolderDialog = openCreateFolderDialog.value,
                onChangeOpenCreateFolderDialogVisible = {
                    openCreateFolderDialog.value = it
                },
            )
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = uiState.selectedFolder?.title
                                    ?: stringResource(id = R.string.all_notes),
//                                style = MaterialTheme.typography.titleMedium,
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, stringResource(id = R.string.desc_menu))
                            }
                        },
                        actions = {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    Icons.Default.Search,
                                    stringResource(id = R.string.desc_search)
                                )
                            }
                            IconButton(onClick = {
                                if (viewModel.syncConfigExists()) {
                                    viewModel.sync()
                                } else {
                                    navigateToLogin()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = stringResource(id = R.string.desc_sync),
                                    modifier = Modifier.rotate(if (uiState.isSyncing) syncAngle else 360f),
                                )
                            }
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    stringResource(id = R.string.desc_more)
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = { Text(stringResource(id = R.string.new_note)) },
                        icon = { Icon(Icons.Default.Edit, stringResource(id = R.string.new_note)) },
                        onClick = { navigateToNoteDetail(uiState.selectedFolder?.id, null) })
                }
            ) { innerPadding ->
                NoteList(
                    modifier = Modifier.padding(innerPadding),
                    notes = uiState.items,
                    navigateToNoteDetail = {
                        navigateToNoteDetail(uiState.selectedFolder?.id, it)
                    },
                    showActionBottomDrawer = { note ->
                        scope.launch {
                            viewModel.selectNote(note)
                            showActionBottomDrawerState.show()
                        }
                    }
                )
            }
        }
    )

    uiState.selectedNote?.let { note ->
        BottomDrawer(
            drawerState = showActionBottomDrawerState,
            sheetContent = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SelectionContainer {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            text = note.title,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp), horizontalArrangement = Arrangement.End) {
                        OutlinedButtonWithIcon(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            onClick = {
                                scope.launch { showActionBottomDrawerState.hide() }
                                viewModel.deleteNote(note.id)
                                viewModel.unselecteNote()
                            },
                            icon = Icons.Outlined.Delete,
                            text = stringResource(id = R.string.delete)
                        )
                        FilledTonalButtonWithIcon(
                            onClick = {
                                scope.launch { showActionBottomDrawerState.hide() }
                                navigateToNoteDetail(uiState.selectedFolder?.id, note.id)
                                viewModel.unselecteNote()
                            },
                            icon = Icons.Outlined.FileOpen, text = stringResource(id = R.string.open))
                    }
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadAbbrNotes()
    }
}