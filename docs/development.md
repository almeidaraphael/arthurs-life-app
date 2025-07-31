# Development Guide - Technical Documentation

[🏠 Back to Docs Hub](README.md) | [🏠 Main README](../README.md)

Comprehensive development workflow guide covering coding standards, build processes, IDE setup, and quality assurance for LemonQwest app development.

## 📋 Document Overview

### Purpose

Provide developers with comprehensive guidance on development workflows, coding standards, build processes, and quality assurance practices to ensure consistent, high-quality code.

### Audience

- **Primary**: Developers implementing features and maintaining the codebase
- **Secondary**: Technical leads and code reviewers
- **Prerequisites**: Basic Android development knowledge, Kotlin familiarity

### Scope

Covers development workflow, code quality standards, build commands, IDE configuration, and performance optimization. Does not include deployment or production operations.

## 🎯 Quick Reference

### Key Information

- **Summary**: Development workflow with quality gates and coding standards
- **Related**: [Architecture](architecture.md), [Testing Guide](testing.md)

### Common Tasks

- [Feature Development Process](#feature-development-process)
- [Code Quality Standards](#code-quality-standards)
- [Build Commands](#build-commands)
- [IDE Configuration](#ide-configuration)

## 📖 Main Content

### Section 1: Core Concepts

#### Development Workflow

1. **Architecture Planning**
   - Apply DDD principles for new domain concepts
   - Follow SOLID principles for class design
   - Implement DRY practices for shared functionality

2. **Implementation Guidelines**
   - Create domain entities and value objects first
   - Implement repository interfaces before concrete implementations
   - Build use cases to orchestrate business logic
   - Create UI components following Material Design 3

3. **Quality Assurance (Modernized)**
   - Write unit tests using modern LemonQwestTestExtension pattern (focus on essential business rules)
   - Use parallel test execution for faster feedback
   - Run static analysis tools (Detekt, KtLint)
   - Test accessibility features with TalkBack
   - Validate UI responsiveness across screen sizes

#### Branch Strategy

- **main**: Production-ready code
- **develop**: Integration branch for features
- **feature/***: Individual feature development
- **hotfix/***: Critical bug fixes

### Section 2: Implementation Details

#### Code Quality Standards

##### Kotlin Coding Standards

- **Classes**: PascalCase (`TaskRepository`, `UserViewModel`)
- **Functions**: camelCase (`createTask()`, `validateInput()`)
- **Constants**: SCREAMING_SNAKE_CASE (`MAX_TASK_NAME_LENGTH`)
- **Packages**: lowercase (`com.lemonqwest.app.domain.task`)

##### Code Style Requirements

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

##### Architecture Compliance

- **Entities** must encapsulate business logic
- **Value Objects** must be immutable
- **Repositories** must use interfaces in domain layer
- **Domain Events** must represent business concepts

##### SOLID Principles Application

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

##### DRY Implementation

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

### Section 3: Configuration

#### Build Commands

##### Essential Commands

```bash
# Build and install
make build                        # Build entire project
make install                      # Install debug APK
make build-debug                  # Create debug APK
make build-release                # Create release APK


# Code quality
make format                       # Auto-format code (Detekt formatting only)
make lint                         # Run static analysis (Detekt, zero violations required)
make qa                           # Run all quality checks

# Testing (Modernized)
make test                         # Run unit tests with parallel execution
make test-unit                    # Run debug unit tests (48 modernized test files)
make test-integration             # Run instrumented tests
make test-coverage                # Generate coverage report

# Parallel execution (faster)
./gradlew testDebugUnitTest --parallel   # Run all tests concurrently
```

##### Quality Gates (Modernized)

```bash
# Mandatory sequence (all must pass) - includes modernized tests
make copilot-pipeline             # Complete pipeline: format → lint → build → test → install
# OR step by step:
make format && make lint && make build && make test && make install

# Faster execution with parallel tests
make format && make lint && make build && ./gradlew testDebugUnitTest --parallel && make install
```

##### Java Version Verification

```bash
# Verify Java 21 (preferred) or Java 17 (fallback)
java -version
make version                      # Show version information including Gradle
make build-debug                  # Verify debug build compilation
```

#### IDE Configuration

##### Android Studio Setup

1. Go to **File → Project Structure → Project**
2. Set **Project SDK** to Java 21 (preferred) or Java 17 (fallback)
3. Set **Project Language Level** to 21 (or 17)
4. Configure **Gradle JVM** to use Java 21 (preferred) or Java 17 (fallback)

##### Code Style Settings

See `.editorconfig` in `android-kotlin/` for enforced formatting. Detekt handles all Kotlin-specific formatting rules.

##### Useful Plugins

- **Detekt** - Static analysis integration (mandatory)
- **GitToolBox** - Git integration enhancements

##### VS Code Configuration

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

### Section 4: Examples

#### Performance Optimization

##### Jetpack Compose Best Practices

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

##### Database Optimization

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

### Section 5: Best Practices

#### Security Considerations

##### Input Validation

All user input must be validated according to business rules. Use domain-level validators and ensure compliance with COPPA and child safety standards.

##### Data Protection

Sensitive data (e.g., PINs, tokens) must be stored securely using Android Keystore. Never store sensitive information in plain text or shared preferences.

### Section 6: Troubleshooting (Modernized)

Common issues and their solutions:

- **Detekt violations**: Run `make format` and fix all issues before proceeding. Zero violations required.
- **Build failures**: Check for missing dependencies, incorrect Java version, or misconfigured Gradle files. Use `make debug-info` for troubleshooting.
- **Test failures (Modernized)**: All tests use LemonQwestTestExtension - no manual MockK setup needed. Use `make test` or parallel execution with `./gradlew testDebugUnitTest --parallel`.
- **Legacy test patterns**: Convert old tests to use `@RegisterExtension` with `LemonQwestTestExtension` - see [Testing Guide](testing.md).
- **IDE configuration issues**: Verify `.editorconfig` and SDK settings match project requirements. Use `make check-env` to validate environment.

## 🔗 Integration Points

### Dependencies

- **Internal**: [Architecture](architecture.md) - Design principles and patterns
- **Internal**: [Getting Started](getting-started.md) - Development environment setup
- **Internal**: [Testing Guide](testing.md) - Testing strategies and implementation

### Related Modules

- **Dependency Injection**: See `di/DataStoreModule.kt` for Hilt setup and repository bindings.
- **Domain Events**: See `domain/common/AchievementUpdateEvent.kt` for event-driven architecture.

### Related Features

- **Code Quality**: Static analysis with Detekt and KtLint
- **Build Process**: Gradle-based build system with quality gates
- **IDE Integration**: Android Studio and VS Code configuration
- **Security**: Input validation and data protection practices

## 📚 Additional Resources

### Internal Documentation

- [Getting Started](getting-started.md) - Initial setup and configuration
- [Architecture](architecture.md) - Design principles and patterns
- [Testing Guide](testing.md) - Testing strategies and implementation
- [Contributing Guide](contributing.md) - Contribution guidelines

### External Resources

- [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html) - Official Kotlin conventions
- [Android Development](https://developer.android.com/guide) - Official Android documentation
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - UI framework documentation
- [Detekt](https://detekt.dev/) - Static analysis tool

### Tools and Utilities

- [Gradle](https://gradle.org/) - Build automation tool
- [Android Studio](https://developer.android.com/studio) - Primary IDE
- [VS Code](https://code.visualstudio.com/) - Alternative editor
- [Git](https://git-scm.com/) - Version control system

---

## 📝 Contributing

### How to Contribute

1. **Follow Standards**: Adhere to established coding standards and workflows
2. **Run Quality Gates**: Execute all quality checks before submitting
3. **Document Changes**: Update documentation when modifying workflows
4. **Test Thoroughly**: Ensure all changes are properly tested

### Review Process

1. **Code Review**: Validate adherence to standards and patterns
2. **Quality Gates**: Ensure all automated checks pass
3. **Manual Testing**: Verify functionality works as expected
4. **Documentation Review**: Update relevant documentation

### Style Guidelines

- Follow Kotlin coding conventions
- Use clear, descriptive naming
- Include appropriate comments and documentation
- Maintain consistent formatting and structure

---

**Navigation**: [🏠 Docs Hub](README.md) | [🏠 Main README](../README.md) | [📋 Planning](../planning/README.md)
