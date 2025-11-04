# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
This is an Android note-taking application built with Jetpack Compose following modern Android development practices. The app uses MVVM architecture, Material Design 3, Kotlin coroutines, and Room database for local storage.

## Key Architecture Patterns
- **Single Activity Architecture**: Uses MainActivity as the single entry point
- **MVVM Pattern**: ViewModels manage UI state and business logic, separate from UI components
- **Repository Pattern**: NoteRepository acts as single source of truth for note data
- **Room Database**: Local SQLite database with type-safe access via NoteDao
- **Jetpack Compose Navigation**: Declarative navigation between NoteListScreen and NoteDetailScreen

## Core Components
- `Note.kt`: Data class and Room entity for note objects
- `NoteDatabase.kt`: Room database configuration
- `NoteDao.kt`: Database access object with CRUD operations
- `NoteRepository.kt`: Repository handling data operations and business logic
- `NoteViewModel.kt`: ViewModel managing UI state for note operations
- `NavGraph.kt`: Navigation configuration for Compose Navigation
- `NoteListScreen.kt`: Main screen displaying list of notes
- `NoteDetailScreen.kt`: Screen for creating/editing individual notes
- `Theme.kt`: Material Design 3 theme implementation with light/dark mode support

## Development Commands
Since this is an Android project, use Android Studio or command line tools:

- **Build project**: `./gradlew build`
- **Run tests**: `./gradlew test`
- **Run on device/emulator**: `./gradlew installDebug`
- **Clean build**: `./gradlew clean`

## Key Dependencies (add to app/build.gradle.kts)
- Room database components (`androidx.room:room-runtime`, `androidx.room:room-ktx`, `androidx.room:room-compiler`)
- Jetpack Compose Navigation (`androidx.navigation:navigation-compose`)
- ViewModel and Compose integration (`androidx.lifecycle:lifecycle-viewmodel-compose`)
- Material Design 3 (`androidx.compose.material3:material3`)
- Kotlin Coroutines (`org.jetbrains.kotlinx:kotlinx-coroutines-android`)

## Testing Structure
- Unit tests for `NoteViewModel` and `NoteRepository` logic
- UI tests for Compose screens using `androidx.compose.ui:ui-test-junit4`
- Repository tests using Room's in-memory database testing utilities

## Material Design 3 Implementation
- Supports both light and dark themes
- Uses Material 3 color schemes and typography
- Consistent Material Design components throughout the app
- Dynamic color support where available (Android 12+)

## Navigation Flow
```
NoteListScreen -> NoteDetailScreen (new note)
NoteListScreen -> NoteDetailScreen (edit existing note)
NoteDetailScreen -> NoteListScreen (save/cancel/delete)
```

## Database Schema
- Single `notes` table with columns: id (primary key), title, content, timestamp
- Room handles database creation and migrations
- Repository provides coroutine-based async database operations