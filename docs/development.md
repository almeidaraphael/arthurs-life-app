# Development Guide - Technical Documentation

[🏠 Back to Docs Hub](README.md) | [🏠 Main README](../README.md)

Comprehensive development workflow guide covering coding standards, build processes, IDE setup, and quality assurance for Arthur's Life app development.

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
- **Status**: Complete - actively maintained
- **Last Updated**: 2025-01-06
- **Related**: [Architecture](architecture.md), [Testing Guide](testing.md)

### Common Tasks
- [Feature Development Process](#development-workflow)
- [Code Quality Standards](#code-quality-standards)
- [Build Commands](#build-commands)
- [IDE Configuration](#ide-configuration)

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

## 🔗 Integration Points

### Dependencies
- **Internal**: [Architecture](architecture.md) - Design principles and patterns
- **Internal**: [Getting Started](getting-started.md) - Development environment setup
- **Internal**: [Testing Guide](testing.md) - Testing strategies and implementation
- **Planning**: [Requirements](../planning/requirements.md) - Feature specifications

### Related Features
- **Code Quality**: Static analysis with Detekt and KtLint
- **Build Process**: Gradle-based build system with quality gates
- **IDE Integration**: Android Studio and VS Code configuration
- **Security**: Input validation and data protection practices

## 📊 Success Metrics

### Implementation Goals
- **Code Quality**: 80%+ test coverage, zero critical issues in static analysis
- **Development Speed**: Efficient workflows with automated quality checks
- **Consistency**: All code follows established standards and patterns
- **Maintainability**: Clear, readable code with proper documentation

### Quality Indicators
- **Build Success**: All builds pass with no failures
- **Test Coverage**: Unit tests cover all business logic
- **Code Standards**: Detekt and KtLint pass with no violations
- **Performance**: App meets responsiveness and efficiency requirements

## 🚧 Implementation Status

**Current Status**: Complete

### Completed Features
- [x] Development workflow with feature branch strategy
- [x] Code quality standards with Kotlin conventions
- [x] Build commands and quality gates
- [x] IDE configuration for Android Studio and VS Code
- [x] Performance optimization guidelines
- [x] Security considerations and best practices

### Future Enhancements
- [ ] CI/CD pipeline integration
- [ ] Automated code review tools
- [ ] Performance profiling automation
- [ ] Enhanced security scanning

## 🔄 Maintenance

### Regular Updates
- **When to Update**: When adding new tools, changing standards, or updating dependencies
- **Update Process**: Review workflow effectiveness, update tool configurations, validate standards
- **Review Schedule**: Monthly workflow review, quarterly tool evaluation

### Version History
- **v1.0.0** (2025-01-06): Initial development guide with comprehensive workflow and standards

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
