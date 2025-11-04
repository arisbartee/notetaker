"Assume the role of a senior Android app developer. The task is to build a complete, functional Android application from scratch using Jetpack Compose. Modern Android development best practices should be followed (e.g., MVVM architecture, Material Design 3, Kotlin coroutines, and a Room database for local storage). 

The app should be a simple Note-Taking App with the following features:
1. App Structure & UI
Single Activity Architecture: Use a single MainActivity.
Navigation: Implement navigation using Jetpack Compose Navigation between a NoteListScreen and a NoteDetailScreen.
Theme: Use Material Design 3 for all UI components, including light and dark modes. 
2. Features
View Notes: Display a list of all notes on the NoteListScreen. Each item should show the note title and a snippet of the content.
Add/Edit Notes: Allow users to create a new note or edit an existing one on the NoteDetailScreen with a title and a multi-line content field.
Save Notes: Implement a mechanism to save notes locally using a Room database.
Delete Notes: Add a button on the NoteDetailScreen to delete the current note. 
3. Technical Requirements
Data Layer: Define a Note data class, a Room NoteDao, and a NoteRepository to handle data operations.
Domain Layer (ViewModel): Use a ViewModel (NoteViewModel) to manage the UI state and interact with the repository.
Dependencies: List all necessary dependencies (Room, Navigation Compose, ViewModel, Coroutines, Material 3, etc.) in the build.gradle.kts (Module :app) file.
Code Organization: Provide all the necessary Kotlin files (MainActivity.kt, NoteViewModel.kt, Note.kt, NoteDatabase.kt, NoteRepository.kt, NavGraph.kt, NoteListScreen.kt, NoteDetailScreen.kt, and Theme.kt for styling) and their full content.
Testing: Include basic unit test stubs for the NoteViewModel and repository. 
The response should provide detailed, step-by-step instructions and the complete code for each file. Ensure the project is ready to compile and run in Android Studio with minimal manual configuration." 