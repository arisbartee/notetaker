package com.example.notetaker.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.notetaker.NoteViewModel
import com.example.notetaker.ui.screens.NoteDetailScreen
import com.example.notetaker.ui.screens.NoteListScreen

const val NOTE_LIST_ROUTE = "note_list"
const val NOTE_DETAIL_ROUTE = "note_detail"
const val NOTE_DETAIL_WITH_ID_ROUTE = "note_detail/{noteId}"

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: NoteViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = NOTE_LIST_ROUTE
    ) {
        composable(NOTE_LIST_ROUTE) {
            val notes by viewModel.allNotes.collectAsState(initial = emptyList())

            NoteListScreen(
                notes = notes,
                onNoteClick = { noteId ->
                    navController.navigate("note_detail/$noteId")
                },
                onAddNote = {
                    viewModel.clearNote()
                    navController.navigate(NOTE_DETAIL_ROUTE)
                }
            )
        }

        composable(NOTE_DETAIL_ROUTE) {
            val title by viewModel.title.collectAsState()
            val content by viewModel.content.collectAsState()

            NoteDetailScreen(
                title = title,
                content = content,
                onTitleChange = viewModel::updateTitle,
                onContentChange = viewModel::updateContent,
                onSave = viewModel::saveNote,
                onDelete = viewModel::deleteNote,
                onNavigateBack = {
                    navController.popBackStack()
                },
                isNewNote = true
            )
        }

        composable(
            route = NOTE_DETAIL_WITH_ID_ROUTE,
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            val title by viewModel.title.collectAsState()
            val content by viewModel.content.collectAsState()

            LaunchedEffect(noteId) {
                viewModel.loadNote(noteId)
            }

            NoteDetailScreen(
                title = title,
                content = content,
                onTitleChange = viewModel::updateTitle,
                onContentChange = viewModel::updateContent,
                onSave = viewModel::saveNote,
                onDelete = viewModel::deleteNote,
                onNavigateBack = {
                    navController.popBackStack()
                },
                isNewNote = false
            )
        }
    }
}