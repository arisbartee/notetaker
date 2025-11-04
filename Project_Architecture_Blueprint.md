# Project Architecture Blueprint

**Generated on:** November 4, 2024
**Project:** NoteTaker Android Application
**Version:** 1.0
**Document Purpose:** Definitive reference for maintaining architectural consistency in the NoteTaker Android application

## Table of Contents

1. [Architecture Detection and Analysis](#1-architecture-detection-and-analysis)
2. [Architectural Overview](#2-architectural-overview)
3. [Architecture Visualization](#3-architecture-visualization)
4. [Core Architectural Components](#4-core-architectural-components)
5. [Architectural Layers and Dependencies](#5-architectural-layers-and-dependencies)
6. [Data Architecture](#6-data-architecture)
7. [Cross-Cutting Concerns Implementation](#7-cross-cutting-concerns-implementation)
8. [Service Communication Patterns](#8-service-communication-patterns)
9. [Technology-Specific Architectural Patterns](#9-technology-specific-architectural-patterns)
10. [Implementation Patterns](#10-implementation-patterns)
11. [Testing Architecture](#11-testing-architecture)
12. [Deployment Architecture](#12-deployment-architecture)
13. [Extension and Evolution Patterns](#13-extension-and-evolution-patterns)
14. [Architectural Pattern Examples](#14-architectural-pattern-examples)
15. [Architectural Decision Records](#15-architectural-decision-records)
16. [Architecture Governance](#16-architecture-governance)
17. [Blueprint for New Development](#17-blueprint-for-new-development)

---

## 1. Architecture Detection and Analysis

### Technology Stack Analysis

Based on the analysis of configuration files and source code, the application employs:

**Core Technologies:**
- **Language:** Kotlin 2.2.21
- **Build System:** Gradle with Kotlin DSL
- **Android SDK:** Target SDK 36, Minimum SDK 35
- **Kotlin Symbol Processing (KSP):** 2.2.21-2.0.4 for annotation processing

**UI Framework:**
- **Jetpack Compose:** Latest BOM 2025.10.01 for declarative UI
- **Material Design 3:** Complete theming and component system
- **Activity Compose:** 1.11.0 for Compose integration with Activities

**Navigation:**
- **Navigation 3:** Alpha version 1.0.0-alpha11 with type-safe navigation
- **Kotlinx Serialization:** 1.8.0 for type-safe route parameters

**Data Persistence:**
- **Room Database:** 2.8.3 for SQLite abstraction
- **Room KTX:** Kotlin Extensions and Coroutines support

**Reactive Programming:**
- **Kotlinx Coroutines:** 1.10.2 for asynchronous operations
- **StateFlow/Flow:** For reactive data streams

**Architecture Components:**
- **Lifecycle ViewModel:** 2.9.4 for UI-related data handling
- **Lifecycle Runtime:** 2.9.4 for lifecycle-aware components

### Architectural Pattern Detection

The codebase follows a **Clean MVVM (Model-View-ViewModel)** architectural pattern with clear separation of concerns:

1. **Repository Pattern:** Data access abstraction layer
2. **Dependency Injection:** Manual dependency injection in MainActivity
3. **Unidirectional Data Flow:** StateFlow/Flow for reactive UI updates
4. **Single Activity Architecture:** Navigation 3 for screen management

---

## 2. Architectural Overview

### Guiding Architectural Principles

1. **Separation of Concerns:** Clear boundaries between UI, business logic, and data layers
2. **Unidirectional Data Flow:** Data flows down from ViewModel to UI, events flow up from UI to ViewModel
3. **Reactive Programming:** Flow/StateFlow for asynchronous data handling
4. **Immutability:** Data classes and immutable state management
5. **Testability:** Each layer can be independently tested with clear interfaces
6. **Type Safety:** Navigation 3 and Kotlin serialization for compile-time safety

### Overall Architectural Approach

The application implements a **three-layer MVVM architecture:**

- **Presentation Layer:** Jetpack Compose UI with ViewModels
- **Domain Layer:** Implicit through ViewModel business logic
- **Data Layer:** Room database with Repository pattern

### Architectural Boundaries

- **UI Layer:** Compose screens and ViewModels
- **Data Access Layer:** Repository and Room DAO
- **Navigation Layer:** Navigation 3 destinations and routes
- **Theme Layer:** Material Design 3 theming system

---

## 3. Architecture Visualization

### High-Level System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
├─────────────────────────────────────────────────────────────┤
│  MainActivity  │  NoteListScreen  │  NoteDetailScreen  │     │
│                │                  │                    │     │
│  NoteNavigation     NoteDestinations (Type-safe Routes)     │
│                │                  │                    │     │
│           NoteViewModel (StateFlow/Flow)                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATA LAYER                             │
├─────────────────────────────────────────────────────────────┤
│              NoteRepository (Data Abstraction)              │
│                              │                              │
│                              ▼                              │
│     NoteDao (Room Interface)  │   NoteDatabase (Room DB)    │
│                              │                              │
│                    Note (Entity Model)                      │
└─────────────────────────────────────────────────────────────┘
```

### Component Interaction Diagram

```
MainActivity
    │
    ├── Creates NoteDatabase (Singleton)
    ├── Creates NoteRepository(noteDao)
    ├── Creates NoteViewModelFactory(repository)
    │
    └── Launches NoteNavigation
            │
            ├── NoteListScreen ←→ NoteViewModel
            │       │                   │
            │       └── Flow<List<Note>> ──┘
            │
            └── NoteDetailScreen ←→ NoteViewModel
                    │                   │
                    └── Note Operations ──┘
```

### Data Flow Diagram

```
UI Events (Compose)
    │
    ▼
NoteViewModel (Business Logic)
    │
    ▼
NoteRepository (Data Abstraction)
    │
    ▼
NoteDao (Room Interface)
    │
    ▼
Room Database (SQLite)

UI State ←── StateFlow/Flow ←── Repository ←── Database
```

---

## 4. Core Architectural Components

### MainActivity (Application Entry Point)

**Purpose and Responsibility:**
- Application lifecycle management
- Dependency injection setup
- Theme and navigation initialization

**Internal Structure:**
- Single Activity hosting Compose content
- Manual dependency injection pattern
- Database singleton initialization

**Interaction Patterns:**
- Creates and wires all major dependencies
- Provides ViewModel through factory pattern
- Launches main navigation flow

### NoteViewModel (Presentation Logic)

**Purpose and Responsibility:**
- Business logic orchestration
- UI state management
- Repository interaction coordination

**Internal Structure:**
- Extends ViewModel with lifecycle awareness
- Exposes Flow<List<Note>> for reactive UI updates
- Provides suspend functions for CRUD operations
- Includes ViewModelFactory for dependency injection

**Interaction Patterns:**
- Communicates with Repository through clean interface
- Exposes reactive streams to UI layer
- Handles business logic like timestamp updates
- Manages save/insert decision logic

### NoteRepository (Data Abstraction)

**Purpose and Responsibility:**
- Data access abstraction
- DAO interaction coordination
- Future data source flexibility

**Internal Structure:**
- Simple pass-through to Room DAO
- Consistent API for data operations
- Coroutine-based async operations

**Interaction Patterns:**
- Abstracts Room implementation details
- Provides clean interface to ViewModel
- Enables future multi-source data scenarios

### Navigation System (Type-Safe Navigation)

**Purpose and Responsibility:**
- Screen navigation management
- Type-safe route parameters
- Navigation state management

**Internal Structure:**
- Sealed class destinations with Kotlinx Serialization
- Navigation 3 integration
- Centralized navigation logic

**Interaction Patterns:**
- Type-safe parameter passing between screens
- Back stack management
- Deep linking support

---

## 5. Architectural Layers and Dependencies

### Layer Structure

```
┌─────────────────────────────────────────────────┐
│               PRESENTATION LAYER                │
│  - Compose UI (NoteListScreen, NoteDetailScreen) │
│  - ViewModels (NoteViewModel)                   │
│  - Navigation (NoteNavigation, NoteDestinations) │
│  - Theme (Material Design 3 Components)        │
└─────────────────────────────────────────────────┘
                        │ depends on
                        ▼
┌─────────────────────────────────────────────────┐
│                  DATA LAYER                     │
│  - Repository (NoteRepository)                  │
│  - Database (NoteDatabase, NoteDao)            │
│  - Entities (Note)                             │
└─────────────────────────────────────────────────┘
```

### Dependency Rules

1. **Presentation Layer** can depend on Data Layer
2. **Data Layer** has no dependencies on Presentation Layer
3. **UI Components** depend only on ViewModels, not directly on Repository
4. **ViewModels** depend on Repository abstraction, not concrete implementations

### Dependency Injection Pattern

**Current Implementation:**
- Manual dependency injection in MainActivity
- Factory pattern for ViewModel creation
- Singleton pattern for Database

**Abstraction Mechanisms:**
- Interface-based Repository pattern
- Room DAO interfaces
- ViewModel factory abstraction

---

## 6. Data Architecture

### Domain Model Structure

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

**Characteristics:**
- Immutable data class with Room annotations
- Auto-generated primary key
- Automatic timestamp management
- Simple flat structure for MVP

### Data Access Patterns

**Repository Pattern Implementation:**
- Single Repository class for Note operations
- Pass-through to Room DAO
- Consistent async/suspend function signatures
- Flow-based reactive queries

**Room DAO Pattern:**
```kotlin
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    // ... other CRUD operations
}
```

### Data Transformation Patterns

**ViewModel Business Logic:**
- Timestamp updates on save operations
- Insert vs. Update decision based on ID
- Copy operations for immutable updates

### Caching Strategy

- Room provides automatic SQLite caching
- Flow-based reactive updates eliminate stale data
- In-memory database option for testing

---

## 7. Cross-Cutting Concerns Implementation

### Error Handling & Resilience

**Current Implementation:**
- Try-catch blocks in Navigation components
- Error state management in UI components
- Coroutine exception handling

**Patterns:**
```kotlin
try {
    onNavigateToDetail(noteId)
    errorMessage = null
} catch (e: Exception) {
    errorMessage = "Failed to navigate: ${e.message}"
}
```

### Logging & Monitoring

**Current State:** Basic Android logging (implicit)
**Extension Points:**
- Centralized logging facade in Repository layer
- Performance monitoring in ViewModel operations
- Navigation tracking

### Validation

**Input Validation:**
- UI-level validation through Compose text field constraints
- Business logic validation in ViewModel (timestamp management)
- Database constraints through Room annotations

**Validation Patterns:**
- Empty title handling ("Untitled" fallback)
- Content length management through UI constraints

### Configuration Management

**Current Approach:**
- Build configuration through Gradle
- Theme configuration through Material Design 3
- Database configuration in Room builder

**Configuration Patterns:**
- Gradle version catalogs (libs.versions.toml)
- Environment-specific build types
- Compile-time configuration through KSP

---

## 8. Service Communication Patterns

### Internal Component Communication

**Synchronous Patterns:**
- Direct Repository calls from ViewModel
- Immediate Compose state updates

**Asynchronous Patterns:**
- Flow/StateFlow for reactive data streams
- Coroutines for database operations
- LaunchedEffect for UI lifecycle integration

**API Patterns:**
- Consistent suspend function signatures
- Flow-based query results
- Exception-based error handling

---

## 9. Technology-Specific Architectural Patterns

### Android Architectural Patterns

#### Activity/Fragment Lifecycle Management
- Single Activity architecture
- Compose manages its own lifecycle
- ViewModel survives configuration changes

#### Jetpack Compose UI Architecture
**Composable Organization:**
- Screen-level Composables for major UI sections
- Reusable component Composables (NoteItem)
- State hoisting for data management
- Unidirectional data flow

**State Management:**
```kotlin
@Composable
private fun NoteListContent(
    viewModel: NoteViewModel,
    onNavigateToDetail: (Long) -> Unit
) {
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())
    // ... UI composition
}
```

#### Navigation 3 Implementation Patterns
**Type-Safe Navigation:**
```kotlin
@Serializable
sealed class NoteDestination : NavKey {
    @Serializable
    data object NoteList : NoteDestination()

    @Serializable
    data class NoteDetail(val noteId: Long = 0L) : NoteDestination()
}
```

**Navigation Management:**
- BackStack-based navigation
- Safe back navigation with size checks
- Route-based screen composition

#### Room Database Integration
**Database Setup:**
```kotlin
@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null
        // Singleton implementation
    }
}
```

#### ViewModel and Lifecycle-Aware Components
- ViewModel factory pattern for dependency injection
- Flow-based reactive state management
- Coroutine scope tied to ViewModel lifecycle

#### Coroutines for Async Operations
- Repository operations as suspend functions
- Flow for continuous data streams
- rememberCoroutineScope for UI-triggered operations

---

## 10. Implementation Patterns

### Interface Design Patterns

**Repository Interface Pattern:**
- Repository as concrete class (simple pass-through)
- DAO as Room interface with generated implementation
- Clear separation between interface and implementation

### Service Implementation Patterns

**Repository Implementation:**
```kotlin
class NoteRepository(private val noteDao: NoteDao) {
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)
    // Consistent delegation pattern
}
```

### UI Implementation Patterns

**Composable Organization:**
- Screen Composables for top-level UI (NoteListScreen, NoteDetailScreen)
- Content Composables for business logic (NoteListContent, NoteDetailContent)
- Item Composables for reusable components (NoteItem)

**State Management Approaches:**
```kotlin
var title by remember { mutableStateOf("") }
val notes by viewModel.allNotes.collectAsState(initial = emptyList())
```

**Event Handling Patterns:**
- Lambda-based callbacks for user actions
- Coroutine launch for async operations
- Error state management with try-catch

### Domain Model Implementation

**Entity Implementation:**
- Data classes with Room annotations
- Immutable structure with copy operations
- Default parameter values for optional fields

---

## 11. Testing Architecture

### Testing Strategy Alignment

**Unit Testing (JUnit):**
- Repository layer testing with mocked DAO
- ViewModel testing with mocked Repository
- Isolated component testing

**Integration Testing (Android Test):**
- Complete navigation flow testing
- Database operations with in-memory Room
- Full user journey scenarios

**Testing Tools Integration:**
- Mockito for dependency mocking
- Coroutines Test for async testing
- Compose UI Test for interface testing
- Room in-memory database for integration tests

### Test Boundary Patterns

**Unit Test Boundaries:**
```kotlin
class NoteRepositoryTest {
    @Mock private lateinit var noteDao: NoteDao
    private lateinit var repository: NoteRepository

    // Tests verify Repository behavior without database
}
```

**Integration Test Boundaries:**
```kotlin
class NoteNavigationTest {
    private lateinit var database: NoteDatabase  // In-memory
    private lateinit var repository: NoteRepository
    private lateinit var viewModel: NoteViewModel

    // Tests complete user workflows
}
```

### Test Data Strategies

- Mock objects for unit testing
- In-memory Room database for integration testing
- Real user interaction simulation in UI tests
- Coroutine test dispatchers for async testing

---

## 12. Deployment Architecture

### Build Configuration

**Gradle Configuration:**
- Kotlin DSL for build scripts
- Version catalogs for dependency management
- KSP for annotation processing
- Multiple build types (debug/release)

**Environment Configuration:**
- Debug and Release build variants
- ProGuard configuration for release builds
- Test-specific configurations

**Android Specific Deployment:**
- Single APK targeting Android 12+ (API 35+)
- Material Design 3 theming
- No external service dependencies

---

## 13. Extension and Evolution Patterns

### Feature Addition Patterns

**Adding New Note Operations:**
1. Add suspend function to NoteDao interface
2. Add corresponding function to NoteRepository
3. Add business logic to NoteViewModel
4. Update UI components to use new functionality

**Adding New Screens:**
1. Create new Destination in NoteDestinations
2. Add route handling in NoteNavigation
3. Create new Screen Composable
4. Update navigation calls in existing screens

**Configuration Extension:**
1. Add new dependencies to libs.versions.toml
2. Update build.gradle.kts dependencies
3. Implement new features following existing patterns

### Modification Patterns

**Safe Component Modification:**
- Follow existing interface contracts
- Maintain backward compatibility in ViewModel APIs
- Use Flow/StateFlow for state management
- Preserve Room database schema versioning

**Database Migration:**
```kotlin
@Database(
    entities = [Note::class],
    version = 2,  // Increment version
    exportSchema = false
)
```

### Integration Patterns

**Adding External Data Sources:**
1. Expand Repository to handle multiple sources
2. Add network layer with Retrofit/OkHttp
3. Implement caching strategies
4. Add sync functionality

**Adding Dependency Injection Framework:**
1. Replace manual injection with Hilt/Dagger
2. Add @Module and @Component interfaces
3. Update ViewModel creation patterns
4. Maintain existing component boundaries

---

## 14. Architectural Pattern Examples

### Layer Separation Examples

**Repository Pattern Implementation:**
```kotlin
// Clean interface separation
class NoteRepository(private val noteDao: NoteDao) {
    // Repository abstracts Room implementation
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)
}

// ViewModel depends on Repository, not DAO directly
class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    val allNotes = repository.getAllNotes()

    suspend fun saveNote(note: Note): Long {
        return if (note.id == 0L) {
            repository.insertNote(note)
        } else {
            repository.updateNote(note.copy(timestamp = System.currentTimeMillis()))
            note.id
        }
    }
}
```

### Component Communication Examples

**Reactive Data Flow:**
```kotlin
// ViewModel exposes Flow
class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    val allNotes = repository.getAllNotes()  // Flow<List<Note>>
}

// UI collects Flow and updates automatically
@Composable
private fun NoteListContent(viewModel: NoteViewModel, /* ... */) {
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())

    NoteListScreen(
        notes = notes,
        onNoteClick = onNavigateToDetail,
        onAddNote = { onNavigateToDetail(0L) }
    )
}
```

**Navigation Communication:**
```kotlin
// Type-safe navigation with parameters
@Serializable
data class NoteDetail(val noteId: Long = 0L) : NoteDestination()

// Navigation between screens
backStack += NoteDestination.NoteDetail(noteId)

// Parameter extraction in destination
is NoteDestination.NoteDetail -> {
    NavEntry(route) {
        NoteDetailContent(
            viewModel = viewModel,
            noteId = route.noteId,  // Type-safe parameter access
            onNavigateBack = { /* ... */ }
        )
    }
}
```

### Extension Point Examples

**Adding New Fields to Note:**
```kotlin
// 1. Update Entity
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "General"  // New field
)

// 2. Update Database version and add migration
@Database(
    entities = [Note::class],
    version = 2,
    exportSchema = false
)

// 3. DAO automatically supports new field
// 4. Repository passes through unchanged
// 5. ViewModel business logic can use new field
// 6. UI can display and edit new field
```

---

## 15. Architectural Decision Records

### Architectural Style Decisions

**Decision: Clean MVVM Architecture**
- **Context:** Need for clear separation between UI, business logic, and data
- **Factors:** Android Architecture Components support, testability, maintainability
- **Consequences:** Clear boundaries, easy testing, some boilerplate code
- **Alternatives:** MVI, MVP patterns were considered but MVVM better suits Compose

**Decision: Single Activity Architecture**
- **Context:** Navigation 3 supports single Activity pattern
- **Factors:** Simplified lifecycle management, better Compose integration
- **Consequences:** All navigation handled by Compose, reduced Activity overhead
- **Future Flexibility:** Easy to split Activities if needed

### Technology Selection Decisions

**Decision: Navigation 3 Alpha**
- **Context:** Need for type-safe navigation with Compose
- **Factors:** Type safety, Kotlinx Serialization integration, future-proof
- **Consequences:** Using alpha version, potential API changes
- **Alternatives:** Navigation Compose was considered but lacks type safety

**Decision: Room Database**
- **Context:** Need for local data persistence
- **Factors:** SQLite abstraction, Coroutines support, Android Architecture Components
- **Consequences:** Excellent Android integration, compile-time SQL verification
- **Alternatives:** SQL Delight considered but Room better suited for MVP

**Decision: Manual Dependency Injection**
- **Context:** Simple application with few dependencies
- **Factors:** Learning curve, application complexity, build time
- **Consequences:** Simple setup, manual wiring, easy testing
- **Future Migration:** Can easily migrate to Hilt when complexity increases

### Implementation Approach Decisions

**Decision: Flow-based Reactive UI**
- **Context:** Need for reactive UI updates
- **Factors:** Compose StateFlow integration, data consistency
- **Consequences:** Automatic UI updates, minimal boilerplate
- **Alternatives:** LiveData considered but Flow better suited for Compose

**Decision: Repository Pattern**
- **Context:** Abstract data access from ViewModel
- **Factors:** Future flexibility, testing, separation of concerns
- **Consequences:** Additional layer, easy to swap data sources
- **Benefits:** Clean ViewModel APIs, testable components

---

## 16. Architecture Governance

### Architectural Consistency Maintenance

**Code Organization Standards:**
- Package by layer (ui/, data/) and by feature within layers
- Consistent file naming (Screen, ViewModel, Repository, Dao suffixes)
- Clear interface segregation patterns

**Automated Checks:**
- KSP for compile-time annotation processing
- Gradle dependency management through version catalogs
- ProGuard rules for release optimization

**Documentation Practices:**
- Inline code documentation for public APIs
- Clear function naming following Kotlin conventions
- Architecture decision documentation in this blueprint

### Review Processes

**Testing Requirements:**
- Unit tests for Repository and ViewModel layers
- Integration tests for navigation flows
- UI tests for complete user journeys

**Build Verification:**
- Gradle build verification
- Test execution in CI pipeline
- ProGuard verification for release builds

---

## 17. Blueprint for New Development

### Development Workflow

**For New Note-Related Features:**
1. Start with data layer: Add DAO methods if needed
2. Update Repository with new operations
3. Add business logic to ViewModel
4. Update or create UI components
5. Add navigation routes if new screens needed
6. Write unit and integration tests

**For New Screens:**
1. Create new Destination in NoteDestinations.kt
2. Add route handling in NoteNavigation.kt
3. Create Screen Composable in ui/screens/
4. Implement navigation calls in existing screens
5. Add integration tests for navigation flow

**For New Data Fields:**
1. Update Note entity with new fields
2. Update database version and create migration
3. Update UI forms to handle new fields
4. Add validation if needed
5. Update tests with new field scenarios

### Implementation Templates

**New Screen Composable Template:**
```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewScreen(
    // State parameters
    onAction: () -> Unit,  // Action callbacks
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Screen Title") })
        }
    ) { paddingValues ->
        // Screen content
    }
}
```

**New ViewModel Function Template:**
```kotlin
suspend fun newOperation(parameter: Type): Result {
    return try {
        repository.newOperation(parameter)
    } catch (e: Exception) {
        // Handle error appropriately
        throw e
    }
}
```

**New Repository Function Template:**
```kotlin
suspend fun newOperation(parameter: Type): Result =
    noteDao.newOperation(parameter)
```

### Common Pitfalls to Avoid

**Architecture Violations:**
- Don't access Room DAO directly from ViewModel
- Don't put UI logic in Repository
- Don't bypass ViewModel from UI components
- Don't forget to handle coroutine exceptions

**Performance Considerations:**
- Use Flow.collectAsState() for reactive UI updates
- Avoid blocking operations on Main thread
- Use remember for expensive computations in Compose
- Consider LazyColumn for large data sets

**Testing Blind Spots:**
- Always test navigation flows end-to-end
- Mock Repository for ViewModel tests
- Use in-memory database for integration tests
- Test error handling paths

### Keeping This Blueprint Updated

**Regular Updates Recommended:**
- After major feature additions
- When architectural patterns change
- After technology upgrades (Navigation 3 stable release)
- Quarterly architecture reviews

**Update Triggers:**
- Adding new architectural layers
- Integrating external dependencies
- Changing navigation patterns
- Adding dependency injection framework

---

**End of Document**

*This blueprint serves as the definitive reference for maintaining architectural consistency in the NoteTaker Android application. Follow these patterns and principles to ensure clean, maintainable, and scalable code.*