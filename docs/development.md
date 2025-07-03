# Development Guide

[🏠 Back to Main README](../README.md)

## 📋 Page Navigation

| Section | Description |
|---------|-------------|
| [Development Workflow](#development-workflow) | Feature development process |
| [Code Quality](#code-quality-standards) | Standards and enforcement |
| [Build Commands](#build-commands) | Essential build and test commands |
| [IDE Setup](#ide-configuration) | Development environment setup |

## 🔗 Related Documentation

| Topic | Link |
|-------|------|
| **Getting Started** | [getting-started.md](getting-started.md) |
| **Architecture** | [architecture.md](architecture.md) |
| **Testing Guide** | [testing.md](testing.md) |
| **Contributing** | [contributing.md](contributing.md) |

## Development Workflow

### Feature Development Process

1. **Architecture Planning**
   - Apply DDD principles for new domain concepts
   - Follow SOLID principles for class design
   - Implement DRY practices for shared functionality

2. **Implementation Guidelines**
   - Create domain entities and value objects first
   - Implement repository interfaces before concrete implementations
   - Build use cases to orchestrate business logic
   - Create UI components following Material Design 3

3. **Quality Assurance**
   - Write unit tests for domain logic (80%+ coverage required)
   - Run static analysis tools (Detekt, KtLint)
   - Test accessibility features with TalkBack
   - Validate UI responsiveness across screen sizes

### Branch Strategy

- **main**: Production-ready code
- **develop**: Integration branch for features
- **feature/***: Individual feature development
- **hotfix/***: Critical bug fixes

## Code Quality Standards

### Kotlin Coding Standards

#### Naming Conventions
- **Classes**: PascalCase (`TaskRepository`, `UserViewModel`)
- **Functions**: camelCase (`createTask()`, `validateInput()`)
- **Constants**: SCREAMING_SNAKE_CASE (`MAX_TASK_NAME_LENGTH`)
- **Packages**: lowercase (`com.arthurslife.app.domain.task`)

#### Code Style Requirements
```kotlin
// Good: Clear, descriptive naming
class TaskCompletionUseCase(
    private val taskRepository: TaskRepository,
    private val tokenService: TokenService
) {
    suspend fun completeTask(taskId: String, userId: String): Result<Unit> {
        return try {
            val task = taskRepository.findById(taskId)
                ?: return Result.failure(TaskNotFoundException(taskId))
            
            val updatedTask = task.markCompleted(userId)
            taskRepository.save(updatedTask)
            
            tokenService.awardTokens(userId, task.tokenValue)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Bad: Poor naming and structure
class Service {
    fun doTask(id: String, u: String): Boolean {
        // Unclear logic and return type
    }
}
```

### Architecture Compliance

#### Domain-Driven Design
- **Entities** must encapsulate business logic
- **Value Objects** must be immutable
- **Repositories** must use interfaces in domain layer
- **Domain Events** must represent business concepts

#### SOLID Principles Application
```kotlin
// Single Responsibility: One purpose per class
class TokenCalculator {
    fun calculateReward(difficulty: TaskDifficulty): Int {
        return when (difficulty) {
            TaskDifficulty.EASY -> 5
            TaskDifficulty.MEDIUM -> 10
            TaskDifficulty.HARD -> 20
        }
    }
}

// Open/Closed: Extensible without modification
interface NotificationProvider {
    suspend fun sendNotification(message: String, userId: String)
}

class PushNotificationProvider : NotificationProvider {
    override suspend fun sendNotification(message: String, userId: String) {
        // Push notification implementation
    }
}

// Interface Segregation: Focused interfaces
interface TaskReader {
    suspend fun findById(id: String): Task?
    suspend fun findByUserId(userId: String): Flow<List<Task>>
}

interface TaskWriter {
    suspend fun save(task: Task): Result<Task>
    suspend fun delete(taskId: String): Result<Unit>
}
```

#### DRY Implementation
```kotlin
// Shared validation logic
object ValidationRules {
    const val MIN_TASK_TITLE_LENGTH = 3
    const val MAX_TASK_TITLE_LENGTH = 50
    
    fun validateTitle(title: String): ValidationResult {
        return when {
            title.isBlank() -> ValidationResult.Error("Title cannot be empty")
            title.length < MIN_TASK_TITLE_LENGTH -> ValidationResult.Error("Title too short")
            title.length > MAX_TASK_TITLE_LENGTH -> ValidationResult.Error("Title too long")
            else -> ValidationResult.Success
        }
    }
}

// Reusable Compose components
@Composable
fun StandardButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(text)
    }
}
```

## Build Commands

### Essential Commands
```bash
# Navigate to Android project
cd android-kotlin

# Build and install
./gradlew build                    # Build entire project
./gradlew installDebug            # Install debug APK
./gradlew assembleDebug           # Create debug APK
./gradlew assembleRelease         # Create release APK

# Code quality
./gradlew ktlintFormat            # Auto-format code
./gradlew ktlintCheck             # Check code formatting
./gradlew detekt                  # Run static analysis
./gradlew check                   # Run all quality checks

# Testing
./gradlew test                    # Run unit tests
./gradlew testDebugUnitTest       # Run debug unit tests
./gradlew connectedAndroidTest    # Run instrumented tests
./gradlew testCoverage            # Generate coverage report
```

### Quality Gates
```bash
# Pre-commit verification
./gradlew ktlintFormat && ./gradlew detekt && ./gradlew test

# Release verification
./gradlew check && ./gradlew testCoverage && ./gradlew assembleRelease
```

### Java Version Verification
```bash
# Verify Java 21 (preferred) or Java 17 (fallback)
java -version
./gradlew -version
./gradlew compileDebugKotlin
```

## IDE Configuration

### Android Studio Setup

#### Project SDK Configuration
1. Go to **File → Project Structure → Project**
2. Set **Project SDK** to Java 21 (preferred) or Java 17 (fallback)
3. Set **Project Language Level** to 21 (or 17)
4. Configure **Gradle JVM** to use Java 21 (preferred) or Java 17 (fallback)

#### Code Style Settings
```xml
<!-- .editorconfig -->
root = true

[*.{kt,kts}]
indent_style = space
indent_size = 4
end_of_line = lf
charset = utf-8
trim_trailing_whitespace = true
insert_final_newline = true
max_line_length = 120
```

#### Useful Plugins
- **Kotlin Multiplatform Mobile** (built-in)
- **Detekt** - Static analysis integration
- **GitToolBox** - Git integration enhancements

### VS Code Configuration

#### Java Extension Settings
```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-21",
      "path": "/path/to/java21"
    },
    {
      "name": "JavaSE-17", 
      "path": "/path/to/java17"
    }
  ],
  "java.compile.nullAnalysis.mode": "automatic",
  "kotlin.languageServer.enabled": true
}
```

## Performance Optimization

### Jetpack Compose Best Practices
```kotlin
// Use remember for expensive calculations
@Composable
fun TaskList(tasks: List<Task>) {
    val sortedTasks = remember(tasks) {
        tasks.sortedBy { it.dueDate }
    }
    
    LazyColumn {
        items(sortedTasks) { task ->
            TaskItem(task = task)
        }
    }
}

// State hoisting for reusable components
@Composable
fun TaskItem(
    task: Task,
    onComplete: (Task) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Component implementation
}
```

### Database Optimization
```kotlin
// Efficient queries with Room
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = 0")
    fun getActiveTasks(userId: String): Flow<List<TaskEntity>>
    
    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isCompleted = 1")
    suspend fun getCompletedTaskCount(userId: String): Int
}
```

## Security Considerations

### Input Validation
```kotlin
// Validate all user inputs
class TaskCreationUseCase(
    private val taskRepository: TaskRepository,
    private val validator: TaskValidator
) {
    suspend fun createTask(request: CreateTaskRequest): Result<Task> {
        val validationResult = validator.validate(request)
        if (validationResult.hasErrors()) {
            return Result.failure(ValidationException(validationResult.errors))
        }
        
        // Continue with task creation
    }
}
```

### Data Protection
```kotlin
// Use Android Keystore for sensitive data
class SecurePreferences(context: Context) {
    private val keyAlias = "user_pin_key"
    
    fun storePIN(pin: String) {
        // Encrypt using Android Keystore
    }
    
    fun retrievePIN(): String? {
        // Decrypt using Android Keystore
    }
}
```

---

[🏠 Back to Main README](../README.md) | [🚀 Getting Started](getting-started.md) | [🏗️ Architecture](architecture.md) | [🧪 Testing](testing.md)
