# GitHub Copilot Instructions - Arthur's Life App

## 🎯 Project Overview

**Arthur's Life** is a family task management Android app built with Kotlin and Jetpack Compose. It uses a gamified token-based reward system to help families organize daily tasks with role-based access for Children, Caregivers, and Admins.

### Current Implementation Status
- ✅ **Theme System**: Role-based theming with Mario Classic and Material Dark/Light themes
- ✅ **Authentication**: PIN-based role switching between Child/Caregiver/Admin modes  
- ✅ **Navigation**: Basic navigation structure with role-specific screens
- ✅ **Architecture Foundation**: Domain-driven design with clean architecture layers
- ❌ **Core Features**: Task management, token economy, rewards, achievements (NOT YET IMPLEMENTED)

### Technology Stack
- **Platform**: Native Android (API 24+, Target SDK 35)
- **Language**: Kotlin 2.1.0 with Java 21
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: Domain-Driven Design (DDD) with Clean Architecture
- **Database**: Room with SQLite (offline-first)
- **DI**: Hilt
- **Static Analysis**: Detekt (replaces KtLint)
- **Testing**: JUnit, Espresso

## 🏗️ Architecture Principles

### Domain-Driven Design (DDD)
- **Aggregates**: User, Task, Achievement, Token, Reward
- **Value Objects**: UserRole, TaskCategory, TaskDifficulty, AchievementType
- **Domain Events**: TaskCompleted, TokensEarned, RewardRedeemed, AchievementUnlocked
- **Repository Pattern**: Clean separation between domain and infrastructure

### SOLID Principles Applied
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Extensible design for new features
- **Liskov Substitution**: Proper inheritance hierarchies
- **Interface Segregation**: Focused, cohesive interfaces
- **Dependency Inversion**: Depend on abstractions, not concretions

### DRY (Don't Repeat Yourself)
- Reusable Compose components
- Centralized business logic
- Shared utility functions
- Common validation rules

## 🛠️ Development Standards

### Code Quality Requirements
- **Java 21** (preferred) or **Java 17** (fallback)
- **Kotlin 2.1.0** with explicit null safety
- **Detekt compliance** for formatting and static analysis
- **Unit test coverage** minimum 80% for domain layer

### Detekt Compliance Rules (CURRENT STANDARD)
- **Function Naming**: Follow camelCase for functions, PascalCase for classes
- **No Wildcard Imports**: Use specific imports instead of `import package.*`
- **Explicit Imports**: Import each class/function explicitly
- **LongParameterList**: Functions with more than 6 parameters should use data classes to group parameters
- **MagicNumber**: Replace magic numbers with named constants
- **Auto-Format Required**: Run `./gradlew detektFormat` after every code change
- **Pre-Commit Check**: Ensure `./gradlew detekt` passes before committing

### Quick Detekt Commands (for fixing errors)
```bash
# Navigate to Android project
cd android-kotlin

# Run detekt analysis and see errors
./gradlew detekt

# Check specific detekt report for detailed errors
cat app/build/reports/detekt/detekt.xml

# Common fixes:
# - LongParameterList: Create data classes to group 7+ parameters
# - MagicNumber: Define constants for numeric literals
# - WildcardImport: Replace import com.example.* with specific imports
```

### Build & Quality Commands
```bash
# Navigate to Android project
cd android-kotlin

# Format code with Detekt
./gradlew detektFormat

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
- **Composables**: camelCase (`taskScreen()`, `userProfileCard()`)
- **Constants**: SCREAMING_SNAKE_CASE (`MAX_TASK_NAME_LENGTH`)
- **Packages**: lowercase (`com.arthurslife.app.domain.task`)

## 📁 Project Structure

```
android-kotlin/app/src/main/java/com/arthurslife/app/
├── domain/              # Business logic and entities
│   ├── user/           # User roles and authentication
│   ├── task/           # Task domain (basic structure exists)
│   ├── achievement/    # Achievement domain (basic structure exists)
│   ├── auth/           # Authentication domain
│   ├── theme/          # Theme domain models
│   └── common/         # Shared domain models and exceptions
├── infrastructure/     # Data layer implementation
│   ├── database/       # Room entities and DAOs
│   ├── preferences/    # DataStore implementations
│   ├── auth/           # Auth repository implementations
│   ├── task/           # Task repository implementations
│   ├── achievement/    # Achievement repository implementations
│   └── user/           # User repository implementations
├── presentation/       # UI layer with Compose
│   ├── screens/        # Screen composables (Home, Profile, Task Management, etc)
│   ├── theme/          # Theme system and theme-aware components
│   ├── navigation/     # Navigation configuration
│   └── viewmodels/     # ViewModels (Auth, Task, Achievement)
├── data/               # Data layer helpers
│   └── theme/          # Theme preferences data store
└── di/                 # Hilt dependency injection modules
```

## 👥 User Roles & Implementation

### Child Role
- **Primary Theme**: Mario Classic (gamified experience)
- **Screens**: Home, Tasks, Rewards, Achievements, Profile
- **Capabilities**: View and complete tasks, see token balance, browse/redeem rewards

### Caregiver Role
- **Primary Theme**: Material Light/Dark (professional interface)
- **Screens**: Dashboard, Task Management, Progress, Children, Profile
- **Capabilities**: Create/manage tasks, set rewards, monitor children's progress

### Admin Role
- **Access**: Full system administration
- **Capabilities**: Manage family structure, assign permissions, system settings

## 📱 UI Development Guidelines

### Jetpack Compose Standards
- Use `@Composable` functions for UI components
- Apply `@Preview` annotations for design verification
- Follow Material Design 3 principles
- Implement proper state hoisting patterns

### Theme System Requirements
- All UI components must support theme switching
- Use semantic icon mapping for theme-specific icons
- Mario Classic theme uses game terminology (Quests, Coins, etc.)
- Material themes use standard terminology (Tasks, Tokens, etc.)

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

## � Implementation Guidelines

### When Adding New Features
1. **Start with Domain**: Define entities, value objects, and business rules in `domain/`
2. **Create Use Cases**: Implement business logic as use cases
3. **Add Infrastructure**: Repository implementations in `infrastructure/`
4. **Build UI**: Theme-aware Compose screens in `presentation/screens/`
5. **Add DI**: Wire dependencies in `di/` modules
6. **Test**: Comprehensive unit and integration tests

### Current Development Priorities (MVP)
1. **Task Management System**
    - Implement task creation, assignment, completion
    - Build TaskRepository and TaskDao
    - Create task management screens

2. **Token Economy**
    - Implement token earning through task completion
    - Build token balance tracking
    - Create spending mechanisms

3. **Basic Reward System**
    - Implement reward catalog
    - Build token redemption system
    - Create reward management screens

4. **Achievement System**
    - Implement basic achievement tracking
    - Build achievement unlock logic
    - Create achievement display screens

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
- Support theme switching (Mario Classic vs Material themes)

### When generating tests:
- Include setup and teardown methods
- Test edge cases and error conditions
- Use descriptive assertion messages
- Mock all external dependencies

## 🚫 Common Pitfalls to Avoid

### Detekt Violations (CRITICAL)
- **Follow camelCase for functions** - Including @Composable functions (e.g., `taskScreen`, not `TaskScreen`)
- **No wildcard imports** - Import specific classes only (e.g., `import androidx.compose.material3.Button`, not `import androidx.compose.material3.*`)
- **Explicit imports** - Import each class/function explicitly

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
1. **Pass Detekt formatting** - Zero violations in `./gradlew detekt`
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
- Run `./gradlew detektFormat` and ensure it passes
- Run `./gradlew detekt` for static analysis
- Verify all @Composable functions use camelCase naming
- Confirm no wildcard imports are used
- Check that all imports are explicit and specific

### 🔧 Detekt Error Fixing Guide

When encountering detekt errors, follow this systematic approach:

#### Quick Fix Command
```bash
cd android-kotlin && ./gradlew detekt
```

#### Common Error Types & Solutions

**1. LongParameterList (7+ parameters)**
- **Solution**: Create data classes to group related parameters
- **Example**: Replace `fun myFunction(a: String, b: Int, c: Boolean, d: List<String>, e: (String) -> Unit, f: () -> Unit, g: Boolean)` 
- **With**: Create `data class MyFunctionParams(...)` and use `fun myFunction(params: MyFunctionParams)`

**2. MagicNumber (hardcoded numbers)**
- **Solution**: Define named constants
- **Example**: Replace `Text(modifier = Modifier.padding(16.dp))` 
- **With**: `private const val DEFAULT_PADDING = 16` and `Text(modifier = Modifier.padding(DEFAULT_PADDING.dp))`

**3. WildcardImport (import com.example.*)**
- **Solution**: Replace with explicit imports
- **Example**: Replace `import androidx.compose.material3.*` 
- **With**: Individual imports like `import androidx.compose.material3.Button`

#### When Asked to "run detekt and fix errors"
1. Navigate to `android-kotlin/` directory
2. Run `./gradlew detekt`
3. Read errors from `app/build/reports/detekt/detekt.xml`
4. Fix each error type systematically:
   - **LongParameterList**: Group parameters into data classes
   - **MagicNumber**: Extract constants with meaningful names
   - **WildcardImport**: Use explicit imports
5. Re-run detekt to verify fixes
6. Report summary of fixes applied

## 🧭 Context Guidelines for GitHub Copilot

### Current Project State
- Theme system and authentication are fully implemented
- Navigation structure exists but screens are mostly placeholders
- Domain models exist but business logic is missing
- Database schema is incomplete
- Core features (tasks, tokens, rewards, achievements) need implementation

### When Asked to Implement Features
1. Check existing patterns in theme system and authentication code
2. Follow DDD structure: domain → infrastructure → presentation  
3. Maintain theme compatibility across all new UI
4. Add proper error handling and validation
5. Write tests for new business logic

### File Patterns to Follow
- Domain interfaces in `domain/[feature]/`
- Repository implementations in `infrastructure/[feature]/`
- ViewModels in `presentation/viewmodels/`
- Theme-aware components in `presentation/theme/components/`
- DI modules in `di/`

### Key Files for Reference
- `presentation/theme/` - Theme system implementation patterns
- `presentation/navigation/MainAppNavigation.kt` - Navigation structure
- `domain/user/` and `infrastructure/auth/` - Complete implementation examples
- `di/AuthModule.kt` - Dependency injection patterns
