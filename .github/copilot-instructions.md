# GitHub Copilot Instructions – Arthur's Life App

## Project Overview
Arthur's Life is a native Android family task management app built with Kotlin 2.1.0, Java 21, and Jetpack Compose. It features a gamified token-based reward system, role-based access (Child, Caregiver, Admin), and a sophisticated theme system (Mario Classic, Material Light/Dark) with semantic UI mapping.

## Architecture Overview
**Clean Architecture + DDD**: Domain → Infrastructure → Presentation with strict dependency inversion.

**Package Structure**:
```kotlin
com.arthurslife.app/
├── domain/           # Aggregates: User, Task, Token, Reward, Achievement
├── infrastructure/   # Repository implementations, Room DAOs, data sources  
├── presentation/     # Jetpack Compose UI, navigation, ViewModels
├── di/              # Hilt dependency injection modules
└── data/            # DataStore preferences and theme management
```

**Key Architectural Patterns**:
- **Repository Pattern**: Domain interfaces → Infrastructure implementations
- **Aggregate Roots**: User, Task, Token, Reward with business logic encapsulation
- **Domain Events**: TaskCompleted, TokensEarned, RewardRedeemed for cross-aggregate communication
- **Theme System**: Role-based theming (Child→Mario Classic, Caregiver→Material) with semantic icon mapping

## Development Standards - ZERO TOLERANCE POLICY
- **Language**: Kotlin 2.1.0 (explicit null safety), Java 21
- **Architecture**: DDD, Clean Architecture, SOLID, DRY
- **Testing**: JUnit, Espresso, MockK (80%+ coverage required for domain)
- **Static Analysis**: Detekt (ZERO violations - any violation blocks completion)
- **Accessibility**: All UI must support TalkBack, semantic roles, and 4.5:1 color contrast
- **Child Safety**: Input validation, secure storage, COPPA compliance
- **Quality Gate**: NO code is considered complete with ANY build/test/detekt failures

## Critical Implementation Patterns

### Theme System Architecture
**Role-Based Theme Mapping**: 
- Child Role → Mario Classic theme → Use game terminology (Quests, Coins, Power-ups)
- Caregiver/Admin → Material Light/Dark → Use standard terminology (Tasks, Tokens, Rewards)

**Theme-Aware Components**: All UI components must support theme switching via `BaseAppTheme` interface:
```kotlin
// Use ThemeAware components, not raw Material components
ThemeAwareCard(...)      // NOT Card(...)
ThemeAwareButton(...)    // NOT Button(...)
themeAwareIconButton(...) // NOT IconButton(...)
```

**Semantic Icon System**: Use `SemanticIconType` enum for consistent theming:
```kotlin
ThemeAwareIcon(semanticType = SemanticIconType.TASK_COMPLETE)
// Automatically maps to different icons per theme
```

## Navigation & Role Architecture
**Single App, Three Modes**: The app has a unified navigation structure with role-based screen access via `MainAppNavigation.kt`:

```kotlin
// Role-based navigation paths
Route.CHILD_HOME          // Child dashboard with tasks/tokens
Route.CAREGIVER_DASHBOARD // Caregiver overview and management
Route.CAREGIVER_CHILDREN  // Child management interface
Route.THEME_SETTINGS      // Cross-role theme switching
```

**Role Switching**: Users can switch between roles using `RoleSwitchingDialog` with PIN authentication for elevated permissions.

## Domain-Driven Design Implementation

### Aggregate Boundaries & Events
**Core Aggregates**: User, Task, Token, Reward maintain their own consistency
**Cross-Aggregate Communication**: Use domain events (`TaskCompleted`, `TokensEarned`, `RewardRedeemed`)
**Repository Pattern**: Domain interfaces in `domain/`, implementations in `infrastructure/`

```kotlin
// Domain interface (never change)
interface TaskRepository {
    suspend fun findByUserId(userId: String): Flow<List<Task>>
    suspend fun save(task: Task): Result<Task>
}

// Infrastructure implementation
class TaskRepositoryImpl(
    private val taskDataSource: TaskDataSource
) : TaskRepository
```

### Value Objects & Domain Logic
**Immutable Value Objects**: `UserRole`, `TaskCategory`, `TokenBalance`, `TaskDifficulty`
**Business Rules in Entities**: Task completion logic, token earning rules, role permissions
**Domain Services**: Cross-entity business logic (e.g., `AuthenticationDomainService`)

## Essential Development Workflow

### Mandatory Directory Check
**CRITICAL**: Always verify you're in the correct directory before running Gradle commands:
```zsh
# Check current directory and navigate if needed
if [ "$(basename $(pwd))" != "android-kotlin" ]; then 
    echo "Navigating to android-kotlin directory..."
    cd android-kotlin
fi
```

### Build & Quality Pipeline
**MANDATORY SEQUENCE** - All must pass for completion:
```zsh
./gradlew detektFormat  # Auto-format code
./gradlew detekt        # ZERO violations required
./gradlew build         # Must build successfully
./gradlew test          # All tests must pass
./gradlew installDebug  # Must install on device/emulator
```

### Kotlin File Structure
**STRICT ORDER** - Package → Imports → Code:
```kotlin
package com.arthurslife.app.domain.task  // ALWAYS first

import androidx.compose.runtime.Composable  // Explicit imports only
import com.arthurslife.app.domain.user.User

// Code starts here
data class Task(...)

@Composable  // camelCase function names
fun taskCard(...) { ... }
```

## Feature Implementation Workflow
1. **Start with Domain**: Define entities, value objects, business rules in `domain/`
2. **Create Use Cases**: Implement business logic as use cases
3. **Add Infrastructure**: Repository implementations in `infrastructure/`
4. **Build UI**: Theme-aware Compose screens in `presentation/screens/`
5. **Add DI**: Wire dependencies in `di/` modules
6. **Test**: Comprehensive unit/integration tests

## Critical Code Patterns

### Theme-Aware UI Components
**ALWAYS use theme-aware components for UI consistency:**
```kotlin
// ✅ CORRECT - Theme-aware components
import com.arthurslife.app.presentation.theme.components.*

@Composable
fun taskScreen() {  // camelCase for @Composable
    ThemeAwareCard {
        ThemeAwareIcon(semanticType = SemanticIconType.TASK_COMPLETE)
        themeAwareButton(
            onClick = { ... },
            text = "Complete"
        )
    }
}

// ❌ WRONG - Raw Material components
Card { 
    Icon(...)
    Button(...)
}
```

### Repository Pattern Implementation
**Domain interfaces NEVER depend on Android/infrastructure:**
```kotlin
// ✅ CORRECT - Domain interface
// File: domain/task/TaskRepository.kt
interface TaskRepository {
    suspend fun findByUserId(userId: String): Flow<List<Task>>
    suspend fun save(task: Task): Result<Task>
}

// ✅ CORRECT - Infrastructure implementation  
// File: infrastructure/task/TaskRepositoryImpl.kt
class TaskRepositoryImpl(
    private val taskDataSource: TaskDataSource,
    private val mapper: TaskMapper
) : TaskRepository
```

### Hilt Dependency Injection Pattern
**Module organization by feature, not layer:**
```kotlin
// File: di/TaskManagementModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class TaskManagementModule {
    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
}
```

## UI & Accessibility Guidelines
- Use `@Composable` functions for UI
- Apply `@Preview` for design verification
- Support theme switching (Mario Classic, Material)
- Use semantic icon mapping
- Provide `contentDescription` for images
- Use `LazyColumn` for long lists
- Avoid hardcoded strings (use string resources)
- No business logic in composables

## Testing Requirements
- Test all domain logic and business rules
- Mock external dependencies with MockK
- Use descriptive test names (Given-When-Then)
- Verify happy path and error scenarios
- UI tests: Espresso for interactions, navigation, accessibility

## Common Pitfalls to Avoid
- Wildcard imports (use explicit)
- Magic numbers (use named constants)
- Long parameter lists (group into data classes)
- Mutable shared state (prefer immutable data classes)
- Blocking operations on main thread (use coroutines)
- Direct DB access in UI (use repository pattern)
- Hardcoded strings (use resources)
## Mandatory Verification Before Completion - ALL MUST PASS
- Check/log current working directory before ALL Gradle commands
- Run `./gradlew detektFormat` - MUST pass with no errors
- Run `./gradlew detekt` - MUST show ZERO violations
- Run `./gradlew build` - MUST build successfully 
- Run `./gradlew test` - MUST pass ALL tests
- Run `./gradlew installDebug` - MUST install successfully
- Verify all @Composable functions use camelCase - NO EXCEPTIONS
- Confirm ZERO wildcard imports - ALL must be explicit
- Check all imports are specific - NO exceptions
- Ensure 80%+ test coverage in domain layer - NON-NEGOTIABLE
- Support accessibility and child safety standards - MANDATORY
- **IMPLEMENTATION IS INCOMPLETE** if ANY of above fail

## Key References
- `presentation/theme/` – Theme system patterns
- `presentation/navigation/MainAppNavigation.kt` – Navigation structure
- `domain/user/`, `infrastructure/auth/` – Complete implementation examples
- `di/AuthModule.kt` – DI patterns

## Current Development Priorities (MVP)
1. **Task Management**: Task creation, assignment, completion, repository, screens
2. **Token Economy**: Token earning, balance tracking, spending
3. **Reward System**: Reward catalog, redemption, management screens
4. **Achievement System**: Achievement tracking, unlock logic, display screens

---
**ZERO TOLERANCE POLICY: All code MUST pass Detekt with ZERO violations, follow DDD/SOLID/DRY principles, include comprehensive tests, support full accessibility, and maintain child safety. ANY failure in these areas means the implementation is INCOMPLETE and MUST be fixed before proceeding.**

## CRITICAL SUCCESS CRITERIA
**Implementation is ONLY complete when ALL of these pass:**
- ✅ `./gradlew detektFormat` - formats successfully
- ✅ `./gradlew detekt` - ZERO violations found
- ✅ `./gradlew build` - builds successfully
- ✅ `./gradlew test` - ALL tests pass
- ✅ `./gradlew installDebug` - installs successfully

**If ANY command fails, the work is NOT DONE.**
