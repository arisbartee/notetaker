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

    private val _currentNote = MutableStateFlow<Note?>(null)
    val currentNote: StateFlow<Note?> = _currentNote.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun updateContent(newContent: String) {
        _content.value = newContent
    }

    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            val note = repository.getNoteById(noteId)
            _currentNote.value = note
            _title.value = note?.title ?: ""
            _content.value = note?.content ?: ""
        }
    }

    fun saveNote() {
        viewModelScope.launch {
            val currentNote = _currentNote.value
            if (currentNote != null) {
                val updatedNote = currentNote.copy(
                    title = _title.value,
                    content = _content.value,
                    timestamp = System.currentTimeMillis()
                )
                repository.updateNote(updatedNote)
            } else {
                val newNote = Note(
                    title = _title.value,
                    content = _content.value
                )
                repository.insertNote(newNote)
            }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            _currentNote.value?.let { note ->
                repository.deleteNote(note)
                clearNote()
            }
        }
    }

    fun clearNote() {
        _currentNote.value = null
        _title.value = ""
        _content.value = ""
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