
# GitHub Copilot Instructions – Arthur's Life App

## Purpose
This file defines the essential, up-to-date engineering and documentation standards for Arthur's Life App. All code and documentation must strictly follow these rules.

---

## 1. Architecture & Code Standards

- **Architecture**: Clean Architecture + DDD (Domain → Infrastructure → Presentation). Use strict dependency inversion.
- **Domain**: Aggregates (User, Task, Token, Reward, Achievement), Value Objects, Domain Events, Repository Pattern.
- **UI**: Jetpack Compose, theme-aware components only (never raw Material), role-based theming (Mario Classic for Child, Material for Caregiver/Admin).
- **Navigation**: Unified, role-based navigation via `MainAppNavigation.kt`.
- **Dependency Injection**: Hilt, modules organized by feature.
- **Testing**: JUnit, Espresso, MockK. 80%+ coverage required for domain.
- **Static Analysis**: Detekt (ZERO violations allowed), KtLint.
- **Accessibility**: All UI must support TalkBack, semantic roles, 4.5:1 color contrast.
- **Child Safety**: Input validation, secure storage, COPPA compliance.

---

## 2. File & Project Structure

- **Kotlin File Order**: Package → Explicit Imports → Code.
- **No wildcard imports**. All imports must be explicit.
- **No business logic in composables**. Use string resources, named constants, and immutable data classes.
- **Directory Structure**:
  ```
  com.arthurslife.app/
    domain/           # Business logic, aggregates, value objects
    infrastructure/   # Repository implementations, DAOs, data sources
    presentation/     # Jetpack Compose UI, navigation, ViewModels
    di/               # Hilt modules
    data/             # DataStore, theme management
  ```

---

## 3. Build & Quality Pipeline

**MANDATORY SEQUENCE** (all must pass, in order):
```zsh
# Always check/log current directory before running Gradle commands
if [ "$(basename $(pwd))" != "android-kotlin" ]; then 
    echo "Navigating to android-kotlin directory..."
    cd android-kotlin
fi

./gradlew detektFormat
./gradlew detekt        # ZERO violations required
./gradlew build
./gradlew test
./gradlew installDebug
```
- **Implementation is INCOMPLETE if any command fails.**

---

## 4. Documentation Standards

- **All docs** must follow `/docs/DOCUMENTATION_GUIDELINES.md` and `/docs/TEMPLATE.md`.
- **Required sections**: Document Overview, Quick Reference, Main Content, Integration Points, Additional Resources, Contributing.
- **Navigation**: Use emoji-prefixed headings and top/bottom navigation links as in the template.
- **File naming**: kebab-case, descriptive, no spaces or special characters.
- **Update docs** for any API, workflow, or architectural change.

---

## 5. Contribution Workflow

- **Start with Domain**: Define entities, value objects, business rules.
- **Implement Use Cases**: In domain layer.
- **Add Infrastructure**: Repository implementations.
- **Build UI**: Theme-aware Compose screens.
- **Wire DI**: Hilt modules by feature.
- **Test**: Comprehensive unit/integration tests.
- **Review**: All code and docs must pass technical and editorial review.

---

## 6. Commit & PR Guidelines

- Use clear, imperative commit messages.
- Reference issues in PRs.
- Document major decisions in PR descriptions.
- Update documentation for all significant changes.

---

## 7. Key References

- `/docs/architecture.md` – Architecture patterns
- `/docs/tech-stack.md` – Technology stack
- `/docs/theme-system.md` – Theme system
- `/docs/testing.md` – Testing strategy
- `/docs/security.md` – Security practices
- `/docs/DOCUMENTATION_GUIDELINES.md` – Documentation standards
- `/docs/TEMPLATE.md` – Documentation template

---

**ZERO TOLERANCE POLICY:**  
All code and documentation must comply with these standards. Any violation blocks completion.

---
