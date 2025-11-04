package com.example.notetaker.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.notetaker.NoteViewModel
import com.example.notetaker.data.Note
import com.example.notetaker.ui.screens.NoteDetailScreen
import com.example.notetaker.ui.screens.NoteListScreen
import kotlinx.coroutines.launch

@Composable
fun NoteNavigation(viewModel: NoteViewModel) {
    val backStack = rememberNavBackStack(NoteDestination.NoteList)

    NavDisplay(
        backStack = backStack,
        entryProvider = { route ->
            when (route) {
                is NoteDestination.NoteList -> {
                    NavEntry(route) {
                        NoteListContent(
                            viewModel = viewModel,
                            onNavigateToDetail = { noteId ->
                                backStack += NoteDestination.NoteDetail(noteId)
                            }
                        )
                    }
                }

                is NoteDestination.NoteDetail -> {
                    NavEntry(route) {
                        NoteDetailContent(
                            viewModel = viewModel,
                            noteId = route.noteId,
                            onNavigateBack = {
                                // Safe back navigation - only remove if stack has more than 1 item
                                if (backStack.size > 1) {
                                    backStack.removeLastOrNull()
                                }
                            }
                        )
                    }
                }

                else -> {
                    NavEntry(route) {
                        // Empty fallback screen
                    }
                }
            }
        }
    )
}

@Composable
private fun NoteListContent(viewModel: NoteViewModel, onNavigateToDetail: (Long) -> Unit) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val notes by viewModel.allNotes.collectAsState(initial = emptyList())

    NoteListScreen(
        notes = notes,
        onNoteClick = { noteId ->
            try {
                onNavigateToDetail(noteId)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Failed to navigate: ${e.message}"
            }
        },
        onAddNote = {
            try {
                onNavigateToDetail(0L)
                errorMessage = null
            } catch (e: Exception) {
                errorMessage = "Failed to create new note: ${e.message}"
            }
        }
    )
}

@Composable
private fun NoteDetailContent(viewModel: NoteViewModel, noteId: Long, onNavigateBack: () -> Unit) {
    var note by remember { mutableStateOf<Note?>(null) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(noteId) {
        try {
            if (noteId != 0L) {
                note = viewModel.getNoteById(noteId)
                title = note?.title ?: ""
                content = note?.content ?: ""
            }
            errorMessage = null
        } catch (e: Exception) {
            errorMessage = "Failed to load note: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    if (!isLoading) {
        NoteDetailScreen(
            title = title,
            content = content,
            onTitleChange = { title = it },
            onContentChange = { content = it },
            onSave = {
                scope.launch {
                    try {
                        isSaving = true
                        errorMessage = null
                        val noteToSave =
                            note?.copy(
                                title = title,
                                content = content
                            ) ?: Note(
                                title = title,
                                content = content
                            )
                        viewModel.saveNote(noteToSave)
                        onNavigateBack()
                    } catch (e: Exception) {
                        errorMessage = "Failed to save note: ${e.message}"
                    } finally {
                        isSaving = false
                    }
                }
            },
            onDelete = {
                scope.launch {
                    try {
                        isSaving = true
                        errorMessage = null
                        note?.let { noteToDelete ->
                            viewModel.deleteNote(noteToDelete)
                            onNavigateBack()
                        }
                    } catch (e: Exception) {
                        errorMessage = "Failed to delete note: ${e.message}"
                    } finally {
                        isSaving = false
                    }
                }
            },
            onNavigateBack = onNavigateBack,
            isNewNote = noteId == 0L
        )
    }
}
