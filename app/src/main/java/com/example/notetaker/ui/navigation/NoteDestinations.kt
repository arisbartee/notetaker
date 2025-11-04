package com.example.notetaker.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class NoteDestination : NavKey {
    @Serializable
    data object NoteList : NoteDestination()

    @Serializable
    data class NoteDetail(val noteId: Long = 0L) : NoteDestination()
}