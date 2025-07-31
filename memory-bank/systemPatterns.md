---
title: System Patterns – LemonQwest App
author: Memory Bank (AI)
date: 2025-07-15
---

## System Architecture

- Native Android app (Kotlin 2.1.0, Jetpack Compose)
- Clean Architecture with strict separation: Domain → Infrastructure → Presentation
- DDD (Domain-Driven Design) for aggregates: User, Task, Token, Reward, Achievement
- Repository pattern for data access and abstraction
- Offline-first, local data storage with encryption
- Role-based access: Child, Caregiver, Admin
- Theme-aware UI with semantic icon mapping and terminology (role-based: Material for Caregiver/Admin, Mario Classic for Child). Theme system is user-based, persistent (per-user, via DataStore), extensible (dynamic theme registration), and includes error handling and accessibility compliance as per [feature-theme-system.ipd.md].
- All code and architecture must strictly follow DDD, SOLID, and DRY principles
- Java 21 is the primary target, with Java 17 as fallback (explicit troubleshooting and build config steps)
- 80%+ test coverage for the domain layer is mandatory
- Zero Detekt/KtLint violations (static analysis and formatting)
- All UI must be accessible (TalkBack, semantic roles, 4.5:1 contrast, contentDescription for images)

## 🧪 Testing Architecture Patterns

### Thread-Safe Testing Infrastructure (BREAKTHROUGH IMPLEMENTED)

**Core Infrastructure**: Enhanced ViewModelTestBase with synchronized Main dispatcher access for JUnit parallel execution

**Thread-Safety Requirements**:
- **JUnit Parallel Execution**: Enabled for performance but requires synchronized access to global resources
- **Synchronized Main Dispatcher**: Uses `Mutex` with `withLock` to prevent concurrent `Dispatchers.Main` modification
- **Resource Cleanup**: Bulletproof cleanup with proper scope cancellation and mock management

**Category-Based Testing Methodology (PROVEN EFFECTIVE)**:

**Category A Pattern (Repository/UseCase Tests)**:
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class UseCaseTest {
    private lateinit var testDispatcher: TestDispatcher
    
    @BeforeEach
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
    }
    
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
```

**Category B Pattern (ViewModel Tests)**:
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
abstract class ViewModelTestBase {
    companion object {
        private val mainDispatcherLock = Mutex()
    }
    
    protected fun runViewModelTest(testBody: suspend TestScope.() -> Unit) = 
---
}
```

- Case-by-case evaluation with appropriate pattern selection based on test requirements
- Flexible approach for edge cases not fitting Category A or B patterns

```

**Pattern 2: ViewModel Creation Double Stabilization**
```kotlin
private fun createViewModel(): AuthViewModel {
    testDispatcher.scheduler.advanceUntilIdle()
```kotlin
@Test
}
```
### MockK Standardization Patterns (TARGET FOR IMPLEMENTATION)

- **Comprehensive Verification**: Consistent patterns with exact call counts
- **Thread-Safe Mock Management**: Integration with ViewModelTestBase infrastructure
- Incomplete mock definitions relying on default behavior
- Inconsistent verification patterns across similar test scenarios
### File Size and Complexity Management

- **Atomic extraction protocols** for oversized files with functional coherence
- **Single responsibility principle** applied to test file organization
- Atomic verification after each file split (compilation + test execution)
- Clear documentation and cross-references between extracted files
- **No IllegalStateException failures** accepted in test suite
- **Evidence-Based Verification**: All test fixes must be verified with concrete failure count reduction
- **Automated Quality Gates**: File size, code quality, test execution, MockK compliance
- **Comprehensive Validation**: Production-ready test suite with sustainability protocols
- Use of immutable value objects for roles, categories, balances, etc.
- Domain events for cross-aggregate communication (e.g., TaskCompleted, TokensEarned)
- Accessibility and child safety as first-class requirements
- No external integrations or PII collection

- Repository pattern (domain interfaces, infrastructure implementations)
- Observer pattern for real-time updates and progress tracking
- State machine for token economy and achievement unlocks
- Modular, testable components

## Component Relationships

- Task System integrates with Reward and Achievement Systems for token earning and progress
- Task Management System (caregiver) manages assignment, templates, analytics, and multi-child features
- Top Navigation Bar and dialogs are role- and theme-aware, with persistent access to profile/settings

## Source of Truth

All patterns and decisions are based on the latest PRDs and updated documentation in `docs/`. In case of conflict, PRDs override all other documentation.

## Test Isolation Migration Status (2025-07-30)

### 🚨 PARTIAL SUCCESS – MODERN PATTERNS ADOPTED IN 43 FILES
- **LemonQwestTestExtension**: Used in 27 unit tests
- **DatabaseTestBase**: Used in 16 DAO tests
- **ComposeUiTestBase**: Used in 60+ UI/integration tests (appropriate pattern)
- **Legacy Patterns Remain**: 110+ test files still use older/manual patterns
- **Migration Ongoing**: Next steps are to migrate remaining legacy files to modern patterns

### Key Requirements
- All new/migrated tests must use LemonQwestTestExtension or DatabaseTestBase
- Parallel execution is enabled for migrated tests; legacy tests may not be thread-safe
- Evidence-based tracking required for each migration (before/after code, build/test/lint results)

### Source of Truth
- Migration status and evidence are tracked in TEST_ISOLATION_MIGRATION_PLAN.md and CLAUDE.md
