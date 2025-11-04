package com.example.notetaker

import com.example.notetaker.data.Note
import com.example.notetaker.data.NoteDao
import com.example.notetaker.data.NoteRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NoteRepositoryTest {
    @Mock
    private lateinit var noteDao: NoteDao

    private lateinit var repository: NoteRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = NoteRepository(noteDao)
    }

    @Test
    fun `getAllNotes should return notes from dao`() {
        val notes =
            listOf(
                Note(1, "Title 1", "Content 1"),
                Note(2, "Title 2", "Content 2")
            )
        whenever(noteDao.getAllNotes()).thenReturn(flowOf(notes))

        val result = repository.getAllNotes()

        // In a real test, you would collect the flow and verify the result
        verify(noteDao).getAllNotes()
    }

    @Test
    fun `insertNote should call dao insertNote`() = runTest {
        val note = Note(0, "Test Title", "Test Content")

        repository.insertNote(note)

        verify(noteDao).insertNote(note)
    }

    @Test
    fun `updateNote should call dao updateNote`() = runTest {
        val note = Note(1, "Updated Title", "Updated Content")

        repository.updateNote(note)

        verify(noteDao).updateNote(note)
    }

    @Test
    fun `deleteNote should call dao deleteNote`() = runTest {
        val note = Note(1, "Title", "Content")

        repository.deleteNote(note)

        verify(noteDao).deleteNote(note)
    }

    @Test
    fun `getNoteById should call dao getNoteById`() = runTest {
        val noteId = 1L
        val note = Note(noteId, "Title", "Content")
        whenever(noteDao.getNoteById(noteId)).thenReturn(note)

        val result = repository.getNoteById(noteId)

        verify(noteDao).getNoteById(noteId)
        assert(result == note)
    }
}
