# LemonQwest App – GitHub Copilot Instructions

## 📋 Project Overview

**LemonQwest** is a family task management Android app built with Clean Architecture and Domain-Driven Design (DDD). It provides a gamified token-based reward system with role-based access for Children, Caregivers, and Admins, featuring a comprehensive theme system supporting Material Design and Mario Classic themes.

## 🏗️ Architecture & Design Principles

### Core Architecture

- **Clean Architecture**: Strict separation of Domain → Infrastructure → Presentation layers
- **Domain-Driven Design (DDD)**: Aggregates (User, Task, Token, Reward, Achievement), Value Objects, Domain Events
- **SOLID Principles**: Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion
- **Repository Pattern**: Clean data access interfaces with infrastructure implementations
- **MVVM**: Modern Android architecture with ViewModels and UI state management

### Technology Stack

- **Language**: Kotlin 2.1.0
- **Java Version**: Java 21 (primary), Java 17 (fallback)
- **UI Framework**: Jetpack Compose with Material Design 3
- **Dependency Injection**: Hilt
- **Database**: Room 2.6.1 with local storage and encryption
- **Data Storage**: DataStore for preferences
- **Navigation**: Jetpack Navigation Compose
- **Testing**: JUnit 5, MockK, Turbine, Robolectric, Espresso
- **Static Analysis**: Detekt, KtLint
- **Build System**: Gradle with Version Catalog

### Project Structure

```
com.lemonqwest.app/
├── domain/           # Business logic and entities
│   ├── user/         # User aggregate and role management
│   ├── task/         # Task management and completion
│   ├── token/        # Token economy system
│   ├── reward/       # Reward system and redemption
│   └── achievement/  # Achievement system
├── infrastructure/   # Data layer implementation
│   ├── database/     # Room database, DAOs, entities
│   ├── repository/   # Repository implementations
│   └── datastore/    # DataStore preferences
├── presentation/     # UI layer with Jetpack Compose
│   ├── screens/      # Screen composables
│   ├── components/   # Reusable UI components
│   ├── theme/        # Theme system and components
│   ├── viewmodels/   # ViewModels for state management
│   └── navigation/   # Navigation setup
├── di/               # Hilt dependency injection modules
└── testutils/        # Shared testing utilities
```

## 🎨 Theme System Architecture

### User-Based Theme Selection

- **All users can select any theme**: Material Light, Material Dark, Mario Classic
- **Default**: Material Light theme for all new users
- **Persistent Storage**: Per-user theme preferences via DataStore
- **Theme-Aware Components**: All UI components adapt to selected theme
- **Semantic Mapping**: Icons and terminology change based on theme context

### Theme Implementation

```kotlin
// Theme-aware component pattern
@Composable
fun ThemeAwareTaskCard(
    task: Task,
    currentTheme: BaseAppTheme,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = currentTheme.colorScheme.surface
        ),
        shape = currentTheme.shapes.medium
    ) {
        // Theme-aware content
    }
}
```

## 🧪 Testing Architecture & Standards (Modernized - July 2025)

### Modernized Test Infrastructure ✅

All tests use the **LemonQwestTestExtension** pattern - the standardized approach for all LemonQwest tests:

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("My Feature Tests")
class MyFeatureTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    private lateinit var mockRepository: MyRepository
    private lateinit var useCase: MyUseCase

    @BeforeEach
    fun setUp() {
        mockRepository = mockk()
        useCase = MyUseCase(mockRepository)
    }

    @Test
    fun `should test feature successfully`() = runTest {
        // Given
        coEvery { mockRepository.getData() } returns Result.success(testData)

        // When
        val result = useCase.execute()

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { mockRepository.getData() }
    }
}
```

### Modernization Achievements (July 2025)

- ✅ **48 test files** modernized with `@RegisterExtension` pattern
- ✅ **Zero legacy patterns** - No manual MockK setup/teardown
- ✅ **Parallel execution enabled** - Safe concurrent test execution
- ✅ **Thread-safe infrastructure** - No race conditions in test setup

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
abstract class ViewModelTestBase {
    companion object {
        private val mainDispatcherLock = Any()
    }

    protected lateinit var testDispatcher: TestDispatcher
    protected lateinit var testScope: TestScope

    @BeforeEach
    open fun setUpViewModel() {
        synchronized(mainDispatcherLock) {
            testDispatcher = UnconfinedTestDispatcher()
            testScope = TestScope(testDispatcher)
            Dispatchers.setMain(testDispatcher)
            MockKAnnotations.init(this, relaxUnitFun = false)
            // Multiple stabilization passes for edge case timing
            testDispatcher.scheduler.advanceUntilIdle()
            testDispatcher.scheduler.advanceUntilIdle()
        }
    }

    @AfterEach
    open fun tearDownViewModel() {
        synchronized(mainDispatcherLock) {
            testScope.cancel()
            Dispatchers.resetMain()
            unmockkAll()
        }
    }

    protected fun runViewModelTest(testBody: suspend TestScope.() -> Unit) {
        testScope.runTest(timeout = 5.seconds) {
            testDispatcher.scheduler.advanceUntilIdle()
            testBody()
            advanceUntilIdle()
        }
    }
}
```

### Modern Testing Patterns (All Tests Use Same Pattern)

#### Standard Pattern for All Test Types

All tests now use the same modern `LemonQwestTestExtension` pattern:

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("Repository Tests")
class TaskRepositoryTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    private lateinit var mockDataSource: TaskDataSource
    private lateinit var repository: TaskRepository

    @BeforeEach
    fun setUp() {
        mockDataSource = mockk()
        repository = TaskRepositoryImpl(mockDataSource)
    }

    @Test
    fun `should save task successfully`() = runTest {
        // Modern test implementation
    }
}
```

#### ViewModel Tests (Modernized)

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
@DisplayName("TaskManagementViewModel Tests")
class TaskManagementViewModelTest {

    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    private lateinit var mockTaskUseCases: TaskUseCases
    private lateinit var viewModel: TaskManagementViewModel

    @BeforeEach
    override fun setUpViewModel() {
        super.setUpViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        mockTaskUseCases = mockk()
        // Setup mocks with explicit behavior
        coEvery { mockTaskUseCases.getTasksForUser(any()) } returns Result.success(emptyList())
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun createViewModel(): TaskManagementViewModel {
        testDispatcher.scheduler.advanceUntilIdle()
        return TaskManagementViewModel(
            dependencies = TaskManagementDependencies(
                taskUseCases = mockTaskUseCases,
                // ... other dependencies
            ),
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun `should load tasks successfully`() = runViewModelTest {
        viewModel = createViewModel()
        viewModel.initialize()
        advanceUntilIdle()

        // Assertions
        assertTrue(viewModel.uiState.first().tasks.isEmpty())
        coVerify(exactly = 1) { mockTaskUseCases.getTasksForUser(any()) }
    }
}
```

### ViewModel Architecture Requirements

```kotlin
@HiltViewModel
class TaskManagementViewModel @Inject constructor(
    private val dependencies: TaskManagementDependencies,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private var isInitialized = false

    // NO coroutine launches in init blocks

    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        viewModelScope.launch(ioDispatcher) {
            // Initialization logic here
        }
    }
}
```

## 🚫 Critical Anti-Patterns & Forbidden Practices

### Testing Anti-Patterns (Updated for Modern Infrastructure)

- **FORBIDDEN**: Manual `MockKAnnotations.init()` - use `LemonQwestTestExtension`
- **FORBIDDEN**: Manual `unmockkAll()` - extension handles cleanup automatically
- **FORBIDDEN**: `MainDispatcherRule` - extension provides dispatcher management
- **FORBIDDEN**: `relaxed = true` in MockK without explicit justification
- **FORBIDDEN**: Creating ViewModels in `@BeforeEach` methods
- **FORBIDDEN**: Coroutine launches in ViewModel `init` blocks
- **REQUIRED**: All new tests must use `@RegisterExtension` pattern

### Code Quality Violations

- **ZERO TOLERANCE**: Detekt violations
- **ZERO TOLERANCE**: KtLint formatting violations
- **MANDATORY**: 80%+ test coverage for domain layer
- **FORBIDDEN**: Raw Material Design components (use theme-aware components)
- **FORBIDDEN**: Business logic in Composables

## 🔧 Build & Quality Pipeline

### Mandatory Build Sequence

```bash
# All commands must pass in order
make copilot-pipeline
# OR individually:
make format             # Format code with detekt
make lint              # Run static analysis (ZERO violations required)
make build             # Build all variants
make test              # Run all tests
make install           # Install debug APK
```

### Java Version Management

- **Primary Target**: Java 21
- **Fallback**: Java 17 (for compatibility issues)
- **Gradle Configuration**:

```kotlin
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```

## 🎯 Role-Based Features & Business Logic

### User Roles & Permissions

- **Child**: View/complete tasks, earn tokens, redeem rewards, unlock achievements
- **Caregiver**: Create/manage tasks, monitor progress, manage rewards catalog
- **Admin**: Full system access, user management, system configuration

### Core Domain Models

```kotlin
// User Aggregate
data class User(
    val id: String,
    val name: String,
    val role: UserRole,
    val avatarType: AvatarType,
    val avatarData: String,
    val selectedTheme: ThemeType,
    val tokenBalance: Int = 0
)

// Task Aggregate
data class Task(
    val id: String,
    val title: String,
    val description: String,
    val category: TaskCategory,
    val difficulty: TaskDifficulty,
    val tokenReward: Int,
    val assignedToUserId: String,
    val isCompleted: Boolean = false,
    val completedAt: Instant? = null
)
```

### Domain Events

- `TaskCompletedEvent`: Triggered when a task is completed
- `TokensEarnedEvent`: Triggered when tokens are awarded
- `RewardRedeemedEvent`: Triggered when a reward is redeemed
- `AchievementUnlockedEvent`: Triggered when an achievement is unlocked

## 🛡️ Security & Child Safety

### Data Protection

- **Local Storage Only**: No external network dependencies
- **Encryption**: All sensitive data encrypted at rest
- **COPPA Compliance**: No PII collection, child-safe design
- **Input Validation**: Comprehensive validation at domain boundaries

### Authentication

- **PIN-based**: Simple PIN authentication for role switching
- **Secure Storage**: PINs hashed with BCrypt
- **Session Management**: Secure session handling with automatic logout

## ♿ Accessibility Requirements

### Mandatory Accessibility Features

- **TalkBack Support**: All interactive elements have proper content descriptions
- **Semantic Roles**: Proper semantic roles for all UI components
- **Color Contrast**: 4.5:1 minimum color contrast ratio
- **Focus Management**: Proper focus ordering and visibility
- **Large Touch Targets**: Minimum 48dp touch targets

### Accessibility Implementation

```kotlin
@Composable
fun AccessibleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .semantics {
                contentDescription = text
                role = Role.Button
            }
    ) {
        Text(text)
    }
}
```

## 📦 Dependency Management

### Core Dependencies

```toml
[versions]
kotlin = "2.1.0"
compose = "2025.01.00"
hilt = "2.51.1"
room = "2.6.1"
datastore = "1.1.1"
mockk = "1.13.8"
junit-jupiter = "5.10.1"

[libraries]
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
```

## 🚀 Development Workflow

### Code Review Requirements

1. **Architecture Compliance**: Follow DDD, SOLID, and Clean Architecture principles
2. **Test Coverage**: Maintain 80%+ domain layer coverage
3. **Quality Gates**: All Detekt/KtLint checks must pass
4. **Theme Compatibility**: All UI changes must work across all themes
5. **Accessibility**: All interactive elements must be accessible

### Documentation Standards

- Follow `/docs/DOCUMENTATION_GUIDELINES.md`
- Use emoji-prefixed headings for navigation
- Maintain cross-references between related documents
- Update Memory Bank for significant changes

## 📚 Key Documentation References

- **Architecture Guide**: `/docs/architecture.md`
- **Theme System**: `/docs/theme-system.md`
- **Testing Guide**: `/docs/testing.md`
- **Development Guide**: `/docs/development.md`
- **Memory Bank**: `/memory-bank/` for current project state
- **System Diagrams**: `/docs/diagrams/` for visual architecture

## 🎯 Success Criteria

### Build Success

- Zero Detekt violations
- Zero KtLint violations
- All tests passing (80%+ domain coverage)
- Successful build and installation
- Theme compatibility across all roles

### Code Quality

- Clean Architecture boundaries respected
- Domain logic independent of Android framework
- Comprehensive error handling
- Proper resource cleanup
- Thread-safe implementations

### User Experience

- Smooth theme transitions
- Responsive UI across screen sizes
- Full accessibility compliance
- Child-safe design patterns
- Intuitive role-based workflows

---

**Remember**: This is a family-oriented app with strict quality, safety, and accessibility requirements. Every change must maintain the high standards for child safety, data protection, and user experience across all themes and roles.
