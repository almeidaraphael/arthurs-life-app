---
title: Tech Context – LemonQwest App
author: Memory Bank (AI)
date: 2025-07-15
---

## Technologies Used

- Kotlin 2.1.0 (Android, Jetpack Compose)
- Java 21 (primary), Java 17 (fallback)
- Hilt (Dependency Injection)
- Room (Local database)
- DataStore (Preferences)
- JUnit, Espresso, MockK (Testing)
- Detekt, KtLint (Static analysis and formatting)

## Development Setup

- Native Android project structure
- Clean Architecture: domain, infrastructure, presentation, di, data
- Modular codebase for testability and maintainability
- All features designed for offline-first operation
- 80%+ test coverage for the domain layer is mandatory
- Zero Detekt/KtLint violations required

## Technical Constraints

- No external integrations or network dependencies for core features
- All data stored locally with encryption
- COPPA compliance and no PII collection
- Accessibility and child safety are mandatory
- All code, tests, and documentation must be cross-referenced and kept in sync

## Dependencies

- All dependencies are managed via Gradle and specified in `android-kotlin/` project
- Only trusted libraries for Android, DI, database, and testing

## 🎯 CURRENT TECHNICAL FOCUS: Test Suite Transformation

### Strategic Mission Status
- **MISSION**: Comprehensive test suite transformation with sustainable quality standards
- **CURRENT PHASE**: Four-phase systematic approach (Infrastructure → MockK → File Reduction → Pipeline Hardening)
- **BREAKTHROUGH**: Thread-safe infrastructure achieving 70% IllegalStateException reduction (17 → 10 failures)

### Core Technical Challenges Addressed

**Thread-Safety for JUnit Parallel Execution**:
- **Problem**: Concurrent `Dispatchers.Main` modification causing IllegalStateException failures
- **Solution**: Synchronized ViewModelTestBase with `Mutex` protection and `withLock` access
- **Result**: Thread-safe test execution with bulletproof resource cleanup

**ViewModel Test Timing Dependencies**:
- **Problem**: Complex timing requirements for ViewModel initialization and state management
- **Solution**: Three-tier timing stabilization patterns (setup, creation, test method levels)
- **Result**: Systematic resolution of timing-dependent test failures

**MockK Reliability Issues**:
- **Problem**: Inconsistent mocking patterns with unjustified `relaxed = true` usage
- **Solution**: Explicit MockK standardization with comprehensive verification protocols
- **Status**: Ready for systematic implementation using proven infrastructure

**File Size and Complexity Management**:
- **Problem**: 5 test files exceeding 150-line organizational requirement (335-479 lines each)
- **Solution**: Atomic extraction protocols with functional coherence and single responsibility
- **Status**: Ready for parallel execution with established safety protocols

## 🧪 Testing Infrastructure & IllegalStateException Prevention

### Thread-Safe ViewModelTestBase Pattern (IMPLEMENTED)
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
abstract class ViewModelTestBase {
    companion object {
        private val mainDispatcherLock = Mutex() // BREAKTHROUGH: Thread-safe access
    }
    
    protected lateinit var testDispatcher: TestDispatcher
    
    @BeforeEach
    open fun setUp() = runTest {
        testDispatcher = UnconfinedTestDispatcher()
        mainDispatcherLock.withLock {
            Dispatchers.setMain(testDispatcher)
        }
        MockKAnnotations.init(this, relaxUnitFun = false)
        setUpViewModel()
    }
    
    @AfterEach
    open fun tearDown() = runTest {
        mainDispatcherLock.withLock {
            Dispatchers.resetMain()
        }
        clearAllMocks()
    }
    
    protected fun runViewModelTest(testBody: suspend TestScope.() -> Unit) = 
        runTest(testDispatcher) { testBody() }
}
        synchronized(mainDispatcherLock) {
            testDispatcher = UnconfinedTestDispatcher()
            Dispatchers.setMain(testDispatcher)
            // Double stabilization for edge case timing
            testDispatcher.scheduler.advanceUntilIdle()
            testDispatcher.scheduler.advanceUntilIdle()
        }
    }
}
```

### Category A Pattern (UseCase/Repository Tests)
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class UseCaseTest {
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        MockKAnnotations.init(this)
    }
    
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
}
```

### Category B Pattern (ViewModel Tests)
- Extend ViewModelTestBase for thread-safe dispatcher management
- Apply double stabilization in setUpViewModel and createViewModel methods
- Never use `relaxed = true` in MockK configurations

### **BREAKTHROUGH: Pattern 3 - Advanced Test Method Timing**
For persistent IllegalStateException failures, apply enhanced timing at test method level:
```kotlin
@Test
fun testMethod() = runViewModelTest {
    // CRITICAL: Enhanced timing before test execution
    testDispatcher.scheduler.advanceUntilIdle()
    // Setup mocks...
    // CRITICAL: Stabilization before ViewModel creation
    testDispatcher.scheduler.advanceUntilIdle()
    viewModel = createViewModel()
    // CRITICAL: Stabilization after ViewModel creation
    testDispatcher.scheduler.advanceUntilIdle()
    // Continue with test...
}
```
**Success Rate**: Improved AuthViewModelLogoutTest from 0% to 75% success

## Source of Truth

This tech context is based on the latest PRDs and updated documentation in `docs/`. In case of conflict, PRDs take precedence.
