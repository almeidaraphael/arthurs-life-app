---
title: Arthur's Life App
summary: Native Android family task management app with gamified token economy, role-based access, and robust theme system. See /docs for full technical documentation.
---

# Arthur's Life App

> Native Android application for family task management, featuring a gamified token-based reward system, role-based access (Child, Caregiver, Admin), and a robust theme system. Built with Kotlin, Java, and Jetpack Compose.

## 📚 Documentation Hub

| Category | Documentation | Purpose |
|----------|---------------|---------|
| **Architecture** | [Architecture Guide](docs/architecture.md) | DDD, SOLID, system design |
| **Tech Stack** | [Technology Stack](docs/tech-stack.md) | Frameworks, tools, rationale |
| **Theme System** | [Theme System](docs/theme-system.md) | Role-based theming |
| **Development** | [Development Guide](docs/development.md) | Workflow, standards, QA |
| **Testing** | [Testing Guide](docs/testing.md) | Testing strategy, coverage |
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

## 🏗️ Architecture Overview

- Domain-Driven Design (DDD): Aggregates, value objects, domain events
- SOLID principles: Maintainable, extensible code
- Repository pattern: Clean separation of concerns
- MVVM: Modern Android architecture
- Role-based theming: Material & Mario Classic themes

See [Architecture Guide](docs/architecture.md) and [Theme System](docs/theme-system.md).

## 🛠️ Technology Stack

- Kotlin 2.1.0, Java 21/17
- Jetpack Compose, Material Design 3
- Room, DataStore, Hilt, Coroutines, Flow
- JUnit 5, MockK, Espresso

See [Technology Stack](docs/tech-stack.md) for details.

## 🎯 Core Features

- **Task Management**: Create, assign, complete, and schedule tasks
- **Token Economy**: Earn, track, and spend tokens
- **Reward System**: Redeem tokens for digital/physical rewards
- **Family Management**: Multi-child support, role-based access
- **Theme System**: Role-based UI customization

## 🧪 Quality Assurance

- 80%+ test coverage (domain layer)
- Static analysis: Detekt, KtLint
- Accessibility: TalkBack, color contrast
- See [Testing Guide](docs/testing.md)

## 🔒 Security & Child Safety

- PIN-based role switching
- Data encryption, secure storage
- Input validation, privacy controls
- See [Security Guide](docs/security.md)

## 🛠️ Development Workflow

- Start with domain layer (entities, value objects)
- Implement use cases, repositories, UI components
- Follow DDD, SOLID, DRY
- See [Development Guide](docs/development.md)

## 🛠️ Contributing

See [Contributing Guide](docs/contributing.md) for workflow, standards, and PR process.

## 📄 License

MIT License - see LICENSE file for details.

---

## 📚 Documentation Navigation

| Role | Start Here |
|------|------------|
| **Developers** | [Getting Started](docs/getting-started.md) → [Architecture](docs/architecture.md) → [Development Guide](docs/development.md) |
| **Contributors** | [Contributing Guide](docs/contributing.md) → [Testing Guide](docs/testing.md) |
| **Designers** | [Theme System](docs/theme-system.md) → [System Diagrams](docs/diagrams/README.md) |

**Full technical documentation:** [Docs Hub](docs/README.md)