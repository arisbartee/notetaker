package com.example.notetaker

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.notetaker.data.NoteDatabase
import com.example.notetaker.data.NoteRepository
import com.example.notetaker.ui.navigation.NoteNavigation
import com.example.notetaker.ui.theme.NoteTakerTheme
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteNavigationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: NoteDatabase
    private lateinit var repository: NoteRepository
    private lateinit var viewModel: NoteViewModel

    @Before
    fun setup() {
        // Create in-memory database for testing
        database =
            Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                NoteDatabase::class.java
            ).allowMainThreadQueries().build()

        repository = NoteRepository(database.noteDao())
        viewModel = NoteViewModel(repository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testCreateNoteFlow() {
        // Launch the app
        composeTestRule.setContent {
            NoteTakerTheme {
                NoteNavigation(viewModel = viewModel)
            }
        }

        // Verify we start on the note list screen
        composeTestRule.onNodeWithText("My Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("No notes yet. Tap + to create your first note!").assertIsDisplayed()

        // Click the FAB to create a new note
        composeTestRule.onNodeWithContentDescription("Add Note").performClick()

        // Verify we navigated to the detail screen for a new note
        composeTestRule.onNodeWithText("New Note").assertIsDisplayed()

        // Enter title and content
        composeTestRule.onNodeWithText("Title").performTextInput("Test Note Title")
        composeTestRule.onNodeWithText("Content").performTextInput("This is my test note content.")

        // Save the note
        composeTestRule.onNodeWithText("Save Note").performClick()

        // Verify we're back on the list screen and the note appears
        composeTestRule.onNodeWithText("My Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Note Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("This is my test note content.").assertIsDisplayed()
    }

    @Test
    fun testNavigateToExistingNote() {
        // Pre-populate database with a test note
        runBlocking {
            repository.insertNote(
                com.example.notetaker.data.Note(
                    title = "Existing Note",
                    content = "This note already exists",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // Launch the app
        composeTestRule.setContent {
            NoteTakerTheme {
                NoteNavigation(viewModel = viewModel)
            }
        }

        // Verify the existing note is displayed
        composeTestRule.onNodeWithText("Existing Note").assertIsDisplayed()
        composeTestRule.onNodeWithText("This note already exists").assertIsDisplayed()

        // Click on the existing note to navigate to detail
        composeTestRule.onNodeWithText("Existing Note").performClick()

        // Verify we navigated to the edit screen
        composeTestRule.onNodeWithText("Edit Note").assertIsDisplayed()

        // Verify the note content is loaded
        composeTestRule.onNodeWithText("Existing Note").assertIsDisplayed()
        composeTestRule.onNodeWithText("This note already exists").assertIsDisplayed()
    }

    @Test
    fun testBackNavigationFromNewNote() {
        // Launch the app
        composeTestRule.setContent {
            NoteTakerTheme {
                NoteNavigation(viewModel = viewModel)
            }
        }

        // Navigate to create new note
        composeTestRule.onNodeWithContentDescription("Add Note").performClick()
        composeTestRule.onNodeWithText("New Note").assertIsDisplayed()

        // Enter some content
        composeTestRule.onNodeWithText("Title").performTextInput("Draft Title")
        composeTestRule.onNodeWithText("Content").performTextInput("Draft content")

        // Navigate back using the back button
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Verify we're back on the list screen and note was saved
        composeTestRule.onNodeWithText("My Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Draft Title").assertIsDisplayed()
    }

    @Test
    fun testBackNavigationFromExistingNote() {
        // Pre-populate database with a test note
        runBlocking {
            repository.insertNote(
                com.example.notetaker.data.Note(
                    title = "Original Title",
                    content = "Original content",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // Launch the app
        composeTestRule.setContent {
            NoteTakerTheme {
                NoteNavigation(viewModel = viewModel)
            }
        }

        // Click on the existing note
        composeTestRule.onNodeWithText("Original Title").performClick()

        // Edit the note
        composeTestRule.onNodeWithText("Original Title").performTextClearance()
        composeTestRule.onNodeWithText("Title").performTextInput("Updated Title")

        // Navigate back
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Verify we're back on the list screen and changes were saved
        composeTestRule.onNodeWithText("My Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Updated Title").assertIsDisplayed()
    }

    @Test
    fun testDeleteNoteFlow() {
        // Pre-populate database with a test note
        runBlocking {
            repository.insertNote(
                com.example.notetaker.data.Note(
                    title = "Note to Delete",
                    content = "This note will be deleted",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // Launch the app
        composeTestRule.setContent {
            NoteTakerTheme {
                NoteNavigation(viewModel = viewModel)
            }
        }

        // Verify note exists
        composeTestRule.onNodeWithText("Note to Delete").assertIsDisplayed()

        // Click on the note to navigate to detail
        composeTestRule.onNodeWithText("Note to Delete").performClick()

        // Delete the note
        composeTestRule.onNodeWithContentDescription("Delete").performClick()

        // Verify we're back on the list screen and note is gone
        composeTestRule.onNodeWithText("My Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Note to Delete").assertDoesNotExist()
        composeTestRule.onNodeWithText("No notes yet. Tap + to create your first note!").assertIsDisplayed()
    }

    @Test
    fun testMultipleNotesNavigation() {
        // Pre-populate database with multiple test notes
        runBlocking {
            repository.insertNote(
                com.example.notetaker.data.Note(
                    title = "First Note",
                    content = "First note content",
                    timestamp = System.currentTimeMillis()
                )
            )
            repository.insertNote(
                com.example.notetaker.data.Note(
                    title = "Second Note",
                    content = "Second note content",
                    timestamp = System.currentTimeMillis() + 1000
                )
            )
        }

        // Launch the app
        composeTestRule.setContent {
            NoteTakerTheme {
                NoteNavigation(viewModel = viewModel)
            }
        }

        // Verify both notes are displayed
        composeTestRule.onNodeWithText("First Note").assertIsDisplayed()
        composeTestRule.onNodeWithText("Second Note").assertIsDisplayed()

        // Click on first note
        composeTestRule.onNodeWithText("First Note").performClick()
        composeTestRule.onNodeWithText("Edit Note").assertIsDisplayed()
        composeTestRule.onNodeWithText("First note content").assertIsDisplayed()

        // Navigate back
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Verify we're back on list screen
        composeTestRule.onNodeWithText("My Notes").assertIsDisplayed()

        // Click on second note
        composeTestRule.onNodeWithText("Second Note").performClick()
        composeTestRule.onNodeWithText("Edit Note").assertIsDisplayed()
        composeTestRule.onNodeWithText("Second note content").assertIsDisplayed()

        // Navigate back
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Verify we're back on list screen with both notes
        composeTestRule.onNodeWithText("My Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("First Note").assertIsDisplayed()
        composeTestRule.onNodeWithText("Second Note").assertIsDisplayed()
    }

    @Test
    fun testEmptyNoteSaveFlow() {
        // Launch the app
        composeTestRule.setContent {
            NoteTakerTheme {
                NoteNavigation(viewModel = viewModel)
            }
        }

        // Navigate to create new note
        composeTestRule.onNodeWithContentDescription("Add Note").performClick()

        // Don't enter any content, just save
        composeTestRule.onNodeWithText("Save Note").performClick()

        // Verify we're back on the list screen
        composeTestRule.onNodeWithText("My Notes").assertIsDisplayed()

        // Verify an empty note was created (with "Untitled" as title)
        composeTestRule.onNodeWithText("Untitled").assertIsDisplayed()
    }

    @Test
    fun testNoteEditingFlow() {
        // Pre-populate database with a test note
        runBlocking {
            repository.insertNote(
                com.example.notetaker.data.Note(
                    title = "Edit Me",
                    content = "Original content to be edited",
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        // Launch the app
        composeTestRule.setContent {
            NoteTakerTheme {
                NoteNavigation(viewModel = viewModel)
            }
        }

        // Click on the note to edit
        composeTestRule.onNodeWithText("Edit Me").performClick()

        // Clear and update title
        composeTestRule.onNodeWithText("Edit Me").performTextClearance()
        composeTestRule.onNodeWithText("Title").performTextInput("Edited Title")

        // Clear and update content
        composeTestRule.onNodeWithText("Original content to be edited").performTextClearance()
        composeTestRule.onNodeWithText("Content").performTextInput("Updated content after editing")

        // Save the changes
        composeTestRule.onNodeWithText("Save Note").performClick()

        // Verify changes are persisted on the list screen
        composeTestRule.onNodeWithText("My Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Edited Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Updated content after editing").assertIsDisplayed()

        // Verify old content is gone
        composeTestRule.onNodeWithText("Edit Me").assertDoesNotExist()
        composeTestRule.onNodeWithText("Original content to be edited").assertDoesNotExist()
    }
}
