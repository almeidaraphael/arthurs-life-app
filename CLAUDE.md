# Arthur's Life App - Claude Code Instructions

## 📋 Project Overview
**Arthur's Life** is a family task management Android app built with Clean Architecture and Domain-Driven Design. It provides a gamified token-based reward system with role-based access for Children, Caregivers, and Admins.

**NOTE**: This file works in conjunction with `CLAUDE.global.md` which contains universal development standards and practices. Read both files for complete context.

## 🧠 Memory Bank Workflow

**CRITICAL**: This project uses a Memory Bank workflow designed for AI memory resets. Before any work:

1. **Read ALL Memory Bank Files** (required for every session):
   - `memory-bank/projectbrief.md` - Core requirements and goals
   - `memory-bank/productContext.md` - Project purpose and user experience
   - `memory-bank/activeContext.md` - Current focus and next steps
   - `memory-bank/systemPatterns.md` - Architecture and design patterns
   - `memory-bank/techContext.md` - Technologies and constraints
   - `memory-bank/progress.md` - Current status and known issues
   - `memory-bank/plans/index.md` - Master index of all IPDs and tasks

2. **Follow Document Hierarchy**:
   - **PRDs** (read-only): `/docs/product-requirements-documents/`
   - **IPDs** (editable): `/memory-bank/plans/[ipd-folder]/[ipd-file].ipd.md`
   - **Tasks** (editable): `/memory-bank/plans/[ipd-folder]/tasks/TASKID-taskname.md`

3. **Task Management Rules**:
   - Cannot create tasks without corresponding IPD
   - Cannot create IPD without referenced PRD
   - Must update `memory-bank/plans/index.md` for all changes
   - Must reference source documents in all IPDs and tasks

## 🏗️ Arthur's Life App Context

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
android-kotlin/app/src/main/java/com/arthurslife/app/
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

## 🔧 Arthur's Life Build Pipeline

**MANDATORY SEQUENCE** (all must pass, zero tolerance policy):

```bash
# Always navigate to android-kotlin directory first
cd android-kotlin

# 1. Format and analyze (run after ANY code change)
./gradlew detektFormat
./gradlew detekt        # ZERO violations required

# 2. Build and test (before marking any work complete)
./gradlew build         # Must build successfully
./gradlew test          # Must have ZERO test failures
./gradlew installDebug  # Must install successfully
```

**NOTE**: See `CLAUDE.global.md` for the complete zero tolerance policy and universal build verification principles.

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

## 📋 Arthur's Life Code Standards

### Kotlin Requirements (Arthur's Life Specific)
- **Explicit imports only** (no wildcard imports)
- **Explicit nullability** declarations
- **camelCase** functions (including @Composable)
- **PascalCase** classes and data classes
- **SCREAMING_SNAKE_CASE** constants
- **No magic numbers** (use named constants)
- **Group 7+ parameters** into data classes
- **No business logic in composables**

### Architecture Requirements (Arthur's Life Specific)
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

## 🚀 Development Workflow

### 1. Plan Mode
When asked to implement new features:
1. **Check if PRD exists** in `docs/product-requirements-documents/`
2. **Check if IPD exists** in `memory-bank/plans/`
3. **Create IPD if needed** following template standards
4. **Reference source documents** in all planning

### 2. Implementation Mode
Follow this exact sequence:
1. **Domain** → Define entities, value objects, business rules
2. **Infrastructure** → Repository implementations, DAOs
3. **Presentation** → Theme-aware Compose screens, ViewModels
4. **DI** → Wire dependencies with Hilt modules
5. **Testing** → Unit tests, integration tests (80%+ coverage)
6. **Validation** → Run complete build pipeline

### 3. Task Management
- **Create tasks** only with existing IPD
- **Reference source documents** in all tasks
- **Update** `memory-bank/plans/index.md` for changes
- **Document progress** in individual task files
- **Follow task state transitions** (Pending → In Progress → Completed)

### 4. Documentation Updates
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

### Search Strategy
1. **For UI patterns** → Search `presentation/theme/` first
2. **For business logic** → Search `domain/` first
3. **For data access** → Search `infrastructure/` first
4. **For existing features** → Use comprehensive codebase search
5. **For documentation** → Check `docs/` and `memory-bank/`

### Analysis Workflow
- **Use semantic search** for broad concept discovery
- **Use file search** for specific patterns
- **Check Memory Bank** for current context
- **Verify against PRDs** for requirements
- **Cross-reference IPDs** for implementation plans

## 🧪 Arthur's Life Testing Strategy

### Testing Requirements (Arthur's Life Specific)
- **80%+ coverage** for domain layer (mandatory)
- **Unit tests** for all business logic
- **Integration tests** for repository implementations
- **UI tests** for critical user flows
- **Accessibility tests** for all interactive elements

### Testing Patterns (Arthur's Life Specific)
- **Given-When-Then** structure for all tests
- **MockK** for mocking dependencies
- **Happy path** and error scenario coverage
- **Test data factories** for consistent test data
- **Parameterized tests** for multiple scenarios

## 🚨 Arthur's Life Error Handling

### Domain Layer (Arthur's Life Specific)
- **Custom exceptions** for business rule violations
- **Result patterns** for operation outcomes
- **Validation** at aggregate boundaries
- **Domain events** for error notifications

### Infrastructure Layer (Arthur's Life Specific)
- **Repository exceptions** for data access errors
- **Database transaction** handling
- **Network failure** resilience (offline-first)
- **Data consistency** validation

### Presentation Layer (Arthur's Life Specific)
- **User-friendly error messages**
- **Loading states** for async operations
- **Error recovery** options
- **Accessibility** for error states

## 📚 Arthur's Life Documentation Standards

### Markdown Requirements (Arthur's Life Specific)
- **Kebab-case** filenames (no spaces or special characters)
- **Emoji** section headers for navigation
- **Required navigation** links (top/bottom)
- **Line length** limit of 400 characters
- **Proper hierarchy** (H2, H3 - avoid H1, H4+)
- **Front matter** with metadata fields

### Documentation Types (Arthur's Life Specific)
- **Technical docs** in `docs/` directory
- **Planning docs** in `memory-bank/plans/`
- **Code documentation** with comprehensive comments
- **Architecture decisions** recorded and justified
- **API documentation** for public interfaces

## 🎯 Arthur's Life Decision Matrix

### Feature Implementation Priority
1. **Task System** → Foundation for all other features
2. **Token Economy** → Depends on task completion
3. **Reward System** → Depends on token balance
4. **Achievement System** → Depends on task/token data

### When User Asks For:
- **"tasks"** → Start with `domain/task/` then `infrastructure/task/`
- **"rewards"** → Verify tasks/tokens exist first
- **"achievements"** → Verify tasks exist first
- **"screens"** → Verify underlying domain logic exists first
- **"navigation"** → Check role-based navigation requirements
- **"theme"** → Verify all three available themes (Material Light, Material Dark, Mario Classic)

### Code Generation Rules
- **Check existing patterns** before creating new ones
- **Follow theme compatibility** for all UI components
- **Implement security** validation for all inputs
- **Add accessibility** features for all interactive elements
- **Create comprehensive tests** for all new functionality

## 📝 Arthur's Life Commit Standards

### Commit Message Format (Arthur's Life Specific)
```
feat: Add token earning system for task completion

- Implement TokenEarningUseCase with business logic
- Add TokenTransaction domain entity
- Create TokenRepository with Room implementation
- Add comprehensive unit tests with 85% coverage
- Update task completion flow to award tokens
```

### Special Requirements (Arthur's Life Specific)
- **Do not add** "Generated by", "Co-authored By" or related annotations
- **Reference Memory Bank** updates in commit messages
- **Follow atomic commit practices** for single features
- **Update documentation** for architectural changes

## 🏁 Arthur's Life Success Criteria

### Code Quality (Arthur's Life Specific)
- **Zero Detekt violations** (enforced automatically)
- **All tests passing** (80%+ domain coverage)
- **Successful build** and installation
- **Theme compatibility** across all roles
- **Accessibility compliance** (TalkBack, contrast)

### Architecture Quality (Arthur's Life Specific)
- **Clean layer separation** (domain, infrastructure, presentation)
- **Proper dependency direction** (toward domain)
- **Immutable domain models** and value objects
- **Comprehensive error handling**
- **Security best practices** throughout

### Documentation Quality (Arthur's Life Specific)
- **Memory Bank maintenance** (current context)
- **Cross-referenced** documents
- **Markdown standards** compliance
- **Architectural decisions** documented
- **Traceability** between PRDs, IPDs, and tasks

---

**ARTHUR'S LIFE BUILD VERIFICATION**:
```bash
# ALL of these commands MUST pass with zero failures:
cd android-kotlin
./gradlew detektFormat  # Must format successfully
./gradlew detekt        # Must have ZERO violations
./gradlew build         # Must build successfully
./gradlew test          # Must have ZERO test failures  
./gradlew installDebug  # Must install successfully
```

**Implementation is INCOMPLETE with ANY failures above.**

## 📖 How These Files Work Together

This file (`CLAUDE.md`) contains **Arthur's Life App specific** instructions and must be used in conjunction with `CLAUDE.global.md` which contains **universal development standards**.

### Reading Order
1. **First** → Read `CLAUDE.global.md` for universal standards
2. **Second** → Read this file for Arthur's Life specifics
3. **During work** → Follow both sets of instructions

### Division of Responsibilities
- **CLAUDE.global.md** → Universal standards, build verification, general best practices
- **CLAUDE.md** → Arthur's Life context, Memory Bank workflow, project-specific requirements

### When in Doubt
- **Code standards** → Check global file first, then project-specific additions
- **Build process** → Use Arthur's Life specific commands, but follow global zero-tolerance policy
- **Architecture** → Arthur's Life uses Clean Architecture + DDD (specified here)
- **Quality standards** → Global file defines the framework, this file adds project requirements

Both files are **mandatory** and **complementary** - neither is complete without the other.
