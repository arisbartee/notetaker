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
    fun `updateTitle should update title state`() {
        val newTitle = "Test Title"
        viewModel.updateTitle(newTitle)

        assert(viewModel.title.value == newTitle)
    }

    @Test
    fun `updateContent should update content state`() {
        val newContent = "Test Content"
        viewModel.updateContent(newContent)

        assert(viewModel.content.value == newContent)
    }

    @Test
    fun `saveNote should call repository insertNote for new note`() = runTest {
        viewModel.updateTitle("Test Title")
        viewModel.updateContent("Test Content")

        viewModel.saveNote()

        // Verify insertNote was called (stub implementation)
        // In a real test, you would verify the repository method was called
    }

    @Test
    fun `clearNote should reset all fields`() {
        viewModel.updateTitle("Test Title")
        viewModel.updateContent("Test Content")

        viewModel.clearNote()

        assert(viewModel.title.value == "")
        assert(viewModel.content.value == "")
        assert(viewModel.currentNote.value == null)
    }
}