# Testing Guide

[🏠 Back to Main README](../README.md)

Comprehensive guide to testing strategies, practices, and implementation for Arthur's Life Android Kotlin application.

## 📋 Page Navigation

| Section | Description |
|---------|-------------|
| [Testing Philosophy](#testing-philosophy) | Our approach to testing |
| [Testing Strategy](#testing-strategy) | Testing pyramid and coverage |
| [Unit Testing](#unit-testing) | Domain and service layer tests |
| [Integration Testing](#integration-testing) | Repository and database tests |
| [UI Testing](#ui-testing) | Compose UI testing |
| [Testing Tools](#testing-tools) | Frameworks and utilities |

## 🔗 Related Documentation

| Topic | Link |
|-------|------|
| **Setup Guide** | [getting-started.md](getting-started.md) |
| **Contributing** | [contributing.md](contributing.md) |
| **Architecture** | [architecture.md](architecture.md) |
| **Development** | [development.md](development.md) |

## Testing Philosophy

Arthur's Life app prioritizes reliability and maintainability, making comprehensive testing essential. Our testing strategy ensures:

- **Business Logic Integrity**: Domain services and use cases work correctly
- **Token Economy Accuracy**: Precise calculations for token earnings and spending
- **Child Safety**: Secure authentication and data protection
- **Accessibility Compliance**: TalkBack and accessibility features function properly
- **Offline Functionality**: App works reliably without internet connection
- **Code Quality**: Maintainable codebase through comprehensive test coverage

## Testing Strategy

### Testing Pyramid

```
                    /\
                   /  \
                  / E2E \
                 /______\
                /        \
               /Integration\
              /____________\
             /              \
            /   Unit Tests    \
           /________________\
```

### Coverage Requirements
- **Domain Layer**: 80%+ unit test coverage (business logic critical)
- **Use Cases**: 100% coverage for business operations
- **ViewModels**: Test state changes and error handling
- **UI Components**: Test user interactions and accessibility
- **Integration**: Test database operations and repository implementations

### Testing Levels

#### Unit Tests (Foundation)
- **Domain Entities**: Business rule validation and state changes
- **Use Cases**: Business logic and error scenarios
- **Utilities**: Helper functions and extensions
- **Value Objects**: Immutability and validation

#### Integration Tests (Middle)
- **Repository Implementations**: Data access and mapping
- **Database Operations**: Room DAO and entity mapping
- **Use Case Orchestration**: Cross-service interactions

#### End-to-End Tests (Top)
- **User Workflows**: Complete task completion flows
- **Role Switching**: Authentication and permission validation
- **Data Persistence**: Cross-session data integrity

## Testing Tools

### Core Testing Framework

- **JUnit 5** - Modern testing framework for Kotlin
- **MockK** - Mocking library for Kotlin
- **Turbine** - Testing library for Kotlin Flow
- **Robolectric** - Android unit testing framework
- **Espresso** - UI testing framework for Android
- **Compose Testing** - Jetpack Compose UI testing utilities

### Additional Tools

- **Detekt** - Static code analysis for Kotlin
- **KtLint** - Code formatting and style checking
- **Truth** - Fluent assertions library
- **Hilt Testing** - Dependency injection testing utilities

## Unit Testing

### Domain Entity Testing

```kotlin
// src/test/java/com/arthurslife/app/domain/task/TaskTest.kt
class TaskTest {
    @Test
    fun `complete task should mark as completed and return tokens`() {
        // Given
        val task = Task(
            id = "task-123",
            title = "Brush teeth",
            difficulty = TaskDifficulty.MEDIUM,
            tokenValue = 10,
            isCompleted = false
        )
        
        // When
        val result = task.complete()
        
        // Then
        assertThat(result.isCompleted).isTrue()
        assertThat(result.tokenValue).isEqualTo(10)
        assertThat(task.isCompleted).isFalse() // Original unchanged
    }
    
    @Test
    fun `complete already completed task should throw exception`() {
        // Given
        val completedTask = Task(
            id = "task-123",
            title = "Brush teeth",
            difficulty = TaskDifficulty.MEDIUM,
            tokenValue = 10,
            isCompleted = true
        )
        
        // When & Then
        assertThrows<IllegalStateException> {
            completedTask.complete()
        }
    }
}
```

### Use Case Testing

```kotlin
// src/test/java/com/arthurslife/app/domain/task/CompleteTaskUseCaseTest.kt
class CompleteTaskUseCaseTest {
    @MockK
    private lateinit var taskRepository: TaskRepository
    
    @MockK
    private lateinit var tokenService: TokenService
    
    private lateinit var useCase: CompleteTaskUseCase
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = CompleteTaskUseCase(taskRepository, tokenService)
    }
    
    @Test
    fun `complete task should save completed task and award tokens`() = runTest {
        // Given
        val task = Task(
            id = "task-123",
            title = "Brush teeth",
            difficulty = TaskDifficulty.MEDIUM,
            tokenValue = 10,
            isCompleted = false
        )
        val userId = "user-456"
        
        coEvery { taskRepository.findById("task-123") } returns task
        coEvery { taskRepository.save(any()) } returns Result.success(task.complete())
        coEvery { tokenService.awardTokens(userId, 10) } returns Result.success(Unit)
        
        // When
        val result = useCase.execute("task-123", userId)
        
        // Then
        assertThat(result.isSuccess).isTrue()
        coVerify { taskRepository.save(match { it.isCompleted }) }
        coVerify { tokenService.awardTokens(userId, 10) }
    }
}
```

### Value Object Testing

```kotlin
// src/test/java/com/arthurslife/app/domain/user/UserRoleTest.kt
class UserRoleTest {
    @Test
    fun `child role should have correct permissions`() {
        // Given
        val childRole = UserRole.CHILD
        
        // Then
        assertThat(childRole.canCreateTasks).isFalse()
        assertThat(childRole.canCompleteTasks).isTrue()
        assertThat(childRole.canViewRewards).isTrue()
        assertThat(childRole.canManageUsers).isFalse()
    }
    
    @Test
    fun `caregiver role should have management permissions`() {
        // Given
        val caregiverRole = UserRole.CAREGIVER
        
        // Then
        assertThat(caregiverRole.canCreateTasks).isTrue()
        assertThat(caregiverRole.canCompleteTasks).isFalse()
        assertThat(caregiverRole.canViewRewards).isTrue()
        assertThat(caregiverRole.canManageUsers).isTrue()
    }
}
```

## Integration Testing

### Repository Testing

```kotlin
// src/test/java/com/arthurslife/app/infrastructure/database/TaskRepositoryImplTest.kt
@RunWith(AndroidJUnit4::class)
class TaskRepositoryImplTest {
    
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var repository: TaskRepositoryImpl
    
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        taskDao = database.taskDao()
        repository = TaskRepositoryImpl(taskDao, TaskMapper())
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun `save task should persist to database`() = runTest {
        // Given
        val task = Task(
            id = "task-123",
            title = "Brush teeth",
            difficulty = TaskDifficulty.MEDIUM,
            tokenValue = 10,
            isCompleted = false
        )
        
        // When
        val result = repository.save(task)
        
        // Then
        assertThat(result.isSuccess).isTrue()
        val savedTask = repository.findById("task-123")
        assertThat(savedTask).isNotNull()
        assertThat(savedTask?.title).isEqualTo("Brush teeth")
    }
}
```

### Database Migration Testing

```kotlin
// src/test/java/com/arthurslife/app/infrastructure/database/MigrationTest.kt
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    
    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        listOf(Migration1to2())
    )
    
    @Test
    fun `migration from 1 to 2 should add token_value column`() {
        // Given
        val db = helper.createDatabase(TEST_DB, 1)
        
        // Insert data in version 1 format
        db.execSQL("INSERT INTO tasks (id, title, difficulty, is_completed) VALUES ('1', 'Test', 'MEDIUM', 0)")
        db.close()
        
        // When
        val migratedDb = helper.runMigrationsAndValidate(TEST_DB, 2, true, Migration1to2())
        
        // Then
        val cursor = migratedDb.query("SELECT * FROM tasks WHERE id = '1'")
        assertThat(cursor.moveToFirst()).isTrue()
        assertThat(cursor.getColumnIndex("token_value")).isGreaterThan(-1)
        cursor.close()
    }
    
    companion object {
        private const val TEST_DB = "migration-test"
    }
}
```

## UI Testing

### Compose UI Testing

```kotlin
// src/test/java/com/arthurslife/app/presentation/screens/TaskListScreenTest.kt
@RunWith(AndroidJUnit4::class)
class TaskListScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun `should display task list with correct accessibility labels`() {
        // Given
        val tasks = listOf(
            Task(
                id = "task-1",
                title = "Brush teeth",
                difficulty = TaskDifficulty.MEDIUM,
                tokenValue = 10,
                isCompleted = false
            )
        )
        
        // When
        composeTestRule.setContent {
            TaskListScreen(
                tasks = tasks,
                onTaskComplete = {}
            )
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Brush teeth")
            .assertIsDisplayed()
            .assertHasClickAction()
        
        composeTestRule
            .onNodeWithContentDescription("Complete task: Brush teeth to earn 10 tokens")
            .assertIsDisplayed()
    }
    
    @Test
    fun `should handle task completion correctly`() {
        // Given
        val tasks = listOf(
            Task(
                id = "task-1",
                title = "Brush teeth",
                difficulty = TaskDifficulty.MEDIUM,
                tokenValue = 10,
                isCompleted = false
            )
        )
        var completedTaskId: String? = null
        
        // When
        composeTestRule.setContent {
            TaskListScreen(
                tasks = tasks,
                onTaskComplete = { taskId -> completedTaskId = taskId }
            )
        }
        
        composeTestRule
            .onNodeWithContentDescription("Complete task: Brush teeth to earn 10 tokens")
            .performClick()
        
        // Then
        assertThat(completedTaskId).isEqualTo("task-1")
    }
}
```

### Accessibility Testing

```kotlin
// src/test/java/com/arthurslife/app/presentation/components/TaskButtonAccessibilityTest.kt
@RunWith(AndroidJUnit4::class)
class TaskButtonAccessibilityTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun `task button should be accessible to screen readers`() {
        // Given
        val task = Task(
            id = "task-1",
            title = "Brush teeth",
            difficulty = TaskDifficulty.MEDIUM,
            tokenValue = 10,
            isCompleted = false
        )
        
        // When
        composeTestRule.setContent {
            TaskButton(
                task = task,
                onComplete = {}
            )
        }
        
        // Then
        composeTestRule
            .onNodeWithContentDescription("Complete task: Brush teeth to earn 10 tokens")
            .assertIsDisplayed()
            .assertHasClickAction()
            .assert(hasClickAction())
        
        // Verify semantic properties
        composeTestRule
            .onNodeWithContentDescription("Complete task: Brush teeth to earn 10 tokens")
            .assert(hasRole(Role.Button))
    }
    
    @Test
    fun `completed task button should have correct state description`() {
        // Given
        val completedTask = Task(
            id = "task-1",
            title = "Brush teeth",
            difficulty = TaskDifficulty.MEDIUM,
            tokenValue = 10,
            isCompleted = true
        )
        
        // When
        composeTestRule.setContent {
            TaskButton(
                task = completedTask,
                onComplete = {}
            )
        }
        
        // Then
        composeTestRule
            .onNodeWithContentDescription("Task completed: Brush teeth")
            .assertIsDisplayed()
            .assert(hasStateDescription("Completed"))
    }
}
```

## Testing Commands

### Build Configuration

Add to `android-kotlin/app/build.gradle.kts`:

```kotlin
android {
    // ... other configurations
    
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    // Unit testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("app.cash.turbine:turbine:0.12.1")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("org.robolectric:robolectric:4.9.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    
    // Android testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_version")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.room:room-testing:2.5.0")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.44")
    
    // Debug dependencies
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_version")
}
```

### Essential Commands

```bash
# Navigate to Android project
cd android-kotlin

# Unit tests
./gradlew test                    # Run all unit tests
./gradlew testDebugUnitTest      # Run debug unit tests
./gradlew testReleaseUnitTest    # Run release unit tests

# Integration tests
./gradlew connectedAndroidTest   # Run instrumented tests
./gradlew connectedDebugAndroidTest # Run debug instrumented tests

# Coverage
./gradlew testDebugUnitTestCoverage # Generate coverage report
./gradlew createDebugCoverageReport # Instrumented test coverage

# Specific test classes
./gradlew test --tests TaskTest
./gradlew test --tests "*.TaskTest"
./gradlew test --tests "com.arthurslife.app.domain.*"

# Clean and test
./gradlew clean test
```

## Testing Best Practices

### General Guidelines

1. **Follow AAA Pattern**: Arrange, Act, Assert
2. **Write Descriptive Test Names**: Explain what is being tested
3. **Test One Thing**: Each test should have a single focus
4. **Use Given-When-Then**: Structure tests clearly
5. **Mock External Dependencies**: Keep tests isolated

### Domain Testing Guidelines

1. **Test Business Rules**: Ensure domain invariants are maintained
2. **Test Edge Cases**: Handle boundary conditions
3. **Test Error Scenarios**: Verify proper error handling
4. **Keep Tests Fast**: Unit tests should run quickly
5. **Test Behavior, Not Implementation**: Focus on what, not how

### UI Testing Guidelines

1. **Test User Interactions**: Focus on user-visible behavior
2. **Test Accessibility**: Verify screen reader compatibility
3. **Test Different States**: Loading, error, success states
4. **Use Semantic Finders**: Find elements by accessibility labels
5. **Test Responsive Design**: Verify layout across screen sizes

### Token Economy Testing

1. **Precision is Critical**: Token calculations must be exact
2. **Test All Scenarios**: Earning, spending, balance updates
3. **Verify Atomic Operations**: Ensure consistency
4. **Test Concurrency**: Handle simultaneous operations
5. **Audit All Changes**: Track all token modifications

## Accessibility Testing

### TalkBack Testing

```kotlin
@Test
fun `should announce task completion to screen reader`() {
    // Enable TalkBack simulation
    composeTestRule.onRoot().performSemanticsAction(SemanticsActions.RequestFocus)
    
    // Trigger task completion
    composeTestRule
        .onNodeWithContentDescription("Complete task: Brush teeth")
        .performClick()
    
    // Verify announcement
    composeTestRule
        .onNodeWithText("Task completed! You earned 10 tokens!")
        .assertIsDisplayed()
}
```

### Semantic Testing

```kotlin
@Test
fun `should have proper semantic structure`() {
    composeTestRule.setContent {
        TaskListScreen(tasks = testTasks)
    }
    
    // Verify semantic hierarchy
    composeTestRule
        .onNodeWithContentDescription("Task list")
        .assert(hasRole(Role.List))
    
    composeTestRule
        .onAllNodesWithRole(Role.Button)
        .assertCountEquals(testTasks.size)
}
```

## Continuous Integration

### GitHub Actions Example

```yaml
# .github/workflows/test.yml
name: Android Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Cache Gradle dependencies
        uses: actions/cache@v3
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
          restore-keys: |
            ${{ runner.os }}-gradle-

      - name: Run unit tests
        run: ./gradlew test

      - name: Run instrumented tests
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 29
          script: ./gradlew connectedAndroidTest

      - name: Generate coverage report
        run: ./gradlew testDebugUnitTestCoverage

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
```

## Troubleshooting

### Common Issues

#### Build Issues
```bash
# Clean build
./gradlew clean

# Clear caches
./gradlew cleanBuildCache
rm -rf ~/.gradle/caches/
```

#### Test Failures
```bash
# Run tests with more verbose output
./gradlew test --info

# Run specific test with debug info
./gradlew test --tests TaskTest --debug
```

#### Robolectric Issues
```bash
# Update Robolectric
./gradlew --refresh-dependencies

# Clear Robolectric cache
rm -rf ~/.gradle/caches/robolectric/
```

## Resources

### Documentation
- [Android Testing Documentation](https://developer.android.com/training/testing)
- [Jetpack Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [MockK Documentation](https://mockk.io/)

### Best Practices
- [Android Testing Best Practices](https://developer.android.com/training/testing/fundamentals)
- [Testing Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html)
- [Domain-Driven Design Testing](https://martinfowler.com/tags/domain%20driven%20design.html)

---

This testing documentation ensures Arthur's Life app maintains high quality, reliability, and accessibility standards throughout development.

---

[🏠 Back to Main README](../README.md) | [🚀 Setup Guide](getting-started.md) | [📝 Contributing](contributing.md) | [🏗️ Architecture](architecture.md)