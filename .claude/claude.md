# Claude Code Instructions - Arthur's Life App

## 🚨 CRITICAL EXECUTION PRINCIPLES

### Action-First Approach
- **ACT IMMEDIATELY**: Execute user requests using tools—never just provide suggestions
- **NO EXPLANATIONS WITHOUT ACTION**: If you can do something, do it first, then explain
- **USE TOOLS AGGRESSIVELY**: Prefer available tools over text responses
- **REQUEST CLARIFICATION ONLY AS LAST RESORT**: Try multiple approaches before asking questions

### File Editing Rules
- **USE PROPER TOOLS**: Use edit tools—never show code blocks for file changes
- **INCLUDE CONTEXT**: When replacing strings, include 3-5 lines before/after for uniqueness
- **VERIFY CHANGES**: Check files after editing to ensure changes applied correctly
- **KTLINT COMPLIANCE**: All code changes must follow ktlint formatting rules

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

## 🎯 Project Overview

**Arthur's Life** is a native Android Kotlin family task management application designed to help families organize daily tasks through a gamified token-based reward system. The app supports role-based access (Child, Caregiver, Admin) with comprehensive task management and progress tracking built using Domain-Driven Design principles.

### Core Features to Build
- **Task Management**: Create, assign, and complete age-appropriate tasks
- **Token Economy**: Digital currency system for completing tasks
- **Reward System**: Redeem tokens for digital and physical rewards
- **Family Management**: Multi-child support with caregiver oversight
- **Progress Tracking**: Individual and family analytics
- **Achievement System**: Milestone recognition and motivation

## Java/JDK Requirements

**CRITICAL**: This project requires Java 21 (preferred) or Java 17 (fallback) for Android development.

### Required Java Version
- **Java 21** (LTS) - Preferred for latest features and performance
- **Java 17** (LTS) - Fallback if compatibility issues arise with dependencies
- **JDK 21 or JDK 17** (Oracle JDK or OpenJDK)

### Installation Commands

#### Linux/Ubuntu
```bash
# Install OpenJDK 21 (Preferred)
sudo apt update && sudo apt install openjdk-21-jdk

# Install OpenJDK 17 (Fallback)
sudo apt update && sudo apt install openjdk-17-jdk

# Verify installation
java -version && javac -version
```

#### macOS
```bash
# Java 21 (Preferred)
brew install openjdk@21
export JAVA_HOME="/opt/homebrew/opt/openjdk@21"

# Java 17 (Fallback)
brew install openjdk@17
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
```

#### Arch Linux
```bash
# Java 21 (Preferred)
sudo pacman -S jdk21-openjdk

# Java 17 (Fallback)
sudo pacman -S jdk17-openjdk
```

### Android Studio Configuration
- Set Project SDK to Java 21 (preferred) or Java 17 (fallback)
- Configure Gradle JVM to use Java 21 (preferred) or Java 17 (fallback)
- Update `gradle.properties` if needed:
```properties
org.gradle.java.home=/path/to/java21  # or /path/to/java17
```

## Technology Stack

### Core Framework
- **Kotlin 2.1.0** as primary programming language
- **Java 21+ (preferred) / Java 17+ (fallback)** for Android development
- **Jetpack Compose** for modern declarative UI
- **Android SDK API 24+** with target SDK 35
- **Gradle Kotlin DSL** for build configuration

### Architecture & State Management
- **Domain-Driven Design (DDD)** with Clean Architecture
- **MVVM Architecture** with ViewModels and StateFlow
- **Hilt (Dagger)** for dependency injection
- **Kotlin Coroutines + Flow** for reactive programming

### Data & Storage
- **Room Database** for local data persistence
- **DataStore Preferences** for settings and user preferences
- **Repository Pattern** with reactive data streams

### Testing & Quality
- **JUnit 5** for unit testing
- **MockK** for mocking in Kotlin
- **Espresso** for UI testing
- **Detekt** for static code analysis
- **Ktlint** for code formatting

### UI & Design
- **Material Design 3** theming and components
- **Jetpack Navigation Compose** for navigation
- **TalkBack support** for basic accessibility

## Architecture Principles

### Domain-Driven Design (DDD)

#### Core Components
- **Aggregate Roots**: User, Task, Token, Reward entities with business logic
- **Value Objects**: UserRole, TaskCategory, TaskDifficulty for type safety
- **Domain Events**: TaskCompleted, TokensEarned, RewardRedeemed for side effects
- **Repository Pattern**: Clean data access interfaces separated from implementation

#### DDD Implementation Rules
1. **Aggregate Boundaries**: Each aggregate manages its own consistency
2. **Domain Events**: Use events for cross-aggregate communication
3. **Value Objects**: Immutable objects for concept representation
4. **Domain Services**: Business logic that doesn't belong to entities

### SOLID Principles

#### Single Responsibility Principle (SRP)
- Each class has one reason to change
- Separate concerns into focused components
- Example: `TaskRepository` only handles task data operations

#### Open/Closed Principle (OCP)
- Open for extension, closed for modification
- Use interfaces and inheritance for extensibility
- Example: `RewardProvider` interface for different reward types

#### Liskov Substitution Principle (LSP)
- Subtypes must be substitutable for base types
- Maintain contracts in inheritance hierarchies
- Example: All `RewardProvider` implementations work interchangeably

#### Interface Segregation Principle (ISP)
- Clients shouldn't depend on interfaces they don't use
- Create focused, cohesive interfaces
- Example: `TaskReader` and `TaskWriter` instead of combined `TaskRepository`

#### Dependency Inversion Principle (DIP)
- Depend on abstractions, not concretions
- High-level modules shouldn't depend on low-level modules
- Example: Domain layer depends on repository interfaces, not implementations

### DRY (Don't Repeat Yourself)

#### Code Reuse Strategies
- **Shared Components**: Reusable Compose components for common UI patterns
- **Common Logic**: Centralized business rules and validation functions
- **Utility Functions**: Shared extensions and helper methods
- **Configuration**: Single source of truth for app constants and settings

#### Implementation Guidelines
```kotlin
// Bad: Duplicated validation logic
class TaskCreator {
    fun createTask(title: String) {
        if (title.length < 3 || title.length > 50) throw ValidationException()
        // create task
    }
}

class TaskUpdater {
    fun updateTask(title: String) {
        if (title.length < 3 || title.length > 50) throw ValidationException()
        // update task
    }
}

// Good: Shared validation
object TaskValidation {
    fun validateTitle(title: String) {
        if (title.length < 3 || title.length > 50) throw ValidationException()
    }
}
```

## Project Structure

```
android-kotlin/app/src/main/java/com/arthurslife/app/
├── domain/              # Business logic and entities
│   ├── user/           # User management and authentication
│   ├── task/           # Task creation, assignment, completion
│   ├── token/          # Token economy and calculations
│   └── reward/         # Reward catalog and redemption
├── data/               # Data layer implementation
│   ├── local/         # Room database and DataStore
│   ├── repository/    # Repository implementations
│   └── mapper/        # Domain/data model mapping
├── presentation/       # UI layer with Jetpack Compose
│   ├── screens/       # Screen composables and ViewModels
│   ├── components/    # Reusable UI components
│   ├── theme/         # Material Design 3 theme
│   └── navigation/    # Navigation configuration
├── di/                # Hilt dependency injection modules
└── util/              # Utility classes and extensions
```

## Development Commands

### Essential Commands
```bash
# Development
cd android-kotlin
./gradlew build              # Build the Android project
./gradlew installDebug       # Install debug APK on connected device
./gradlew assembleDebug      # Create debug APK

# Quality Assurance
./gradlew ktlintCheck        # Kotlin code formatting check
./gradlew ktlintFormat       # Auto-format Kotlin code
./gradlew detekt             # Static code analysis
./gradlew check              # Run all checks

# Testing
./gradlew test               # Run unit tests
./gradlew connectedAndroidTest # Run instrumented tests
```

### Java Version Verification
```bash
# Check Java version (should show Java 21 preferred, Java 17 fallback)
java -version
./gradlew -version
./gradlew compileDebugKotlin
```

## Key Features to Build

### Core Features (MVP)
- **Multi-User System**: Child, Caregiver, Admin roles with permission levels
- **Token Economy**: Children earn tokens for completed tasks, spend on rewards
- **Task Management**: Create, schedule, and track daily tasks with categories
- **Reward System**: Predefined and custom rewards with token redemption
- **Progress Tracking**: Basic analytics and completion monitoring
- **Achievement System**: Core gamification with essential achievements

### Advanced Features (Future)
- **Wishlist System**: Save towards higher-cost items with progress tracking
- **Global Achievement System**: Comprehensive achievement framework
- **Multi-Child Support**: Manage multiple children from single caregiver interface
- **Advanced Analytics**: Comprehensive reporting and insights
- **Enhanced Accessibility**: Advanced screen reader support and customization
- **Data Management**: Backup, restore, and export capabilities

## Development Guidelines

### Code Quality Requirements
- **Always verify Java 21+ (preferred) or Java 17+ (fallback)** before building
- **Run `./gradlew ktlintFormat`** before every commit
- **Pass `./gradlew detekt`** static analysis checks
- **Maintain 80%+ test coverage** for domain layer
- **Follow DDD, SOLID, and DRY principles** in all implementations

### Implementation Notes
- Focus on family task management, not specific individuals
- Consider Child, Caregiver, and Admin roles in all features
- Prioritize child safety and data protection
- Build with offline-first approach using Room database
- Use Jetpack Compose for modern, maintainable UI
- Include basic TalkBack support for accessibility

### Security & Privacy
- **Child Safety First**: All decisions prioritize child wellbeing and safety
- **Data Minimization**: Collect only essential information for app functionality
- **Local Storage**: Primary data storage on device, minimal cloud dependency
- **Secure Authentication**: PIN-based system with Android Keystore integration
- **Input Validation**: Comprehensive validation of all user inputs

## Claude Code Specific Guidelines

### When generating domain entities:
- Include proper validation in constructors
- Implement value object patterns where appropriate
- Add domain events for significant business operations
- Follow immutability principles using Kotlin data classes

### When generating repository implementations:
- Use suspend functions for async operations
- Implement comprehensive error handling with Result types
- Add structured logging for debugging and monitoring
- Follow repository interface contracts defined in domain layer

### When generating Compose UI:
- Use state-first approach with proper state hoisting
- Implement error boundaries and loading states
- Add basic accessibility with content descriptions
- Follow Material Design 3 guidelines and theming

### When generating tests:
- Include setup and teardown methods for test isolation
- Test edge cases and error conditions thoroughly
- Use descriptive assertion messages and test names
- Mock all external dependencies using MockK

### Common Pitfalls to Avoid
- **No wildcard imports** - Import specific classes only (ktlint enforced)
- **No PascalCase Composables** - Use camelCase for @Composable functions (ktlint enforced)
- **No magic numbers** - Use named constants for all values
- **No nullable platform types** - Explicit nullability declarations
- **No mutable shared state** - Use immutable data classes and sealed classes
- **No blocking operations on main thread** - Use coroutines for async work
- **No hardcoded strings** - Use string resources for all user-facing text
- **No direct database access in UI** - Use repository pattern consistently
- **No business logic in composables** - Keep UI components pure and focused
- **No ktlint violations** - All code must pass `./gradlew ktlintMainSourceSetFormat`

## Success Criteria

All generated code must:
1. Follow DDD, SOLID, and DRY architectural principles
2. Pass KtLint formatting and Detekt static analysis checks
3. Include comprehensive unit tests with meaningful assertions
4. Support basic accessibility features with TalkBack compatibility
5. Maintain child safety standards and secure data handling
6. Follow established project patterns and naming conventions
7. Include proper documentation and clear code comments
8. Handle errors gracefully with appropriate user feedback
