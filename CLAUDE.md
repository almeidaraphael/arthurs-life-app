# LemonQwest App - Claude Code Instructions

## 📋 Project Overview

**LemonQwest** is a family task management Android app built with Clean Architecture and Domain-Driven Design. It provides a gamified token-based reward system with role-based access for Children, Caregivers, and Admins.

**CRITICAL FOR CLAUDE CODE**: This document contains mandatory safeguards, zero-tolerance build policies, and test anti-patterns specifically designed to prevent over-engineering and breaking changes during AI-assisted development.

## 🤖 Claude Code Safeguards & Operating Principles

### 🛡️ Mandatory Pre-Flight Checks (BEFORE ANY CODE CHANGES)

**CRITICAL**: Claude Code must perform these checks before any test utility modifications, refactoring, or bulk file changes:

#### **1. Reference Analysis (Required for ALL Method/Class Changes)**

```bash
# ALWAYS run before removing/modifying methods or classes
grep -r "MethodName" android-kotlin/app/src/test
grep -r "ClassName" android-kotlin/app/src/test
grep -r "utilityName" android-kotlin/app/src/androidTest
```

#### **2. Compilation Verification (Required EVERY Step)**

```bash
# ALWAYS verify compilation still works after EACH change
make build
```

#### **3. Impact Assessment (Required for ALL Refactoring)**

- Count how many files will be affected BEFORE making changes
- Identify if methods are part of public APIs
- Check if utilities are used in androidTest vs test directories  
- Verify if methods are referenced in comments/documentation
- Create backup files (.backup) for significant changes

### 🚨 Breaking Change Prevention Protocol

#### **Red Flag Situations - STOP IMMEDIATELY**

Claude Code must **HALT ALL OPERATIONS** and request user intervention when encountering:

- ❌ **Mass deletions**: Removing >5 methods simultaneously
- ❌ **Wholesale file removal**: Deleting entire utility files without incremental verification
- ❌ **Bulk refactoring**: Modifying >5 files simultaneously
- ❌ **Signature changes**: Changing method signatures without updating ALL call sites
- ❌ **Base class removal**: Removing test base classes without complete migration plan
- ❌ **Unverified references**: Finding method usage but proceeding with deletion anyway

#### **Safe Approach Protocol (MANDATORY for Claude Code)**

1. **One method at a time** - Never bulk delete or refactor
2. **Verify compilation** after EACH individual change
3. **Update references** BEFORE removing methods
4. **Keep backups** during major changes (.backup extension)
5. **Test critical paths** after modifications

### 📏 Test Infrastructure Size & Complexity Limits

#### **Automatic Stop Conditions (Claude Code MUST Enforce)**

- **150 lines**: 🟡 Yellow flag - Suggest refactoring to user
- **200 lines**: 🔴 **HARD STOP** - Claude Code must refuse to continue and require simplification
- **>3 test base classes**: 🔴 **HARD STOP** - Architecture review required
- **>10 factory methods**: 🔴 **HARD STOP** - Excessive complexity, mandatory reduction

#### **Complexity Red Flags (Claude Code Must Flag These)**

- Methods with >5 parameters
- Complex inheritance hierarchies (>2 levels)
- Performance monitoring utilities in tests
- Caching mechanisms for test data
- Custom async/timing utilities for tests
- Behavioral verification in mock factories

### 📚 IllegalStateException Prevention Guide

**CRITICAL**: Review `docs/testing-IllegalStateException-analysis.md` for comprehensive understanding of coroutine test failures and systematic solutions. Quick reference: `docs/testing-troubleshooting-quick-reference.md`. Copy-paste templates: `docs/testing-templates.md`

### 🚫 Test Anti-Patterns (NEVER CREATE OR SUGGEST)

Claude Code is **EXPLICITLY FORBIDDEN** from creating or suggesting these patterns:

#### **Forbidden Test Infrastructure**

- ❌ **Performance monitoring** utilities in tests
- ❌ **Caching mechanisms** for test data (Redis, in-memory caches, etc.)
- ❌ **Complex mock factories** with behavioral verification
- ❌ **Multiple inheritance** hierarchies (>2 levels for tests)
- ❌ **Test frameworks** within test frameworks
- ❌ **Timing utilities** or complex async handling in tests
- ❌ **Scenario builders** (`createComplexScenario()` methods)

#### **Forbidden TestDataFactory Patterns**

- ❌ **Business logic** in factory methods
- ❌ **Random data generation** for property testing
- ❌ **Extensive parameterization** (>5 parameters per method)
- ❌ **Cross-aggregate** factory methods
- ❌ **State-dependent** factory methods

#### **Forbidden Base Class Patterns**

- ❌ **Multiple base classes** for same test type
- ❌ **Complex setup/teardown** logic
- ❌ **Business logic** in base classes
- ❌ **Deep inheritance** chains (>2 levels)

## 🧠 Memory Bank Workflow (Claude Code Integration)

**CRITICAL**: This project uses a Memory Bank workflow designed for AI memory resets. Claude Code must follow this workflow exactly:

### **Session Initialization (MANDATORY for Claude Code)**

Before any work, Claude Code must:

1. **Read ALL Memory Bank Files** (required for every session):
   - `memory-bank/projectbrief.md` - Core requirements and goals
   - `memory-bank/productContext.md` - Project purpose and user experience
   - `memory-bank/activeContext.md` - Current focus and next steps
   - `memory-bank/systemPatterns.md` - Architecture and design patterns
   - `memory-bank/techContext.md` - Technologies and constraints
   - `memory-bank/progress.md` - Current status and known issues
   - `memory-bank/plans/index.md` - Master index of all IPDs and tasks

2. **Verify Document Hierarchy Understanding**:
   - **PRDs** (read-only): `/docs/product-requirements-documents/`
   - **IPDs** (editable): `/memory-bank/plans/[ipd-folder]/[ipd-file].ipd.md`
   - **Tasks** (editable): `/memory-bank/plans/[ipd-folder]/tasks/TASKID-taskname.md`

3. **Apply Task Management Rules**:
   - Cannot create tasks without corresponding IPD
   - Cannot create IPD without referenced PRD
   - Must update `memory-bank/plans/index.md` for all changes
   - Must reference source documents in all IPDs and tasks

## 🏗️ LemonQwest App Context

### Core Features

- **Task System**: Children complete tasks, earn tokens, unlock achievements
- **Task Management**: Caregivers create, assign, monitor tasks across multiple children
- **Reward System**: Token-based local reward catalog and redemption
- **Achievement System**: Milestone-based badges and celebrations
- **Theme System**: User-based UI themes (Material Light default, with Material Dark and Mario Classic options available to all users)

### Technology Stack

- **Platform**: Android (API 24+, Target SDK 35)
- **Language**: Kotlin 2.1.0 with Java 21 (fallback Java 17)
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: Clean Architecture + DDD (Domain → Infrastructure → Presentation)
- **Database**: Room with SQLite, offline-first with encryption
- **DI**: Hilt with feature-based modules
- **Testing**: JUnit, Espresso, MockK (80%+ domain coverage required)
- **Static Analysis**: Detekt, KtLint (zero violations policy)

## 📁 Project Structure

```
```text
android-kotlin/app/src/main/java/com/lemonqwest/app/
├── domain/           # Business logic, aggregates, value objects
│   ├── user/         # User aggregate (User, UserRole, TokenBalance)
│   ├── task/         # Task aggregate (Task, TaskCategory, TaskStatus)
│   ├── token/        # Token economy (TokenTransaction, TokenBalance)
│   ├── reward/       # Reward system (Reward, RewardCategory)
│   └── achievement/  # Achievement system (Achievement, AchievementType)
├── infrastructure/   # Repository implementations, DAOs, data sources
│   ├── database/     # Room database, entities, DAOs
│   ├── preferences/  # DataStore implementations
│   └── repository/   # Repository implementations
├── presentation/     # Jetpack Compose UI, navigation, ViewModels
│   ├── screens/      # Screen composables
│   ├── components/   # Reusable UI components
│   ├── theme/        # Role-based theme system
│   ├── navigation/   # Navigation logic
│   └── viewmodels/   # ViewModels for state management
├── di/               # Hilt modules organized by feature
└── data/             # DataStore, theme management
```

## 🔧 LemonQwest Build Pipeline (Claude Code Zero-Tolerance Policy)

**MANDATORY SEQUENCE** (Claude Code must enforce - ALL commands must pass):

```bash
# Always navigate to android-kotlin directory first
cd android-kotlin

# 1. Format and analyze (run after ANY code change)
make format
make lint        # ZERO violations required - HARD STOP if any violations

# 2. Build and test (before marking any work complete)
make build         # Must build successfully - HARD STOP if fails
make test          # Must have ZERO test failures - HARD STOP if any fail
make install  # Must install successfully - HARD STOP if fails
```

**CLAUDE CODE ENFORCEMENT**:

- If **ANY** command fails, Claude Code must **STOP ALL WORK**
- Claude Code must **NOT** mark any task as complete with build failures
- Claude Code must request user intervention for persistent failures
- **Implementation is INCOMPLETE** if ANY command above fails

### 🔄 Safe Refactoring Workflow (Claude Code Protocol)

#### **Phase 1: Analysis (Required Before ANY Changes)**

1. **Map dependencies**: Use search tools to find ALL references
2. **Identify over-engineered components**: Flag files >150 lines
3. **Create impact assessment**: Count affected files, estimate complexity
4. **Backup current state**: Create .backup files for significant changes

#### **Phase 2: Incremental Changes (MANDATORY Process)**

1. **Start with leaf dependencies** (utilities used by few files)
2. **One utility method at a time** - Never bulk operations
3. **Verify compilation** after EACH individual change
4. **Update call sites** BEFORE proceeding to next method
5. **Test critical functionality** after each phase

#### **Phase 3: Verification (Required After ALL Changes)**

1. **Full test suite compilation**: `make build`
2. **Run representative tests**: Verify no functionality broken
3. **Import validation**: Check that no broken imports remain
4. **Business logic verification**: Ensure domain tests still pass

### 🚁 Emergency Recovery Protocol (Claude Code)

#### **If Build Breaks (Claude Code Response)**

1. **IMMEDIATE HALT**: Stop ALL refactoring/changes immediately
2. **Assess damage**: Use `make build --continue` to identify breaks
3. **Restore from backup**: Use .backup files if available
4. **Incremental fix**: Address one broken reference at a time
5. **Verify restoration**: Ensure compilation passes before continuing
6. **Alert user**: Report what broke and recovery actions taken

#### **Recovery Commands (Claude Code Must Use)**

```bash
# Check what's broken
make build --continue

# Find missing references
grep -r "UnresolvedReference" build/ || echo "No build directory found"

# Restore backup if needed (Claude Code should suggest this)
cp file.kt.backup file.kt

# Verify fix worked
make build
```

## 🎯 Architecture Principles

### Clean Architecture + DDD

- **Domain Layer**: Business logic, aggregates, value objects, domain events
- **Infrastructure Layer**: Repository implementations, database, external concerns
- **Presentation Layer**: UI, ViewModels, navigation, user interaction
- **Dependency Rule**: Dependencies point inward toward domain

### Key Patterns

- **Aggregates**: User, Task, Token, Reward, Achievement
- **Value Objects**: UserRole, TaskCategory, TokenBalance, RewardCategory
- **Domain Events**: TaskCompleted, TokensEarned, RewardRedeemed
- **Repository Pattern**: Domain interfaces, infrastructure implementations
- **SOLID Principles**: Single responsibility, dependency inversion
- **DRY Implementation**: Shared components, centralized validation

## 🎨 Theme System

### User-Based Theme Selection

- **All Users** → Can select any available theme (Material Light, Material Dark, Mario Classic)
  - Default: Material Light theme for all users
  - Theme Selection: Independent of user role
  - Terminology: Adapts based on selected theme
  - Visual: Theme-appropriate colors, icons, and components
  
- **Mario Classic Theme** → Game-inspired experience
  - Terminology: Quests, Coins, Power-ups, Adventures
  - Visual: Game-inspired colors, playful icons
  - Components: Theme-aware with semantic mapping
  
- **Material Light/Dark Themes** → Professional experience
  - Terminology: Tasks, Tokens, Rewards, Management
  - Visual: Material Design 3 components
  - Components: Standard Material components

### Theme Implementation Rules

- **All UI components** must support theme switching
- **Use semantic mapping** for shared components
- **Never use raw Material** components directly
- **Theme-aware terminology** adapts to selected theme
- **Accessibility compliance** for all themes (4.5:1 contrast ratio)

## 📋 LemonQwest Code Standards

### Kotlin Requirements (LemonQwest Specific)

- **Explicit imports only** (no wildcard imports)
- **Explicit nullability** declarations
- **camelCase** functions (including @Composable)
- **PascalCase** classes and data classes
- **SCREAMING_SNAKE_CASE** constants
- **No magic numbers** (use named constants)
- **Group 7+ parameters** into data classes
- **No business logic in composables**

### Architecture Requirements (LemonQwest Specific)

- **Business logic** only in domain layer
- **UI logic** only in presentation layer
- **Data access** only in infrastructure layer
- **Dependencies** point toward domain
- **Repository pattern** for all data access
- **Immutable data classes** for value objects
- **Domain events** for cross-aggregate communication

### Security Requirements

- **Input validation** for all user inputs
- **Secure storage** for sensitive data
- **COPPA compliance** for child safety
- **No PII collection** or external integrations
- **Defense-in-depth** security architecture

### Accessibility Requirements

- **TalkBack support** for all interactive elements
- **Semantic roles** for UI components
- **4.5:1 color contrast** ratio minimum
- **contentDescription** for all images
- **Proper heading hierarchy** for navigation

## 🚀 Development Workflow (Claude Code Integration)

### 1. Plan Mode

When asked to implement new features, Claude Code must:

1. **Check if PRD exists** in `docs/product-requirements-documents/`
2. **Check if IPD exists** in `memory-bank/plans/`
3. **Create IPD if needed** following template standards
4. **Reference source documents** in all planning
5. **Update Memory Bank** with all changes made

### 2. Implementation Mode

Claude Code must follow this exact sequence:

1. **Domain** → Define entities, value objects, business rules
2. **Infrastructure** → Repository implementations, DAOs
3. **Presentation** → Theme-aware Compose screens, ViewModels
4. **DI** → Wire dependencies with Hilt modules
5. **Testing** → Unit tests, integration tests (80%+ coverage)
6. **Validation** → Run complete build pipeline (MANDATORY)

### 3. Task Management (Claude Code Requirements)

- **Create tasks** only with existing IPD
- **Reference source documents** in all tasks
- **Update** `memory-bank/plans/index.md` for changes
- **Document progress** in individual task files
- **Follow task state transitions** (Pending → In Progress → Completed)

### 4. Documentation Updates (Claude Code Responsibilities)

- **Update Memory Bank** files for architectural changes
- **Never edit PRDs** (they are read-only)
- **Follow markdown standards** (emoji headers, navigation)
- **Cross-reference** all related documents
- **Maintain traceability** between PRDs, IPDs, and tasks

## 🔍 Search and Analysis Strategy

### File Pattern Recognition

- **Domain interfaces**: `domain/[feature]/` (business logic)
- **Repository implementations**: `infrastructure/repository/` (data access)
- **Database entities**: `infrastructure/database/entities/` (Room entities)
- **ViewModels**: `presentation/viewmodels/` (UI state management)
- **Theme components**: `presentation/theme/components/` (UI components)
- **DI modules**: `di/` (dependency injection)

### Search Strategy (Claude Code Protocol)

1. **For UI patterns** → Search `presentation/theme/` first
2. **For business logic** → Search `domain/` first
3. **For data access** → Search `infrastructure/` first
4. **For existing features** → Use comprehensive codebase search
5. **For documentation** → Check `docs/` and `memory-bank/`

### Analysis Workflow (Claude Code Requirements)

- **Use semantic search** for broad concept discovery
- **Use file search** for specific patterns
- **Check Memory Bank** for current context
- **Verify against PRDs** for requirements
- **Cross-reference IPDs** for implementation plans

## 🧪 LemonQwest Testing Strategy (Claude Code Safeguards)

### 🎯 Test Simplicity Principles (CRITICAL - Claude Code Must Enforce)

**MANDATORY**: Claude Code must prevent over-engineering pitfalls that lead to maintenance debt

#### **Lean Testing Manifesto (Claude Code Enforcement)**

- ✅ **Business value over infrastructure complexity**
- ✅ **Readable tests over clever utilities**  
- ✅ **Simple solutions over engineered frameworks**
- ✅ **Essential builders over comprehensive factories**

#### **Strict Limits (Claude Code MUST Enforce)**

- 📏 **Test utility files**: MAX 200 lines (HARD STOP at 200)
- 📏 **Test base classes**: MAX 2 total (HARD STOP at 3)
- 📏 **Factory methods**: Essential builders only
- 📏 **Mock utilities**: Basic setup, no caching/performance monitoring

#### **Anti-Patterns Claude Code Must NEVER Create**

- ❌ **Performance monitoring** in test utilities
- ❌ **Complex mock factories** with caching mechanisms  
- ❌ **Multiple test inheritance hierarchies** (>2 base classes)
- ❌ **Test infrastructure** more complex than production code
- ❌ **Behavioral testing utilities** that test implementation vs behavior
- ❌ **Removing public methods** without checking ALL references first

#### **Claude Code Safety Rules (MANDATORY)**

- 🛡️ **ALWAYS verify compilation** after utility changes
- 🛡️ **Search for method usage** before deletion (`grep -r "methodName"`)
- 🛡️ **Incremental removal** - never delete large files wholesale
- 🛡️ **Reference checking** - ensure no broken imports remain
- 🛡️ **Build verification** - tests must compile before marking complete

### ✅ Approved Patterns (Claude Code May Create)

#### **Minimal Test Infrastructure (Safe for Claude Code)**

- ✅ **Single ViewModelTestBase** for coroutine setup
- ✅ **Essential factory methods** (createUser, createTask)
- ✅ **Simple mock setups** with explicit behavior
- ✅ **Parameterized tests** for multiple scenarios
- ✅ **Direct business logic** verification

#### **Safe Utility Boundaries (Claude Code Guidelines)**

- ✅ **MockKAnnotations.init()** setup
- ✅ **Basic factory methods** with minimal parameters
- ✅ **Simple test data builders** without logic
- ✅ **Explicit mock configurations** per test
- ✅ **Clear test method names** indicating purpose

### 🚨 **CRITICAL MockK Policy - RELAXED MODE RESTRICTIONS**

#### **FIRM STATEMENT: `relaxed = true` IS FORBIDDEN**

**Claude Code MUST NEVER use `relaxed = true` in MockK configurations except for documented justified cases**

#### **❌ WHY `relaxed = true` IS FORBIDDEN:**

- **Hides underlying errors** and design flaws in test setup
- **Masks real MockK exceptions** that indicate incorrect mock configuration  
- **Reduces test reliability** by allowing undefined behavior
- **Creates false passing tests** that don't actually verify business logic
- **Makes debugging harder** when tests fail in CI/production
- **Violates zero-tolerance testing principles**

#### **✅ ONLY JUSTIFIED CASES (RARE):**

1. **Infrastructure Integration Tests** - External dependencies (Room, DataStore) with complex internal call chains NOT part of business logic verification
2. **Legacy Code Cleanup** - Temporary refactoring phases (MUST include TODO with deadline)
3. **Third-party Library Mocks** - Libraries with deep internal interactions we don't control (MUST be isolated to specific instances)

#### **✅ REQUIRED ALTERNATIVES (Claude Code MUST Use):**

```kotlin
// ✅ CORRECT: Explicit mock behavior
every { mockRepository.getUser(any()) } returns testUser
every { mockService.authenticate(any()) } returns AuthResult.Success(user)

// ❌ FORBIDDEN: Relaxed mocking
val mockRepository = mockk<UserRepository>(relaxed = true) // NEVER DO THIS
```

#### **✅ CLAUDE CODE ENFORCEMENT:**

- **ALWAYS use explicit `every { }` blocks** for mock behavior
- **ALWAYS use `mockk<Interface>()` without relaxed** for clean interfaces
- **ALWAYS use `spyk()` for partial mocking** when some real behavior needed
- **ALWAYS refactor production code** if it requires extensive mocking
- **ALWAYS document any relaxed usage** with specific justification and cleanup plan

### 🧪 **CRITICAL Thread-Safe Testing Patterns (IllegalStateException Prevention)**

#### **✅ MANDATORY ViewModelTestBase Usage (Category B Pattern)**

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
abstract class ViewModelTestBase {
    protected lateinit var testDispatcher: TestDispatcher
    protected lateinit var testScope: TestScope
    
    companion object {
        // CRITICAL: Synchronization for JUnit parallel execution
        private val mainDispatcherLock = Any()
    }

    @BeforeEach
    open fun setUpViewModel() {
        synchronized(mainDispatcherLock) {
            testDispatcher = UnconfinedTestDispatcher()
            testScope = TestScope(testDispatcher)
            Dispatchers.setMain(testDispatcher)
            MockKAnnotations.init(this, relaxUnitFun = false)
            // CRITICAL: Double stabilization for edge case timing
            testDispatcher.scheduler.advanceUntilIdle()
            testDispatcher.scheduler.advanceUntilIdle()
        }
    }

    @AfterEach
    open fun tearDownViewModel() {
        synchronized(mainDispatcherLock) {
            testScope.cancel()
            Dispatchers.resetMain()
            unmockkAll()
        }
    }
}
```

#### **✅ MANDATORY Enhanced ViewModel Test Setup**

```kotlin
@BeforeEach
override fun setUpViewModel() {
    super.setUpViewModel()
    
    // ENHANCEMENT: Additional stabilization for ViewModel timing
    testDispatcher.scheduler.advanceUntilIdle()
    
    // Mock setup...
    
    // ENHANCEMENT: Final stabilization after mock setup
    testDispatcher.scheduler.advanceUntilIdle()
}

private fun createViewModel(): MyViewModel {
    // ENHANCEMENT: Additional stabilization before ViewModel creation
    testDispatcher.scheduler.advanceUntilIdle()
    
    val dependencies = Dependencies(...)
    
    // ENHANCEMENT: Final stabilization before ViewModel instantiation
    testDispatcher.scheduler.advanceUntilIdle()
    
    return MyViewModel(dependencies, testDispatcher)
}
```

#### **✅ MANDATORY Category A Pattern (UseCase/Repository Tests)**

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class UseCaseTest {
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher()) // CRITICAL: Main dispatcher setup
        MockKAnnotations.init(this)
        // test setup...
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain() // CRITICAL: Dispatcher cleanup
        unmockkAll()
    }
}
```

#### **🚨 ZERO TOLERANCE ENFORCEMENT**

- **NEVER** mark any task complete with IllegalStateException failures
- **ALWAYS** verify concrete evidence of failure count reduction
- **MANDATORY** synchronization for all ViewModelTestBase usage
- **REQUIRED** double stabilization in ViewModel tests

### Testing Requirements (LemonQwest Specific)

- **80%+ coverage** for domain layer (mandatory)
- **Unit tests** for all business logic
- **Integration tests** for repository implementations
- **UI tests** for critical user flows
- **Accessibility tests** for all interactive elements

### Testing Patterns (LemonQwest Specific)

- **Given-When-Then** structure for all tests
- **MockK** for mocking dependencies
- **Happy path** and error scenario coverage
- **Test data factories** for consistent test data
- **Parameterized tests** for multiple scenarios

## 🚨 LemonQwest Error Handling

### Domain Layer (LemonQwest Specific)

- **Custom exceptions** for business rule violations
- **Result patterns** for operation outcomes
- **Validation** at aggregate boundaries
- **Domain events** for error notifications

### Infrastructure Layer (LemonQwest Specific)

- **Repository exceptions** for data access errors
- **Database transaction** handling
- **Network failure** resilience (offline-first)
- **Data consistency** validation

### Presentation Layer (LemonQwest Specific)

- **User-friendly error messages**
- **Loading states** for async operations
- **Error recovery** options
- **Accessibility** for error states

## 📚 LemonQwest Documentation Standards

### Markdown Requirements (LemonQwest Specific)

- **Kebab-case** filenames (no spaces or special characters)
- **Emoji** section headers for navigation
- **Required navigation** links (top/bottom)
- **Line length** limit of 400 characters
- **Proper hierarchy** (H2, H3 - avoid H1, H4+)
- **Front matter** with metadata fields

### Documentation Types (LemonQwest Specific)

- **Technical docs** in `docs/` directory
- **Planning docs** in `memory-bank/plans/`
- **Code documentation** with comprehensive comments
- **Architecture decisions** recorded and justified
- **API documentation** for public interfaces

## 🎯 LemonQwest Decision Matrix

### Feature Implementation Priority

1. **Task System** → Foundation for all other features
2. **Token Economy** → Depends on task completion
3. **Reward System** → Depends on token balance
4. **Achievement System** → Depends on task/token data

### When User Asks For (Claude Code Response Guide)

- **"tasks"** → Start with `domain/task/` then `infrastructure/task/`
- **"rewards"** → Verify tasks/tokens exist first
- **"achievements"** → Verify tasks exist first
- **"screens"** → Verify underlying domain logic exists first
- **"navigation"** → Check role-based navigation requirements
- **"theme"** → Verify all three available themes (Material Light, Material Dark, Mario Classic)

### Code Generation Rules (Claude Code Requirements)

- **Check existing patterns** before creating new ones
- **Follow theme compatibility** for all UI components
- **Implement security** validation for all inputs
- **Add accessibility** features for all interactive elements
- **Create comprehensive tests** for all new functionality

## 📊 Success Metrics (Claude Code Must Validate)

### Healthy Refactoring Indicators (Claude Code Should Report)

- ✅ **All builds pass** throughout process
- ✅ **No unresolved references** remain
- ✅ **Test count decreases** while maintaining coverage
- ✅ **Test complexity reduces** noticeably
- ✅ **New tests easier** to write post-refactoring

### Warning Signs (Claude Code Must Flag)

- ⚠️ **Compilation failures** at any step
- ⚠️ **Unresolved reference** errors
- ⚠️ **Tests taking longer** to write post-refactoring
- ⚠️ **Increased setup complexity** for new tests
- ⚠️ **Business logic tests** becoming harder to understand

## 📝 LemonQwest Commit Standards

### Commit Message Format (LemonQwest Specific)

```commit-message
feat: Add token earning system for task completion

- Implement TokenEarningUseCase with business logic
- Add TokenTransaction domain entity
- Create TokenRepository with Room implementation
- Add comprehensive unit tests with 85% coverage
- Update task completion flow to award tokens
```

### Special Requirements (LemonQwest Specific)

- **Do not add** "Generated by", "Co-authored By" or related annotations
- **Reference Memory Bank** updates in commit messages
- **Follow atomic commit practices** for single features
- **Update documentation** for architectural changes

## 🏁 LemonQwest Success Criteria (Claude Code Validation)

### Code Quality (LemonQwest Specific) - Claude Code Must Verify

- **Zero Detekt violations** (enforced automatically) - HARD STOP if violations exist
- **All tests passing** (80%+ domain coverage) - HARD STOP if tests fail
- **Successful build** and installation - HARD STOP if build fails
- **Theme compatibility** across all roles
- **Accessibility compliance** (TalkBack, contrast)

### Architecture Quality (LemonQwest Specific) - Claude Code Must Ensure

- **Clean layer separation** (domain, infrastructure, presentation)
- **Proper dependency direction** (toward domain)
- **Immutable domain models** and value objects
- **Comprehensive error handling**
- **Security best practices** throughout

### Documentation Quality (LemonQwest Specific) - Claude Code Must Maintain

- **Memory Bank maintenance** (current context)
- **Cross-referenced** documents
- **Markdown standards** compliance
- **Architectural decisions** documented
- **Traceability** between PRDs, IPDs, and tasks

---

## 🔒 CLAUDE CODE ZERO-TOLERANCE BUILD VERIFICATION

**MANDATORY ENFORCEMENT** - Claude Code must run ALL commands and achieve ZERO failures:

```bash
# ALL of these commands MUST pass with ZERO failures:
cd android-kotlin
make format  # Must format successfully
make lint        # Must have ZERO violations - HARD STOP if any
make build         # Must build successfully - HARD STOP if fails
make test          # Must have ZERO test failures - HARD STOP if any fail
make install  # Must install successfully - HARD STOP if fails
```

**CLAUDE CODE ENFORCEMENT RULES**:

- ❌ **CANNOT** mark work as complete with ANY command failures
- ❌ **CANNOT** proceed to next task with build failures
- ❌ **CANNOT** ignore compilation errors or test failures
- ❌ **CANNOT** suggest workarounds for build failures
- ✅ **MUST** stop work and fix ALL issues before proceeding
- ✅ **MUST** alert user to ALL build failures immediately
- ✅ **MUST** provide specific error details and suggested fixes

**Implementation is INCOMPLETE with ANY failures above.**

## 📖 Claude Code Operating Guidelines Summary

### **Session Start Protocol**

1. Read ALL Memory Bank files
2. Understand current project context
3. Identify active tasks and priorities
4. Apply all safeguards and limits

### **During Development**

1. Enforce zero-tolerance build policy
2. Prevent test over-engineering
3. Use incremental, safe refactoring
4. Maintain documentation standards

### **Before Marking Complete**

1. Verify ALL builds pass
2. Confirm NO broken references
3. Update Memory Bank appropriately
4. Cross-reference all documents

### **Emergency Protocols**

1. STOP immediately on build failures
2. Create backups before major changes
3. Use incremental recovery approaches
4. Alert user to ALL issues found
