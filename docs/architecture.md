# Architecture & Design Principles

[🏠 Back to Main README](../README.md)

## 📋 Page Navigation

| Section | Description |
|---------|-------------|
| [Domain-Driven Design](#domain-driven-design-ddd) | Core DDD principles and implementation |
| [SOLID Principles](#solid-principles) | Object-oriented design guidelines |
| [DRY Implementation](#dry-dont-repeat-yourself) | Code reuse strategies |
| [System Architecture](#system-architecture) | Overall application structure |

## 🔗 Related Documentation

| Topic | Link |
|-------|------|
| **Getting Started** | [getting-started.md](getting-started.md) |
| **Technology Stack** | [tech-stack.md](tech-stack.md) |
| **Testing Guide** | [testing.md](testing.md) |
| **Development Guide** | [development.md](development.md) |

## Domain-Driven Design (DDD)

### Core Concepts

Arthur's Life App implements DDD to manage the complexity of family task management through clear domain modeling:

- **Token Economy**: Digital currency system for completing tasks
- **Role-Based Access**: Child, Caregiver, and Admin user roles with different permissions
- **Task Management**: Task creation, assignment, completion, and scheduling
- **Reward System**: Token-based reward catalog and redemption
- **Family Structure**: Multi-child households with caregiver oversight

### Domain Model Components

#### Aggregate Roots
- **User**: Family member with role, tokens, and authentication
- **Task**: Work items with categories, difficulty, and completion tracking
- **Token**: Digital currency for the reward economy
- **Reward**: Items and privileges available for token redemption

#### Value Objects
- **UserRole**: Child, Caregiver, Admin enumeration
- **TaskCategory**: Chore, homework, personal care classifications
- **TaskDifficulty**: Easy, medium, hard with token values
- **RewardCategory**: Digital, physical, privilege types

#### Domain Events
- **TaskCompleted**: Triggered when a child completes a task
- **TokensEarned**: Fired when tokens are awarded for task completion
- **RewardRedeemed**: Raised when tokens are spent on rewards
- **UserRoleChanged**: Emitted when user permissions are modified

#### Repository Pattern
```kotlin
// Domain interface
interface TaskRepository {
    suspend fun findByUserId(userId: String): Flow<List<Task>>
    suspend fun save(task: Task): Result<Task>
    suspend fun findById(taskId: String): Task?
}

// Implementation in data layer
class RoomTaskRepository(
    private val taskDao: TaskDao,
    private val mapper: TaskMapper
) : TaskRepository {
    // Implementation details
}
```

### DDD Implementation Rules

1. **Aggregate Boundaries**: Each aggregate maintains its own consistency
2. **Domain Events**: Use events for cross-aggregate communication
3. **Value Objects**: Immutable objects for concept representation
4. **Domain Services**: Business logic that doesn't belong to entities
5. **Repository Abstractions**: Keep domain independent of data concerns

## SOLID Principles

### Single Responsibility Principle (SRP)
Each class has one clear purpose and reason to change.

```kotlin
// Good: Focused responsibility
class TaskValidator {
    fun validate(task: Task): ValidationResult {
        // Only validation logic
    }
}

class TaskRepository {
    suspend fun save(task: Task): Result<Task> {
        // Only data persistence logic
    }
}

// Bad: Multiple responsibilities
class TaskService {
    fun validate(task: Task): ValidationResult { /* validation */ }
    suspend fun save(task: Task): Result<Task> { /* persistence */ }
    fun sendNotification(task: Task) { /* notification */ }
}
```

### Open/Closed Principle (OCP)
Open for extension, closed for modification.

```kotlin
// Extensible reward provider interface
interface RewardProvider {
    fun getAvailableRewards(userRole: UserRole): List<Reward>
    fun canRedeem(reward: Reward, tokens: Int): Boolean
}

// Implementations for different reward types
class DigitalRewardProvider : RewardProvider { /* implementation */ }
class PhysicalRewardProvider : RewardProvider { /* implementation */ }
class PrivilegeRewardProvider : RewardProvider { /* implementation */ }
```

### Liskov Substitution Principle (LSP)
Subtypes must be substitutable for their base types.

```kotlin
// All implementations work interchangeably
fun processRewards(provider: RewardProvider, user: User) {
    val rewards = provider.getAvailableRewards(user.role)
    // Works with any RewardProvider implementation
}
```

### Interface Segregation Principle (ISP)
Create focused, cohesive interfaces.

```kotlin
// Focused interfaces instead of one large interface
interface TaskReader {
    suspend fun findById(id: String): Task?
    suspend fun findByUserId(userId: String): Flow<List<Task>>
}

interface TaskWriter {
    suspend fun save(task: Task): Result<Task>
    suspend fun delete(taskId: String): Result<Unit>
}

// Clients depend only on what they need
class TaskDisplayViewModel(
    private val taskReader: TaskReader // Only needs read operations
)

class TaskCreationViewModel(
    private val taskWriter: TaskWriter, // Only needs write operations
    private val taskValidator: TaskValidator
)
```

### Dependency Inversion Principle (DIP)
Depend on abstractions, not concretions.

```kotlin
// High-level module depends on abstraction
class TaskCompletionUseCase(
    private val taskRepository: TaskRepository, // Interface
    private val tokenService: TokenService,     // Interface
    private val eventPublisher: EventPublisher  // Interface
) {
    suspend fun completeTask(taskId: String, userId: String): Result<Unit> {
        // Business logic depends on abstractions
    }
}
```

## DRY (Don't Repeat Yourself)

### Code Reuse Strategies

#### Shared Compose Components
```kotlin
// Reusable UI components
@Composable
fun TaskCard(
    task: Task,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Common task display logic
}

@Composable
fun TokenDisplay(
    tokens: Int,
    modifier: Modifier = Modifier
) {
    // Common token visualization
}
```

#### Common Validation Logic
```kotlin
// Centralized validation rules
object ValidationRules {
    fun validateTaskTitle(title: String): ValidationResult {
        return when {
            title.isBlank() -> ValidationResult.Error("Title cannot be empty")
            title.length < 3 -> ValidationResult.Error("Title too short")
            title.length > 50 -> ValidationResult.Error("Title too long")
            else -> ValidationResult.Success
        }
    }
    
    fun validateTokenAmount(amount: Int): ValidationResult {
        return when {
            amount < 0 -> ValidationResult.Error("Tokens cannot be negative")
            amount > 1000 -> ValidationResult.Error("Token amount too high")
            else -> ValidationResult.Success
        }
    }
}
```

#### Shared Configuration
```kotlin
// Single source of truth for app constants
object AppConfig {
    const val MAX_TASK_TITLE_LENGTH = 50
    const val MIN_TASK_TITLE_LENGTH = 3
    const val MAX_TOKEN_REWARD = 100
    const val MIN_TOKEN_REWARD = 1
    
    object TokenValues {
        const val EASY_TASK = 5
        const val MEDIUM_TASK = 10
        const val HARD_TASK = 20
    }
}
```

## System Architecture

### Layer Organization

```
com.arthurslife.app/
├── domain/              # Business logic and entities
│   ├── user/           # User aggregate with roles and authentication
│   ├── task/           # Task management with categories and completion
│   ├── token/          # Token economy with earning and spending
│   └── reward/         # Reward system with catalog and redemption
├── data/               # Data layer implementation
│   ├── local/         # Room database and DataStore persistence
│   ├── repository/    # Repository pattern implementations
│   └── mapper/        # Domain/data model mapping
├── presentation/       # UI layer with Jetpack Compose
│   ├── screens/       # Screen composables organized by feature
│   ├── components/    # Reusable UI components following DRY
│   ├── theme/         # Material Design 3 theming
│   └── navigation/    # Type-safe navigation configuration
├── di/                # Hilt dependency injection modules
└── util/              # Shared utility functions and extensions
```

### Data Flow Architecture

1. **Presentation Layer**: Jetpack Compose UI with ViewModels
2. **Domain Layer**: Business logic with use cases and entities
3. **Data Layer**: Repository implementations with Room database
4. **Infrastructure**: Android-specific concerns and external services

### Component Dependencies

```
Presentation → Domain ← Data
     ↓           ↓        ↓
    UI      Use Cases  Repository
Components   Events   Implementations
```

### Key Architectural Benefits

- **Separation of Concerns**: Clear boundaries between layers
- **Testability**: Domain logic independent of Android framework
- **Maintainability**: SOLID principles make code easy to modify
- **Reusability**: DRY approach reduces code duplication
- **Scalability**: DDD patterns support feature growth
- **Type Safety**: Kotlin's type system prevents common errors

---

[🏠 Back to Main README](../README.md) | [🚀 Getting Started](getting-started.md) | [🧪 Testing Guide](testing.md)
