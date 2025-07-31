# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 🚨 CRITICAL CLAUDE CODE OPERATING PRINCIPLES

### ❌ PROHIBITED BEHAVIORS (NEVER)

- 🚫 **NEVER mark tasks as complete without verifiable evidence** - Require concrete proof such as:
  - Build command outputs showing success/failure
  - Test execution results with pass/fail counts
  - Lint reports showing zero violations
  - Actual file diffs showing changes made
  - Error logs or stack traces for debugging

- 🚫 **NEVER abandon tasks due to complexity** - Do not stop, delay, or switch tasks because of:
  - Perceived difficulty or time constraints
  - Complex error messages or build failures
  - Multiple failed attempts or setbacks
  - Subjective assessments of task complexity

- 🚫 **NEVER present assumptions as facts** - Avoid:
  - Guessing what code does without reading it
  - Inferring build results without running commands
  - Assuming test outcomes without execution
  - Deriving conclusions from partial information

- 🚫 **NEVER make inferences without evidence** - Only rely on:
  - Confirmed command outputs and logs
  - Observable file contents and changes
  - Actual test execution results
  - Concrete error messages and stack traces

### ✅ MANDATORY BEHAVIORS (ALWAYS)

- ✅ **ALWAYS request clarification for ambiguity** - Ask for specifics when:
  - Task requirements are unclear or underspecified
  - Error messages need interpretation or context
  - Multiple solution approaches are possible
  - Expected outcomes are not clearly defined

- ✅ **ALWAYS maintain persistent task focus** - Continue working until:
  - Task is completely finished with evidence
  - Explicit instruction is given to stop or pivot
  - All quality gates pass with concrete proof
  - Documentation is updated with progress

- ✅ **ALWAYS document progress thoroughly** - Record:
  - Intermediate steps and their outcomes
  - Command outputs and their interpretation
  - Failed attempts and lessons learned
  - Final results with verifiable evidence

- ✅ **ALWAYS use evidence-based debugging** - Prioritize:
  - Root-cause analysis from concrete outputs
  - Stack trace examination and interpretation
  - Failed assertion analysis and resolution
  - Build log analysis for error identification

## 🔧 Essential Build Commands

**CRITICAL**: Always run commands from the project root directory. All build operations use the comprehensive Makefile.

### Development Workflow
```bash
# Setup (one-time)
make setup

# Complete quality pipeline (mandatory before commits)
make copilot-pipeline    # format → lint → build → test → install

# Individual operations
make format              # Format code with detekt
make lint               # Static analysis (ZERO violations required)  
make build              # Build all variants
make test               # Run all tests
make install            # Install debug APK
```

### Testing Commands
```bash
make test-unit              # Unit tests only
make test-integration       # Instrumentation tests (requires device)
make test-coverage          # Generate coverage report
make test-filter PATTERN="*TestName*"  # Run specific tests
make test-class CLASS="MyTestClass"     # Run specific test class
```

### Quality Assurance
```bash
make qa                     # Complete QA pipeline
make clean                  # Clean build artifacts
make deep-clean            # Deep clean (cache, docs, daemons)
```

## 🏗️ Architecture Overview

### Clean Architecture + Domain-Driven Design
- **Domain Layer** (`domain/`): Business logic, entities, use cases
- **Infrastructure Layer** (`infrastructure/`): Repository implementations, database, data sources  
- **Presentation Layer** (`presentation/`): Jetpack Compose UI, ViewModels, navigation

### Key Domain Aggregates
- **User** (`domain/user/`): User entities, roles (Child/Caregiver/Admin), PIN authentication
- **Task** (`domain/task/`): Task management, completion tracking, categories
- **Token** (`domain/user/TokenBalance.kt`): Token economy for rewards
- **Reward** (`domain/reward/`): Reward catalog and redemption system
- **Achievement** (`domain/achievement/`): Milestone tracking and unlocking
- **Theme** (`domain/theme/`): User-based theme selection system

### Project Structure
```
android-kotlin/app/src/main/java/com/lemonqwest/app/
├── domain/           # Business logic (DDD aggregates)
├── infrastructure/   # Data layer (Room, DataStore, repositories)  
├── presentation/     # UI layer (Compose, ViewModels, navigation)
├── di/              # Hilt dependency injection modules
└── data/            # Theme preferences and data stores
```

## 🧪 Testing Architecture

### Test Structure
- **Unit Tests** (`src/test/`): Domain logic, use cases, ViewModels
- **Integration Tests** (`src/androidTest/`): Database, UI flows, end-to-end scenarios

### Critical Testing Patterns

#### Modern LemonQwest Test Pattern (REQUIRED FOR ALL UNIT TESTS)
```kotlin
// Use for ALL unit tests - provides complete isolation and parallel safety
@OptIn(ExperimentalCoroutinesApi::class)
class MyTest {
    @RegisterExtension
    @JvmField
    val testExtension = LemonQwestTestExtension()

    private lateinit var mockDependencies: MockedDependencies

    @BeforeEach
    fun setUp() {
        mockDependencies = mockk()
        // Setup mock behavior with explicit every/coEvery blocks
        // MockK initialization and cleanup handled automatically by extension
    }

    @Test
    fun `should test specific behavior`() = runTest {
        // Test implementation with complete isolation
        // Dispatcher management handled automatically
    }
}
```

#### Android Instrumentation Test Pattern (REQUIRED FOR INTEGRATION TESTS)
```kotlin
// Use for androidTest integration tests with Hilt and database
@HiltAndroidTest
class MyIntegrationTest : AndroidTestBase() {
    
    @Test
    fun `should test integration scenario`() = runTest {
        // Complete isolation with Hilt injection, database cleanup, and dispatcher management
    }
}

// For database-specific tests
class MyDaoTest : DatabaseTestBase() {
    
    @Test
    fun `should test database operation`() = runTest {
        val dao = database.myDao()
        // Fresh in-memory database per test with complete cleanup
    }
}

// For ViewModel tests in androidTest (legacy compatibility)
class MyViewModelTest : ViewModelTestBase() {
    
    protected fun runViewModelTest(testBody: suspend TestScope.() -> Unit) {
        // Synchronized dispatcher management addressing TestMainDispatcher.kt:66 errors
    }
}
```

#### Domain Tests (No Coroutines)
```kotlin
// Use for simple domain logic without coroutines
class MyDomainTest {
    @Test
    fun `should validate domain logic`() {
        // Direct testing without dispatcher setup
    }
}
```

### Test Infrastructure - Modern Isolation Patterns ✅
**STATUS**: Complete test isolation system with standardized patterns across all test types.

#### Test Pattern Requirements
- **Unit Tests**: MUST use `LemonQwestTestExtension` for complete isolation
- **Integration Tests**: MUST use `AndroidTestBase` for Hilt automation and resource management
- **Database Tests**: MUST use `DatabaseTestBase` for fresh in-memory databases
- **UI Tests**: Use `ComposeUiTestBase` for appropriate Compose testing patterns

#### Test Isolation Principles
- **Perfect Isolation**: All tests run independently with zero state bleeding
- **Parallel Safety**: All tests designed for concurrent execution
- **Automated Resource Management**: No manual setup/cleanup required
- **Consistent Patterns**: Standardized approaches across all test types

#### Prohibited Legacy Patterns
- **❌ FORBIDDEN**: Manual MockK setup (`MockKAnnotations.init()`, `unmockkAll()`)
- **❌ FORBIDDEN**: Manual Hilt setup (`@HiltAndroidTest` + `HiltAndroidRule`)
- **❌ FORBIDDEN**: Basic JUnit 5 without proper isolation
- **❌ FORBIDDEN**: Any patterns that cause TestMainDispatcher.kt:66 errors

#### Test Quality Standards
- **Zero State Bleeding**: Tests must not affect each other
- **Automated MockK Management**: All mocking handled by test extensions
- **Build Pipeline Compliance**: All tests must pass format, lint, and build checks
- **Thread Safety**: All tests must be safe for parallel execution

## 🎨 Theme System

### User-Based Themes (All users can select any theme)
- **Material Light** (default): Professional task management interface
- **Material Dark**: Dark mode variant with proper contrast
- **Mario Classic**: Gamified experience with Mario-themed terminology and visuals

### Theme Implementation
- All UI components must support theme switching via semantic mapping
- Theme-aware terminology adapts based on selected theme
- Accessibility compliance required (4.5:1 contrast ratio minimum)

## 🔒 Security & Safety Standards

- **COPPA Compliance**: Child safety and privacy protection
- **Input Validation**: All user inputs validated at domain boundaries
- **Secure Storage**: Android Keystore for sensitive data (PINs, tokens)
- **No External Dependencies**: Offline-first architecture

## 💻 Technology Stack

### Core Technologies
- **Language**: Kotlin 2.1.0 with Java 21 (fallback Java 17)
- **UI**: Jetpack Compose with Material Design 3
- **Database**: Room with SQLite, offline-first
- **DI**: Hilt with feature-based modules
- **Navigation**: Navigation Compose
- **State Management**: ViewModels with coroutines

### Build Tools
- **Build System**: Gradle with version catalogs
- **Quality Tools**: Detekt (static analysis), JaCoCo (coverage)
- **Testing**: JUnit 5, MockK, Espresso
- **Target**: Android API 24+, Target SDK 35

## 📝 Code Standards

### Kotlin Style
- **Classes**: PascalCase (`TaskRepository`, `UserViewModel`)
- **Functions**: camelCase (`createTask()`, `validateInput()`)
- **Constants**: SCREAMING_SNAKE_CASE (`MAX_TASK_NAME_LENGTH`)
- **Explicit imports**: No wildcard imports allowed
- **Null safety**: Explicit nullability declarations required

### Architecture Rules
- Business logic only in domain layer
- UI logic only in presentation layer  
- Data access only in infrastructure layer
- Dependencies point toward domain (Clean Architecture)
- Repository pattern for all data access
- Immutable data classes for value objects

### Quality Requirements
- **Testing**: Essential business rules coverage with simple, maintainable, scalable tests that run in parallel with perfect isolation
- **Linting**: Zero Detekt violations (enforced)
- **Test Quality**: Tests must be non-overengineered, focused on critical business logic validation
- **Documentation**: KDoc for public APIs

## 🚨 Build Enforcement & Evidence Requirements

### Zero-Tolerance Policy with Evidence Verification
All commands in the build pipeline MUST pass with zero failures AND provide concrete evidence:

1. **`make format`** - Must format successfully
   - ✅ **Evidence Required**: Command output showing "✅ Code formatting complete!"
   - ❌ **Invalid**: Assuming formatting worked without running command

2. **`make lint`** - ZERO violations required (HARD STOP)
   - ✅ **Evidence Required**: Detekt report showing "✅ Static analysis complete!" with zero violations
   - ❌ **Invalid**: Claiming "looks clean" without running lint command

3. **`make build`** - Must build successfully (HARD STOP)  
   - ✅ **Evidence Required**: Build output showing "✅ Build complete!" with no compilation errors
   - ❌ **Invalid**: Inferring build success from lack of visible errors

4. **`make test`** - ZERO test failures (HARD STOP)
   - ✅ **Evidence Required**: Test execution results showing pass/fail counts and "✅ All tests completed!"
   - ❌ **Invalid**: Assuming tests pass without running them

5. **`make install`** - Must install successfully (HARD STOP)
   - ✅ **Evidence Required**: Installation output showing "✅ Debug APK installed!"
   - ❌ **Invalid**: Guessing installation worked without device confirmation

### Task Completion Requirements
**NEVER mark work complete without ALL of the following:**
- ✅ Concrete command outputs demonstrating success
- ✅ Actual file diffs showing changes made  
- ✅ Test execution results with pass/fail evidence
- ✅ Build logs confirming zero violations/errors

## 🔍 Common Development Tasks

### Adding New Features
1. **Domain First**: Create entities, value objects, use cases
2. **Infrastructure**: Implement repositories, DAOs, data sources
3. **Presentation**: Build Compose screens with theme support
4. **DI**: Wire dependencies in appropriate Hilt modules
5. **Testing**: Essential business rules tests, integration tests for critical flows
6. **Validation**: Run complete build pipeline

### Running Single Tests
```bash
# Use gradle directly for specific test classes (Make targets not available)
./gradlew testDebugUnitTest --tests "com.lemonqwest.app.domain.task.TaskRepositoryTest"

# Run specific test packages
./gradlew testDebugUnitTest --tests "com.lemonqwest.app.domain.task.*"

# Run all unit tests with continue on failure
./gradlew testDebugUnitTest --continue
```

### Test Suite Status & Debugging
```bash
# Current test suite status:
# - All test patterns use modern isolation approaches
# - 100% standardized pattern coverage across all test types
# - Perfect test isolation with zero state bleeding
# - All tests safe for parallel execution

# Debug test issues
./gradlew clean              # Clean build artifacts
./gradlew build              # Rebuild project
```

## 🗂️ File Organization

### Domain Layer Organization  
- Each aggregate has its own package (`user/`, `task/`, `reward/`, etc.)
- Use cases in `usecase/` subdirectories within aggregates
- Shared concepts in `common/` package

### Testing Organization
- Unit tests mirror main source structure
- Test utilities in `testutils/` package  
- Integration tests in `androidTest/` with automated Hilt setup via `AndroidTestBase`
- UI tests use `ComposeUiTestBase` for Compose testing with proper isolation

### Test Debugging Guide
**Modern Pattern Requirements:**
- **Unit Tests**: Always use `@OptIn(ExperimentalCoroutinesApi::class)` and `LemonQwestTestExtension`
- **Integration Tests**: Always extend `AndroidTestBase` for proper Hilt integration
- **Database Tests**: Always extend `DatabaseTestBase` for fresh database per test
- **MockK Usage**: Use explicit `every { }` blocks, avoid relaxed mocking

**Common Setup Requirements:**
1. Unit tests need `@OptIn(ExperimentalCoroutinesApi::class)` → Add annotation + import
2. MockK methods need imports → Add `import io.mockk.any`, `import io.mockk.every`
3. All mocks need explicit behavior → Use `every { }` blocks
4. Test extensions required → Use `@RegisterExtension` with appropriate base class

### Navigation Structure
- Bottom navigation for role-based screens
- Top navigation bar with user switching and theme selection
- Role-specific routing (Child/Caregiver/Admin different tabs)

## ⚡ Performance Considerations

### Jetpack Compose Best Practices
- Use `remember` for expensive calculations
- State hoisting for reusable components  
- LazyColumn for large lists
- Proper key usage in lists

### Database Optimization
- Efficient Room queries with proper indexing
- Flow-based reactive data loading
- Minimal data transfers between layers

### Memory Management
- Proper coroutine cancellation in ViewModels
- Resource cleanup in lifecycle-aware components
- Efficient image handling for avatars

This codebase follows enterprise-grade development practices with comprehensive quality gates, clear architectural boundaries, and extensive testing coverage. Always run the complete build pipeline before committing changes.