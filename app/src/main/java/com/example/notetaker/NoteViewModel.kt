package com.example.notetaker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notetaker.data.Note
import com.example.notetaker.data.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    val allNotes = repository.getAllNotes()

    suspend fun getNoteById(id: Long): Note? = repository.getNoteById(id)

    suspend fun saveNote(note: Note): Long {
        return if (note.id == 0L) {
            repository.insertNote(note)
        } else {
            repository.updateNote(note.copy(timestamp = System.currentTimeMillis()))
            note.id
        }
    }

    suspend fun deleteNote(note: Note) {
        repository.deleteNote(note)
    }
}

class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}