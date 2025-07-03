# GitHub Copilot Instructions - Arthur's Life App

## 🎯 Project Overview

**Arthur's Life** is a native Android family task management application built with Kotlin, Jetpack Compose, and Domain-Driven Design principles. The app features a token-based reward system for children to complete tasks and earn rewards.

## 🏗️ Architecture Principles

### Domain-Driven Design (DDD)
- **Aggregate Roots**: User, Task, Token, Reward
- **Value Objects**: UserRole, TaskCategory, TaskDifficulty
- **Domain Events**: TaskCompleted, TokensEarned, RewardRedeemed
- **Repository Pattern**: Clean data layer separation

### SOLID Principles
- **Single Responsibility**: One class, one purpose
- **Open/Closed**: Extensible for new features
- **Liskov Substitution**: Proper inheritance
- **Interface Segregation**: Focused interfaces
- **Dependency Inversion**: Depend on abstractions

### DRY (Don't Repeat Yourself)
- Reusable Compose components
- Centralized business logic
- Shared utility functions
- Common validation rules

## 🛠️ Development Standards

### Code Quality Requirements
- **Java 21** (preferred) or **Java 17** (fallback)
- **Kotlin 2.1.0** with explicit null safety
- **100% KtLint compliance** before commits
- **Detekt static analysis** with zero violations
- **Unit test coverage** minimum 80% for domain layer

### Ktlint Compliance Rules (MANDATORY)
- **Function Naming**: All @Composable functions MUST use camelCase (e.g., `taskScreen`, not `TaskScreen`)
- **No Wildcard Imports**: Use specific imports instead of `import package.*`
- **Explicit Imports**: Import each class/function explicitly
- **Factory Method Exception**: Only factory methods can start with uppercase letters
- **Auto-Format Required**: Run `./gradlew ktlintFormat` after every code change
- **Pre-Commit Check**: Ensure `./gradlew ktlintMainSourceSetFormat` passes before committing

#### Specific Naming Examples
```kotlin
// ✅ Correct: Composable functions use camelCase
@Composable
fun taskListScreen() { }

@Composable
fun userProfileCard() { }

@Composable
fun pinEntryScreen() { }

// ❌ Incorrect: PascalCase for Composables (ktlint error)
@Composable
fun TaskListScreen() { }

@Composable
fun UserProfileCard() { }

@Composable
fun PinEntryScreen() { }
```

#### Import Rules
```kotlin
// ✅ Correct: Specific imports
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// ❌ Incorrect: Wildcard imports (ktlint error)
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
```

### Build & Quality Commands
```bash
# Format code
./gradlew ktlintFormat

# Static analysis
./gradlew detekt

# Build project
./gradlew build

# Run tests
./gradlew test

# Install debug APK
./gradlew installDebug
```

### Naming Conventions
- **Classes**: PascalCase (`TaskRepository`, `UserViewModel`)
- **Functions**: camelCase (`createTask()`, `validateInput()`)
- **Constants**: SCREAMING_SNAKE_CASE (`MAX_TASK_NAME_LENGTH`)
- **Packages**: lowercase (`com.arthurslife.app.domain.task`)

## 📱 UI Development Guidelines

### Jetpack Compose Standards
- Use `@Composable` functions for UI components
- Apply `@Preview` annotations for design verification
- Follow Material Design 3 principles
- Implement proper state hoisting patterns

### Accessibility Requirements
- Provide `contentDescription` for images
- Use semantic roles for interactive elements
- Support TalkBack navigation
- Maintain 4.5:1 color contrast ratios

### Performance Best Practices
- Use `remember` for expensive calculations
- Implement `LazyColumn` for long lists
- Apply `derivedStateOf` for computed values
- Avoid unnecessary recompositions

## 🧪 Testing Requirements

### Unit Testing
- Test all domain logic and business rules
- Mock external dependencies with MockK
- Use descriptive test names following Given-When-Then
- Verify both happy path and error scenarios

### UI Testing
- Test user interactions with Espresso
- Verify navigation flows
- Test accessibility features
- Validate error states and loading indicators

## 🔒 Security & Privacy

### Child Safety Requirements
- Validate all user inputs
- Implement proper authentication
- Use secure data storage
- Follow COPPA compliance guidelines

### Technical Security
- Use Android Keystore for sensitive data
- Implement certificate pinning
- Apply ProGuard obfuscation
- Validate data at all boundaries

## 📁 Project Structure

```
app/src/main/java/com/arthurslife/app/
├── domain/              # Business logic
│   ├── user/           # User management
│   ├── task/           # Task operations
│   ├── token/          # Token economy
│   └── reward/         # Reward system
├── data/               # Data layer
│   ├── local/         # Room database
│   ├── repository/    # Data repositories
│   └── mapper/        # Data mappers
├── presentation/       # UI layer
│   ├── screens/       # Screen composables
│   ├── components/    # Reusable components
│   ├── theme/         # Material Design
│   └── navigation/    # Navigation logic
├── di/                # Dependency injection
└── util/              # Utilities
```

## 💡 Code Generation Guidelines

### When generating domain entities:
- Include proper validation in constructors
- Implement value object patterns
- Add domain events where appropriate
- Follow immutability principles

### When generating repository implementations:
- Use suspend functions for async operations
- Implement proper error handling
- Add logging for debugging
- Follow repository interface contracts

### When generating Compose UI:
- Use state-first approach
- Implement proper error boundaries
- Add loading states
- Follow accessibility guidelines

### When generating tests:
- Include setup and teardown methods
- Test edge cases and error conditions
- Use descriptive assertion messages
- Mock all external dependencies

## 🚫 Common Pitfalls to Avoid

### Ktlint Violations (CRITICAL)
- **No PascalCase Composables** - Use camelCase for @Composable functions (e.g., `taskScreen`, not `TaskScreen`)
- **No wildcard imports** - Import specific classes only (e.g., `import androidx.compose.material3.Button`, not `import androidx.compose.material3.*`)
- **No factory method confusion** - Only factory methods can start with uppercase letters

### General Code Quality
- **No magic numbers** - Use named constants
- **No nullable platform types** - Explicit nullability
- **No mutable shared state** - Use immutable data classes
- **No blocking operations on main thread** - Use coroutines
- **No hardcoded strings** - Use string resources
- **No direct database access in UI** - Use repository pattern
- **No business logic in composables** - Keep UI pure

## 📚 Key Dependencies

- **Jetpack Compose**: Modern Android UI toolkit
- **Hilt**: Dependency injection framework
- **Room**: Local database persistence
- **Coroutines**: Asynchronous programming
- **Flow**: Reactive data streams
- **Material3**: Design system components
- **Navigation Compose**: Type-safe navigation
- **Coil**: Image loading library

## 🎯 Success Criteria

All generated code must:
1. **Pass KtLint formatting** - Zero violations in `./gradlew ktlintMainSourceSetFormat`
2. Follow DDD, SOLID, and DRY principles
3. Pass Detekt static analysis checks
4. Include comprehensive tests
5. Support accessibility features
6. Maintain child safety standards
7. Follow established project patterns
8. Include proper documentation
9. Handle errors gracefully

### Mandatory Verification
Before considering any code generation task complete:
- Run `./gradlew ktlintMainSourceSetFormat` and ensure it passes
- Verify all @Composable functions use camelCase naming
- Confirm no wildcard imports are used
- Check that all imports are explicit and specific
