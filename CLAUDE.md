# Arthur's Life App - Claude Agent Context

## Project Overview

**Arthur's Life** is a family task management Android app built with Kotlin and Jetpack Compose. It uses a gamified token-based reward system to help families organize daily tasks with role-based access for Children, Caregivers, and Admins.

### Current Implementation Status (as of git status)
- ✅ **Theme System**: Role-based theming with Mario Classic and Material Dark/Light themes
- ✅ **Authentication**: PIN-based role switching between Child/Caregiver/Admin modes
- ✅ **Navigation**: Basic navigation structure with role-specific screens
- ✅ **Architecture Foundation**: Domain-driven design with clean architecture layers
- ❌ **Core Features**: Task management, token economy, rewards, achievements (NOT YET IMPLEMENTED)

## Technology Stack

- **Platform**: Native Android (API 24+, Target SDK 35)
- **Language**: Kotlin 2.1.0 with Java 21
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: Domain-Driven Design (DDD) with Clean Architecture
- **Database**: Room with SQLite (offline-first)
- **DI**: Hilt
- **Static Analysis**: Detekt (replaces KtLint)
- **Testing**: JUnit, Espresso

## Project Structure

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

## Key Commands

### Development Workflow
```bash
# Navigate to Android project
cd android-kotlin

# Build project
./gradlew build

# Format code with Detekt
./gradlew detektFormat

# Run static analysis
./gradlew detekt

# Run tests
./gradlew test

# Install on device
./gradlew installDebug
```

## Architecture Principles

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

## User Roles & Implementation

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

## Current File Status (from git status)

### Modified Files
- `AuthModule.kt` - Authentication dependency injection
- `MainAppNavigation.kt` - Navigation structure
- `MaterialThemeIcons.kt` & `SemanticIconType.kt` - Theme icon system
- `MarioThemeIcons.kt` - Mario theme specific icons

### New Untracked Files (Need Implementation)
- Domain layer: `domain/achievement/`, `domain/common/`, `domain/task/`
- Infrastructure: `infrastructure/achievement/`, `infrastructure/task/`
- Database: `dao/TaskDao.kt`, `entities/TaskEntity.kt`
- Presentation: Multiple new screens and components
- ViewModels: `AchievementViewModel.kt`, `TaskManagementViewModel.kt`

## Implementation Guidelines

### When Adding New Features
1. **Start with Domain**: Define entities, value objects, and business rules in `domain/`
2. **Create Use Cases**: Implement business logic as use cases
3. **Add Infrastructure**: Repository implementations in `infrastructure/`
4. **Build UI**: Theme-aware Compose screens in `presentation/screens/`
5. **Add DI**: Wire dependencies in `di/` modules
6. **Test**: Comprehensive unit and integration tests

### Code Quality Standards
- Use Detekt for formatting: `./gradlew detektFormat`
- Pass static analysis: `./gradlew detekt`
- Follow DRY principles with shared components
- Maintain clear separation between layers
- All business logic in domain layer

### Theme System
- All UI components must support theme switching
- Use semantic icon mapping for theme-specific icons
- Mario Classic theme uses game terminology (Quests, Coins, etc.)
- Material themes use standard terminology (Tasks, Tokens, etc.)

## Development Priorities

### Immediate Next Steps (MVP)
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

### Testing Strategy
- **Domain Logic**: Comprehensive unit tests (80%+ coverage)
- **Repository Layer**: Integration tests with Room
- **UI Components**: Compose testing for screens
- **Theme System**: Test theme switching and component rendering

## Known Issues & Technical Debt

### Current Limitations
- Task management not implemented (domain exists but no business logic)
- Token economy system missing entirely
- Achievement system has structure but no implementation
- Many screens are placeholder implementations
- Database schema incomplete

### Architecture Improvements Needed
- Complete repository implementations
- Add proper error handling throughout
- Implement domain events system
- Add comprehensive validation
- Complete theme system integration

## Best Practices for Claude

### File Editing
- Always use Edit/MultiEdit tools for code changes
- Include sufficient context (3-5 lines) for unique string matching
- Run `./gradlew detektFormat` after code changes
- Verify changes applied correctly

### Implementation Approach
1. Check existing patterns in implemented features (theme system, navigation)
2. Follow DDD structure: domain → infrastructure → presentation
3. Maintain theme compatibility across all new UI
4. Add proper error handling and validation
5. Write tests for new business logic

### Code Style
- Follow Detekt rules (configured in `config/detekt/detekt.yml`)
- Use meaningful names reflecting domain concepts
- Implement proper error handling with domain exceptions
- Maintain immutability where possible
- Add comprehensive documentation

## Quick Reference

### Key Files to Understand
- `planning/requirements.md` - Current implementation status and roadmap
- `docs/architecture.md` - Detailed architecture documentation
- `android-kotlin/app/build.gradle.kts` - Dependencies and build configuration
- `presentation/theme/` - Theme system implementation patterns
- `presentation/navigation/MainAppNavigation.kt` - Navigation structure

### Common Patterns
- Domain interfaces in `domain/[feature]/`
- Repository implementations in `infrastructure/[feature]/`
- ViewModels in `presentation/viewmodels/`
- Theme-aware components in `presentation/theme/components/`
- DI modules in `di/`

### When to Ask for Clarification
- Business logic unclear from domain models
- UI patterns don't match existing theme system
- Database schema conflicts with domain model
- Testing approach for complex features