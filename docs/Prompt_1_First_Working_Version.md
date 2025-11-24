# Android Note-Taking App - First Working Version

Assume the role of a senior Android app developer. Build a complete, functional Android application from scratch using Jetpack Compose and modern Android development best practices.

## Technical Stack
- **Language:** Kotlin 2.2.21
- **Build System:** Gradle with Kotlin DSL
- **Android SDK:** Target SDK 36, Minimum SDK 35
- **UI Framework:** Jetpack Compose with Material Design 3 (BOM 2025.10.01)
- **Navigation:** Traditional Navigation Compose (androidx.navigation:navigation-compose version 2.9.5)
- **Database:** Room 2.8.3 with KTX extensions
- **Async:** Kotlin Coroutines 1.10.2
- **Architecture:** MVVM with Repository pattern

## Architecture Requirements

### 1. Single Activity Architecture
- Use MainActivity as the single entry point
- Set up the theme and navigation host in MainActivity
- Manually inject dependencies (Database, Repository, ViewModel with Factory)

### 2. Navigation (Traditional Navigation Compose)
- Use **androidx.navigation.compose.NavHost** with string-based routes
- Define routes as constants: `NOTE_LIST_ROUTE`, `NOTE_DETAIL_ROUTE`, `NOTE_DETAIL_WITH_ID_ROUTE`
- Use `navArgument` with `NavType.LongType` for passing note IDs
- Create NavGraph.kt with composable destinations

### 3. Data Layer
**Note Entity:**
```kotlin
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
```

**NoteDao Interface:**
- `getAllNotes(): Flow<List<Note>>` - Query all notes ordered by timestamp DESC
- `getNoteById(id: Long): Note?` - Suspend function to get single note
- `insertNote(note: Note): Long` - Suspend function with REPLACE conflict strategy
- `updateNote(note: Note)` - Suspend function with UPDATE operation
- `deleteNote(note: Note)` - Suspend function with DELETE operation

**NoteDatabase:**
- Singleton pattern with @Volatile INSTANCE
- Room database with Note entity, version 1, exportSchema = false
- Context extension function for getInstance()

**NoteRepository:**
- Simple pass-through class wrapping NoteDao
- Expose same methods as DAO with consistent signatures

### 4. ViewModel Layer (State Management Approach)
**NoteViewModel should:**
- Maintain **internal state** with StateFlow for current note editing:
  - `_currentNote: MutableStateFlow<Note?>`
  - `_title: MutableStateFlow<String>`
  - `_content: MutableStateFlow<String>`
- Expose public StateFlow properties for UI observation
- Provide functions:
  - `updateTitle(newTitle: String)` - Updates title state
  - `updateContent(newContent: String)` - Updates content state
  - `loadNote(noteId: Long)` - Loads note and updates state
  - `saveNote()` - Saves using current state (insert if new, update if existing)
  - `deleteNote()` - Deletes current note and clears state
  - `clearNote()` - Resets all state to empty
- Include ViewModelFactory for dependency injection
- Use viewModelScope for coroutines

### 5. UI Layer

**NoteListScreen:**
- Display LazyColumn of notes with title and content snippet
- Floating action button to add new note
- Each note item clickable to navigate to detail
- Use Material 3 components (Card, ListItem, TopAppBar, FAB)

**NoteDetailScreen:**
- Two TextField components (title and multiline content)
- TopAppBar with back button and delete action
- Save logic triggered in LaunchedEffect when navigating back
- Use ViewModel state: `title by viewModel.title.collectAsState()`
- Call `viewModel.updateTitle()` and `viewModel.updateContent()` on text changes
- Receive `isNewNote` parameter to show/hide delete button

**Theme (Material Design 3):**
- Support both light and dark themes
- Use Material 3 color schemes
- Create Theme.kt, Color.kt, Type.kt files

### 6. Gradle Configuration
**gradle/libs.versions.toml:**
- All dependencies with versions
- Use Compose BOM 2025.10.01
- Navigation Compose 2.9.5 (NOT Navigation 3)
- Room 2.8.3, Coroutines 1.10.2
- KSP 2.2.21-2.0.4
- Include test dependencies: JUnit, Mockito, Coroutines Test, Room Testing

**app/build.gradle.kts:**
- Plugins: android.application, kotlin.android, kotlin.compose, ksp
- Compose enabled with kotlinCompilerExtensionVersion
- Bundle compose dependencies together
- Include all necessary implementation and ksp dependencies

### 7. Testing
**NoteRepositoryTest.kt:**
- Unit tests with mocked NoteDao
- Test CRUD operations
- Use Mockito and Coroutines Test

**NoteViewModelTest.kt:**
- Unit tests with mocked Repository
- Test state management (updateTitle, updateContent, loadNote)
- Test save/delete operations
- Use Coroutines Test dispatcher

### 8. Additional Files
- **CLAUDE.md:** Project documentation for AI assistants
- **.claude/settings.local.json:** Claude Code settings
- **initial_instructions.md:** Copy of these instructions
- **gradle wrapper:** Complete gradle wrapper files
- **.gitignore:** Standard Android gitignore
- **AndroidManifest.xml:** Basic manifest with MainActivity
- **strings.xml, themes.xml:** Android resources

## Key Implementation Details
1. **Navigation Flow:** NavHost uses traditional string-based routes with `navController.navigate("note_detail/$noteId")`
2. **ViewModel State:** ViewModel maintains StateFlow for title/content that UI observes
3. **Screen Updates:** LaunchedEffect(noteId) loads note data when opening detail screen
4. **Data Flow:** UI → ViewModel (updateTitle/Content) → StateFlow → UI (collectAsState)
5. **Save Pattern:** Check if `_currentNote.value` is null to decide insert vs update

## Project Structure
```
app/src/main/java/com/example/notetaker/
├── MainActivity.kt
├── NoteViewModel.kt
├── data/
│   ├── Note.kt
│   ├── NoteDao.kt
│   ├── NoteDatabase.kt
│   └── NoteRepository.kt
├── ui/
│   ├── navigation/
│   │   └── NavGraph.kt
│   ├── screens/
│   │   ├── NoteListScreen.kt
│   │   └── NoteDetailScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
```

## Expected Deliverables
Generate all files with complete, production-ready code following these exact specifications. The application should be ready to compile and run in Android Studio with minimal manual configuration.

---

**Note:** This prompt generates the first commit (9ba0f72) with traditional Navigation Compose and ViewModel state management. For the Navigation 3 conversion, see the second prompt.
