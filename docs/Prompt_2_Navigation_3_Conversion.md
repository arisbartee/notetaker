# Convert Android Note-Taking App to Navigation 3

You have a working Android note-taking app using traditional Navigation Compose. Convert it to use **Navigation 3 (androidx.navigation3)** with type-safe navigation.

## Migration Overview
This is a **refactoring task** - the app functionality remains the same, but navigation becomes type-safe and the ViewModel is simplified.

## Technical Changes Required

### 1. Update Dependencies

**gradle/libs.versions.toml:**
- Change `navigation = "2.9.5"` to `navigation3 = "1.0.0-alpha11"`
- Replace `androidx-navigation-compose` with:
  - `androidx-navigation3-runtime = { group = "androidx.navigation3", name = "navigation3-runtime", version.ref = "navigation3" }`
  - `androidx-navigation3-ui = { group = "androidx.navigation3", name = "navigation3-ui", version.ref = "navigation3" }`
- Add `kotlinx-serialization-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-core", version = "1.8.0" }`
- Add plugin: `jetbrains-kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }`

**app/build.gradle.kts:**
- Add plugin: `alias(libs.plugins.jetbrains.kotlin.serialization)`
- Replace Navigation Compose dependency with Navigation 3 runtime and UI libraries
- Add kotlinx.serialization.core dependency

### 2. Create Type-Safe Navigation Destinations

**Create new file: ui/navigation/NoteDestinations.kt**
```kotlin
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
```

### 3. Replace NavGraph with Navigation 3

**DELETE:** `ui/navigation/NavGraph.kt`

**CREATE:** `ui/navigation/NoteNavigation.kt` with these key changes:
- Import `androidx.navigation3.ui.NavDisplay` and `androidx.navigation3.runtime.*`
- Use `rememberNavBackStack(NoteDestination.NoteList)` for navigation state
- Use `NavDisplay` with `backStack` and `entryProvider`
- Use `when (route)` with sealed class pattern matching
- Navigate with type-safe routes: `backStack += NoteDestination.NoteDetail(noteId)`
- Back navigation: Check `if (backStack.size > 1)` then `backStack.removeLastOrNull()`
- Create private `@Composable` functions:
  - `NoteListContent()` - Wraps NoteListScreen with error handling
  - `NoteDetailContent()` - Wraps NoteDetailScreen with note loading logic

### 4. Simplify ViewModel (Remove State Management)

**Refactor NoteViewModel to:**
- **REMOVE** all StateFlow fields (_currentNote, _title, _content)
- **REMOVE** functions: updateTitle(), updateContent(), loadNote(), clearNote()
- **KEEP** `val allNotes = repository.getAllNotes()`
- **ADD** simplified suspend functions:
  - `suspend fun getNoteById(id: Long): Note?` - Direct repository call
  - `suspend fun saveNote(note: Note): Long` - Check if id==0L for insert vs update
  - `suspend fun deleteNote(note: Note)` - Direct repository call
- ViewModel no longer manages editing state - screens handle their own local state

### 5. Update Screen Implementations

**NoteListScreen:**
- No changes to screen signature
- Call site now in `NoteListContent()` which handles error states

**NoteDetailScreen:**
- **Change signature** to accept Note object directly or noteId + viewModel
- Move state management INTO the screen:
  - `var title by remember { mutableStateOf(note?.title ?: "") }`
  - `var content by remember { mutableStateOf(note?.content ?: "") }`
- Use `LaunchedEffect(noteId)` to load note if editing
- Use `rememberCoroutineScope()` for save/delete operations
- Call `viewModel.saveNote(Note(...))` with constructed Note object
- Handle back navigation after successful save/delete

**NoteDetailContent (in NoteNavigation.kt):**
- Load note with `LaunchedEffect(noteId)` and `viewModel.getNoteById()`
- Maintain `var note by remember { mutableStateOf<Note?>(null) }`
- Pass loaded note to NoteDetailScreen
- Handle save: create Note object from current values and call `viewModel.saveNote()`

### 6. Update MainActivity

**MainActivity.kt changes:**
- Change import from `NavGraph` to `NoteNavigation`
- Remove `rememberNavController()`
- Replace `NavGraph(navController, viewModel)` with `NoteNavigation(viewModel)`

### 7. Update .claude/settings.local.json
Add note about Navigation 3 usage for AI assistants

### 8. Add Comprehensive Integration Tests

**CREATE: app/src/androidTest/.../NoteNavigationTest.kt**
- Test complete navigation flows end-to-end
- Test note creation, editing, deletion through navigation
- Test back navigation behavior
- Test type-safe parameter passing
- Use in-memory Room database
- Test error scenarios

**CREATE: app/src/androidTest/.../TestRunner.kt** (if needed for test configuration)

**UPDATE: app/src/test/.../NoteViewModelTest.kt**
- Update tests to reflect new ViewModel API (suspend functions instead of state)
- Remove tests for updateTitle/updateContent/loadNote/clearNote
- Add tests for simplified suspend functions

## Key Differences Summary

| Aspect | Before (Navigation Compose) | After (Navigation 3) |
|--------|----------------------------|---------------------|
| **Navigation Type** | String-based routes | Type-safe sealed classes |
| **Navigation Control** | NavHostController | NavBackStack |
| **Route Definition** | `"note_detail/{noteId}"` | `NoteDestination.NoteDetail(noteId)` |
| **Parameter Passing** | String interpolation + navArgument | Direct property access |
| **ViewModel State** | StateFlow for title/content | No internal state, suspend functions |
| **Screen State** | From ViewModel StateFlow | Local remember { mutableStateOf() } |
| **Navigation Structure** | NavHost + composable() | NavDisplay + entryProvider |
| **Back Navigation** | navController.popBackStack() | backStack.removeLastOrNull() with size check |

## Implementation Pattern

**Navigation 3 Pattern:**
```kotlin
@Composable
fun NoteNavigation(viewModel: NoteViewModel) {
    val backStack = rememberNavBackStack(NoteDestination.NoteList)

    NavDisplay(
        backStack = backStack,
        entryProvider = { route ->
            when (route) {
                is NoteDestination.NoteList -> NavEntry(route) { /* ... */ }
                is NoteDestination.NoteDetail -> NavEntry(route) { /* ... */ }
            }
        }
    )
}
```

**State Management Pattern (moved to screens):**
```kotlin
@Composable
private fun NoteDetailContent(viewModel: NoteViewModel, noteId: Long, onNavigateBack: () -> Unit) {
    var note by remember { mutableStateOf<Note?>(null) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    LaunchedEffect(noteId) {
        if (noteId != 0L) {
            note = viewModel.getNoteById(noteId)
            // Update local state
        }
    }

    NoteDetailScreen(/* pass local state */)
}
```

## Testing Focus
- Ensure end-to-end navigation flows work correctly
- Verify type-safe parameter passing
- Test note CRUD operations through new architecture
- Verify back navigation with backStack.size checks

## Expected Deliverables
Convert the existing codebase following these specifications while maintaining all existing functionality. The refactored application should:
- Use Navigation 3 with type-safe routes
- Have simplified ViewModel without internal state management
- Maintain all existing note-taking features
- Include comprehensive integration tests
- Be ready to compile and run

---

**Note:** This prompt generates the second commit (8a379b1) which converts from traditional Navigation Compose to Navigation 3 with architectural improvements. Use this prompt only after completing the first prompt that generates the initial working version.
