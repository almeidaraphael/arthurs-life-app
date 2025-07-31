# LemonQwest App

> Native Android application for family task management, featuring a gamified token-based reward system, role-based access (Child, Caregiver, Admin), and a robust theme system. Built with Kotlin, Java, and Jetpack Compose.

## 📚 Documentation Hub

| Category | Documentation | Purpose |
|----------|---------------|---------|
| **Architecture** | [Architecture Guide](docs/architecture.md) | DDD, SOLID, system design |
| **Tech Stack** | [Technology Stack](docs/tech-stack.md) | Frameworks, tools, rationale |
| **Theme System** | [Theme System](docs/theme-system.md) | Role-based theming |
| **Development** | [Development Guide](docs/development.md) | Workflow, standards, QA |
| **Testing** | [Testing Guide](docs/testing.md) | Modern testing strategy, parallel execution |
| **Security** | [Security Guide](docs/security.md) | Security, child safety |
| **Contributing** | [Contributing Guide](docs/contributing.md) | How to contribute |
| **Diagrams** | [System Diagrams](docs/diagrams/README.md) | Architecture visuals |

## 🚀 Getting Started

See [Getting Started Guide](docs/getting-started.md) for full setup instructions.

**Quick steps:**

1. Install Java 21 (or 17)
2. Install Android Studio
3. Clone the repo and open `android-kotlin/`
4. Build and run the app

## 🔧 Build System

This project uses a comprehensive **Makefile** for all build operations. Use `make` commands from the project root:

```bash
# Quick development workflow
make setup          # One-time setup
make copilot-pipeline  # Complete pipeline: format → lint → build → test → install

# Individual commands
make format          # Format code
make lint            # Static analysis (ZERO violations required)
make build           # Build all variants
make test            # Run modernized tests (48 files, parallel execution)
make install         # Install debug APK

# Show all available commands
make help
```

**Documentation:**
- [Makefile Usage Guide](docs/makefile-usage.md) - Comprehensive usage examples
- [Makefile Implementation](docs/makefile-implementation.md) - Technical details

**⚠️ Important:** All documentation now uses `make` commands instead of `./gradlew`. The Makefile provides a modern, user-friendly interface to the build system.

## 🏗️ Architecture Overview

- Domain-Driven Design (DDD): Aggregates, value objects, domain events
- SOLID principles: Maintainable, extensible code
- Repository pattern: Clean separation of concerns
- MVVM: Modern Android architecture
- Role-based theming: Material & Mario Classic themes

See [Technology Stack](docs/tech-stack.md) for details.

See [Development Guide](docs/development.md)

MIT License - see LICENSE file for details.

---

## 📚 Documentation Navigation

| Role | Start Here |
|------|------------|
| **Designers** | [Theme System](docs/theme-system.md) → [System Diagrams](docs/diagrams/README.md) |

**Full technical documentation:** [Docs Hub](docs/README.md)
