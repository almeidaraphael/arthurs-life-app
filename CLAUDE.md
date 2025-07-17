# Arthur's Life App - Claude Code Agent Instructions

## Project Context

**Arthur's Life** is a family task management Android app built with Kotlin and Jetpack Compose. It uses a gamified token-based reward system with role-based access for Children, Caregivers, and Admins.

### AI Decision Tree: What to implement first?
1. **Task Management** (core business logic)
2. **Token Economy** (depends on tasks)
3. **Reward System** (depends on tokens)
4. **Achievement System** (depends on tasks/tokens)

## Technology Stack

- **Platform**: Android (API 24+, Target SDK 35)
- **Language**: Kotlin 2.1.0 with Java 21
- **UI**: Jetpack Compose with Material Design 3
- **Architecture**: Domain-Driven Design with Clean Architecture
- **Database**: Room with SQLite
- **DI**: Hilt
- **Static Analysis**: Detekt

## Project Structure

```
android-kotlin/app/src/main/java/com/arthurslife/app/
├── domain/              # Business logic and entities
├── data/                # Data layer (Room, DataStore, repositories)
├── presentation/        # UI layer (screens, theme, navigation, viewmodels)
├── di/                  # Dependency injection modules
└── util/                # Shared utility functions
```

### Documentation Structure

```
docs/
├── README.md                          # Documentation hub
├── architecture.md                    # DDD & Clean Architecture guide
├── tech-stack.md                      # Technology choices and rationale
├── development.md                     # Workflow, standards, build commands
├── getting-started.md                 # Environment setup
├── testing.md                         # Testing strategies
├── security.md                        # Security practices
├── theme-system.md                    # Role-based theming
├── contributing.md                    # Contribution guidelines
├── diagrams/                          # PlantUML architecture diagrams
└── product-requirements-documents/    # Feature specifications
```

## AI Workflow Commands - ZERO TOLERANCE POLICY

```bash
# Navigate to Android project first
cd android-kotlin

# 1. After ANY code change, run:
./gradlew detektFormat && ./gradlew detekt

# 2. Before completing tasks:
./gradlew build
./gradlew test
./gradlew installDebug  # Verify app can be installed
```

**AI Pattern**: Every code generation task MUST follow this sequence:
1. Generate code → 2. Format & analyze → 3. Build & test → 4. Fix any failing tests

**ZERO TOLERANCE POLICY**: Implementation is NEVER complete with ANY failures:
- Detekt violations = INCOMPLETE implementation
- Build failures = INCOMPLETE implementation  
- Test failures = INCOMPLETE implementation
- Installation failures = INCOMPLETE implementation
- No exceptions, no excuses, no compromises
- Run `./gradlew test` to verify all tests pass before proceeding

## Architecture Principles

- **Domain-Driven Design**: Aggregates (User, Task, Achievement, Token), Value Objects, Domain Events
- **Clean Architecture**: Domain → Infrastructure → Presentation
- **SOLID Principles**: Single responsibility, dependency inversion, interface segregation
- **Repository Pattern**: Clean separation between domain and data layers

## Theme Context for AI

### Role-Based Theme Mapping
- **Child Role** → Mario Classic theme → Use game terminology (Quests, Coins, Power-ups)
- **Caregiver Role** → Material Light/Dark → Use standard terminology (Tasks, Tokens, Rewards)
- **Admin Role** → Material Light/Dark → Use admin terminology (Management, Settings, Reports)

### AI Theme Decision Rules
- **When creating Child screens** → Use Mario Classic components and terminology
- **When creating Caregiver screens** → Use Material Design components
- **When creating shared components** → Support both themes with semantic mapping


## Implementation Guidelines

### Feature Development Flow
1. **Domain** → Define entities, value objects, business rules
2. **Infrastructure** → Repository implementations
3. **Presentation** → Theme-aware Compose screens
4. **DI** → Wire dependencies
5. **Test** → Unit and integration tests

### Code Quality Standards
- **Kotlin 2.1.0** with explicit null safety
- **Detekt compliance** (format after every change)
- **80%+ test coverage** for domain layer
- **DRY principles** with shared components
- **Clear layer separation** (business logic in domain only)

### Critical Code Rules

**Detekt Compliance (MANDATORY)**
- **camelCase functions** (including @Composable)
- **No wildcard imports** (use specific imports)
- **No magic numbers** (use named constants)
- **Group 7+ parameters** into data classes
- **Run `./gradlew detektFormat`** after every change

**Naming Conventions**
- Classes: `PascalCase`
- Functions/Composables: `camelCase`
- Constants: `SCREAMING_SNAKE_CASE`

**Theme System**
- All UI components must support theme switching
- Mario Classic theme: game terminology (Quests, Coins)
- Material themes: standard terminology (Tasks, Tokens)

## AI Task Prioritization

### When asked to implement features, prioritize in this order:
1. **Task Management** - Core business logic foundation
2. **Token Economy** - Depends on task completion events
3. **Reward System** - Depends on token balance
4. **Achievement System** - Depends on task/token data

### AI Decision Matrix:
- **User asks for "tasks"** → Start with domain/task/ then infrastructure/task/
- **User asks for "rewards"** → Check if tasks/tokens exist first
- **User asks for "achievements"** → Check if tasks exist first
- **User asks for "screens"** → Check if underlying domain logic exists first

## AI Code Generation Standards

### Automatic Code Patterns
**Testing** (Generate automatically)
- Domain tests with MockK
- Given-When-Then naming
- Happy path + error scenarios

**Security** (Always include)
- Input validation for all user inputs
- Explicit nullability declarations
- Secure storage for sensitive data

**Performance** (Default patterns)proceed
- `remember` for expensive calculations
- `LazyColumn` for lists
- `derivedStateOf` for computed values

**Accessibility** (Required for all UI)
- `contentDescription` for images
- Semantic roles for interactive elements
- Proper color contrast

## Known Issues

- Task management, token economy, achievements not implemented
- Many screens are placeholders  
- Database schema incomplete
- Need proper error handling and validation
- Documentation consolidation in progress (planning/ → docs/)
- Feature specifications being updated in docs/product-requirements-documents/

## AI Implementation Protocol

### MANDATORY: Follow this exact sequence for ANY code generation:

**Step 1: Analysis**
- Use Task tool to search existing patterns
- Check theme system implementation for UI patterns
- Verify domain structure exists before adding infrastructure

**Step 2: Implementation Order (NEVER deviate)**
1. **Domain** → entities, value objects, use cases
2. **Data** → repositories, DAOs, Room entities
3. **Presentation** → screens, viewmodels
4. **DI** → wire everything together

**Step 3: Code Generation Rules (CRITICAL)**
- **camelCase @Composable functions** - `taskScreen()` not `TaskScreen()`
- **No wildcard imports** - import specific classes only
- **No magic numbers** - use named constants
- **No nullable platform types** - explicit nullability
- **No business logic in composables** - domain logic only in domain layer
- **Theme compatibility** - support Mario Classic and Material themes

**Step 4: Validation (MANDATORY)**
```bash
# Run after EVERY code change
./gradlew detektFormat && ./gradlew detekt
```

**Step 5: Build & Test Verification (CRITICAL)**
```bash
# ALWAYS ensure these pass before marking implementation complete:
./gradlew build
./gradlew test
```

**ABSOLUTE REQUIREMENTS - ZERO TOLERANCE**:
- ❌ **NEVER** mark tasks as completed with ANY detekt violations
- ❌ **NEVER** leave implementations with ANY build failures
- ❌ **NEVER** finish without running FULL build verification
- ❌ **NEVER** leave ANY detekt violations - they are ALL critical
- ❌ **NEVER** rationalize, excuse, or defer detekt violations
- ❌ **NEVER** consider implementation "complete" if ANY gradle command fails
- ❌ **NEVER** submit code that cannot build and install successfully
- ❌ **NEVER** accept "it's just a minor violation" - ALL violations are blocking
- ✅ **ALWAYS** fix ALL detekt violations immediately - no exceptions
- ✅ **ALWAYS** ensure app builds, tests pass, and installs successfully
- ✅ **ALWAYS** verify complete build pipeline before marking complete
- ✅ **ALWAYS** treat code quality as non-negotiable requirement

**CRITICAL BUILD REQUIREMENT - ZERO FAILURES ACCEPTED**: 
ALL of these commands MUST pass with zero failures or implementation is INCOMPLETE:
```bash
./gradlew detektFormat  # Must format successfully
./gradlew detekt        # Must have ZERO violations
./gradlew build         # Must build successfully
./gradlew test          # Must have ZERO test failures  
./gradlew installDebug  # Must install successfully
```

**ABSOLUTE ZERO TOLERANCE**: Every detekt violation BLOCKS completion, regardless of:
- How "trivial" it appears
- Whether it "impacts functionality"
- Personal preference about the rule
- Time pressure or deadlines
- Implementation complexity
- Any other justification

Build success is the ONLY acceptable outcome - failures mean the work is not done.

### AI Error Recovery
If Detekt fails:
1. **WildcardImport** → Replace with specific imports
2. **MagicNumber** → Create named constants
3. **LongParameterList** → Group into data classes
4. **FunctionNaming** → Ensure camelCase for @Composable

## AI Quick Reference

### File Pattern Recognition
- **Domain interfaces**: `domain/[feature]/` (business logic)
- **Repository implementations**: `data/repository/` (data access)
- **Database entities**: `data/local/entity/` (Room entities)
- **ViewModels**: `presentation/viewmodels/` (UI state)
- **Theme-aware components**: `presentation/theme/components/` (UI)
- **DI modules**: `di/` (dependency injection)

### AI Search Strategy
1. **For UI patterns** → Search `presentation/theme/` first
2. **For business logic** → Search `domain/` first
3. **For data access** → Search `data/repository/` and `data/local/` first
4. **For existing features** → Use Task tool for comprehensive search
5. **For documentation** → Check `docs/` for architectural guidance

### AI Decision Points
- **User asks for new feature** → Check if domain exists, then data layer, then presentation
- **User asks for UI changes** → Check theme compatibility first
- **User asks for database changes** → Check domain model alignment first
- **User asks for documentation** → Check `docs/` for existing patterns and templates
- **Detekt errors** → Apply standard fixes (see AI Error Recovery above)

### AI Workflow Optimization
- **Use TodoWrite tool** for multi-step tasks
- **Use Task tool** for comprehensive codebase searches
- **Use MultiEdit** for multiple related changes
- **Run commands in parallel** when possible (multiple Bash calls)

## Memory Bank
- Follow /home/pheiow/.config/Code/User/prompts/memory-bank.instructions.md instructions.  