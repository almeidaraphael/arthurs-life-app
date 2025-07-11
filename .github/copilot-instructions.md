# GitHub Copilot Instructions – Arthur's Life App

## Project Overview
Arthur's Life is a native Android family task management app built with Kotlin 2.1.0, Java 21, and Jetpack Compose. It features a gamified token-based reward system, role-based access (Child, Caregiver, Admin), and a theme system (Mario Classic, Material Light/Dark).

## Codebase Structure
- **Domain Layer**: Business logic, entities, value objects, use cases (`domain/`)
- **Infrastructure Layer**: Data sources, Room entities/DAOs, repository implementations (`infrastructure/`)
- **Presentation Layer**: Jetpack Compose UI, navigation, viewmodels, theme components (`presentation/`)
- **DI Layer**: Hilt modules (`di/`)
- **Data Layer**: DataStore helpers (`data/`)

## Development Standards - ZERO TOLERANCE POLICY
- **Language**: Kotlin 2.1.0 (explicit null safety), Java 21
- **Architecture**: DDD, Clean Architecture, SOLID, DRY
- **Testing**: JUnit, Espresso, MockK (80%+ coverage required for domain)
- **Static Analysis**: Detekt (ZERO violations - any violation blocks completion)
- **Accessibility**: All UI must support TalkBack, semantic roles, and 4.5:1 color contrast
- **Child Safety**: Input validation, secure storage, COPPA compliance
- **Quality Gate**: NO code is considered complete with ANY build/test/detekt failures

# Kotlin File Structure Guidelines

**Always follow the correct Kotlin file structure:**

- The first line of every Kotlin file must be the `package` declaration.
- Immediately after the package, list all required `import` statements.
- Only after package and imports, write your code: data classes, functions, composables, etc.
- Never write code, comments, or annotations before the package or import declarations.
- This ensures proper compilation and avoids common errors.

**Example:**
```kotlin
package com.example.myapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// Your code starts here
data class MyDataClass(...)

@Composable
fun myComposable(...) { ... }
```

## Detekt Compliance Rules - ZERO VIOLATIONS ACCEPTED
- **Function Naming**: camelCase (including @Composable) - NO EXCEPTIONS
- **Class Naming**: PascalCase - NO EXCEPTIONS  
- **Constants**: SCREAMING_SNAKE_CASE - NO EXCEPTIONS
- **No Wildcard Imports**: Use explicit imports only - NO EXCEPTIONS
- **LongParameterList**: >6 params must use data classes - NO EXCEPTIONS
- **MagicNumber**: Replace with named constants - NO EXCEPTIONS
- **Auto-Format**: Run `./gradlew detektFormat` after every change - MANDATORY
- **Pre-Commit**: Run `./gradlew detekt` and ensure ABSOLUTE ZERO violations
- **CRITICAL**: Any detekt violation immediately invalidates the implementation

## Gradle Command Execution
**MANDATORY:**
- Always check current directory before running any Gradle command
- If not in `android-kotlin`, change to it and log/report the action
- Never assume the current directory

Example:
```zsh
if [ "$(basename $(pwd))" != "android-kotlin" ]; then cd android-kotlin; fi
./gradlew detektFormat
./gradlew detekt
./gradlew build
./gradlew test
```

## Feature Implementation Workflow
1. **Start with Domain**: Define entities, value objects, business rules in `domain/`
2. **Create Use Cases**: Implement business logic as use cases
3. **Add Infrastructure**: Repository implementations in `infrastructure/`
4. **Build UI**: Theme-aware Compose screens in `presentation/screens/`
5. **Add DI**: Wire dependencies in `di/` modules
6. **Test**: Comprehensive unit/integration tests

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
