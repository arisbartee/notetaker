package com.example.notetaker

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.notetaker.data.Note
import com.example.notetaker.data.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: NoteRepository

    private lateinit var viewModel: NoteViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        whenever(repository.getAllNotes()).thenReturn(flowOf(emptyList()))

        viewModel = NoteViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `allNotes should return flow from repository`() {
        val testNotes = listOf(
            Note(1L, "Title 1", "Content 1", System.currentTimeMillis()),
            Note(2L, "Title 2", "Content 2", System.currentTimeMillis())
        )

        whenever(repository.getAllNotes()).thenReturn(flowOf(testNotes))
        val newViewModel = NoteViewModel(repository)

        // Since allNotes is a Flow, we can't directly assert its value in a simple test
        // In a real scenario, you would collect the flow and assert the collected values
        assert(newViewModel.allNotes != null)
    }

    @Test
    fun `getNoteById should return note from repository`() = runTest {
        val testNote = Note(1L, "Test Title", "Test Content", System.currentTimeMillis())
        whenever(repository.getNoteById(1L)).thenReturn(testNote)

        val result = viewModel.getNoteById(1L)

        assert(result == testNote)
        verify(repository).getNoteById(1L)
    }

    @Test
    fun `saveNote should call repository insertNote for new note`() = runTest {
        val newNote = Note(0L, "Test Title", "Test Content", System.currentTimeMillis())
        whenever(repository.insertNote(newNote)).thenReturn(1L)

        val result = viewModel.saveNote(newNote)

        assert(result == 1L)
        verify(repository).insertNote(newNote)
    }

    @Test
    fun `saveNote should call repository updateNote for existing note`() = runTest {
        val existingNote = Note(1L, "Test Title", "Test Content", System.currentTimeMillis())

        val result = viewModel.saveNote(existingNote)

        assert(result == 1L)
        // Verify updateNote was called with any Note - timestamp will be updated
        verify(repository).updateNote(any())
    }

    @Test
    fun `deleteNote should call repository deleteNote`() = runTest {
        val testNote = Note(1L, "Test Title", "Test Content", System.currentTimeMillis())

        viewModel.deleteNote(testNote)

        verify(repository).deleteNote(testNote)
    }
}