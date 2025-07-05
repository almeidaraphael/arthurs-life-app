# Arthur's Life - Family Task Management App

A native Android application designed to help families organize daily tasks through a gamified token-based reward system. Built with role-based access for Children, Caregivers, and Admins with comprehensive task management and progress tracking.

## 📋 Documentation Hub

### 📚 Technical Documentation
Complete developer and technical documentation.

| Category | Documentation | Purpose |
|----------|---------------|---------|
| **🏗️ Architecture** | **[Architecture Overview](docs/architecture.md)** | DDD, SOLID principles, and system design |
| **🚀 Development** | **[Getting Started](docs/getting-started.md)** | Setup instructions and first steps |
| **🧪 Testing** | **[Testing Guide](docs/testing.md)** | Testing strategies and implementation |
| **🔒 Security** | **[Security & Privacy](docs/security.md)** | Security implementation and child safety |
| **💻 Workflow** | **[Development Guide](docs/development.md)** | Development workflow and tools |
| **🤝 Contributing** | **[Contributing Guide](docs/contributing.md)** | How to contribute to the project |
| **📚 Full Index** | **[📚 Documentation Hub](docs/README.md)** | Complete technical documentation index |

### 📋 Planning Documentation
Product planning, requirements, and feature specifications.

| Category | Documentation | Purpose |
|----------|---------------|---------|
| **📋 Requirements** | **[Requirements](planning/requirements.md)** | Core requirements and specifications |
| **🎯 MVP** | **[MVP Scope](planning/mvp.md)** | Minimum viable product features |
| **📱 Architecture** | **[App Structure](planning/app-structure.md)** | System architecture and user roles |
| **🎮 Features** | **[Feature Specifications](planning/features/)** | Detailed feature documentation |
| **📊 Analytics** | **[Use Cases](planning/use-cases.md)** | User flows and interaction patterns |
| **📋 Full Index** | **[📋 Planning Hub](planning/README.md)** | Complete planning documentation index |

### 🎯 Quick Access
| Need | Documentation | Purpose |
|------|---------------|---------|
| **Start Development** | **[Getting Started](docs/getting-started.md)** | Setup and first steps |
| **Understand Architecture** | **[Architecture](docs/architecture.md)** | System design principles |
| **Plan Features** | **[Planning Hub](planning/README.md)** | Product planning and requirements |
| **Write Tests** | **[Testing Guide](docs/testing.md)** | Quality assurance practices |
| **Contribute** | **[Contributing Guide](docs/contributing.md)** | Collaboration guidelines |

## 🎯 Core Features

### Task Management System
- **Task Creation**: Caregivers create and assign age-appropriate tasks
- **Task Categories**: Chores, homework, personal care, family activities
- **Difficulty Levels**: Easy, medium, hard with appropriate token rewards
- **Scheduling**: One-time, recurring, and flexible task schedules

### Token Economy
- **Earning Tokens**: Complete tasks to earn digital tokens
- **Token Values**: Task difficulty determines token rewards
- **Spending System**: Redeem tokens for rewards and privileges
- **Financial Literacy**: Learn saving and spending concepts

### Reward System
- **Digital Rewards**: Screen time, game privileges, special activities
- **Physical Rewards**: Toys, treats, family outings
- **Achievement Badges**: Progress milestones and accomplishment recognition
- **Custom Rewards**: Family-specific rewards and traditions

### Family Management
- **Multiple Children**: Support for families with multiple children
- **Role-Based Access**: Child, Caregiver, and Admin permission levels
- **Progress Tracking**: Individual and family progress analytics
- **Communication**: Task comments and family coordination

## 🛠️ Technology Stack

- **Platform**: Native Android (API 24+)
- **Language**: Kotlin 2.1.0 with Java 21/17 support
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: Domain-Driven Design (DDD) with MVVM
- **Database**: Room with SQLite for offline-first experience
- **Testing**: JUnit 5, MockK, Espresso for comprehensive coverage

## 🚀 Quick Start

### Prerequisites
- **Java 21** (preferred) or **Java 17** (fallback)
- **Android Studio** (latest version)
- **Android SDK** with API level 24+

### Setup Steps
1. Clone the repository
2. Open `android-kotlin/` in Android Studio
3. Configure Project SDK to Java 21 (or Java 17)
4. Sync and build the project
5. Run on device or emulator

**For detailed setup:** [📚 Getting Started Guide](docs/getting-started.md)

## 🏗️ Architecture Principles

### Domain-Driven Design (DDD)
- **Aggregate Roots**: User, Task, Token, Reward entities
- **Value Objects**: TaskCategory, TaskDifficulty, UserRole
- **Domain Events**: TaskCompleted, TokensEarned, RewardRedeemed
- **Repository Pattern**: Clean separation of data concerns

### SOLID Principles
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Extensible design for new features
- **Liskov Substitution**: Proper inheritance hierarchies
- **Interface Segregation**: Focused, cohesive interfaces
- **Dependency Inversion**: Depend on abstractions, not concretions

### DRY (Don't Repeat Yourself)
- **Shared Components**: Reusable UI components and utilities
- **Common Logic**: Centralized business rules and validations
- **Configuration**: Single source of truth for app settings

## 📱 Project Structure

```
android-kotlin/app/src/main/java/com/arthurslife/app/
├── domain/              # Business logic and entities
│   ├── user/           # User management and roles
│   ├── task/           # Task creation and completion
│   ├── token/          # Token economy system
│   └── reward/         # Reward catalog and redemption
├── data/               # Data layer implementation
│   ├── local/         # Room database and DataStore
│   ├── repository/    # Repository implementations
│   └── mapper/        # Domain/data model mapping
├── presentation/       # UI layer with Compose
│   ├── screens/       # Screen composables
│   ├── components/    # Reusable UI components
│   ├── theme/         # Material Design theme
│   └── navigation/    # Navigation configuration
├── di/                # Dependency injection modules
└── util/              # Utility classes and extensions
```

## 🧪 Quality Assurance

### Testing Strategy
- **Unit Tests**: Domain logic and business rules (80%+ coverage)
- **Integration Tests**: Repository and database operations
- **UI Tests**: Screen interactions and user flows
- **Accessibility Tests**: TalkBack and accessibility services

### Code Quality
- **Static Analysis**: Detekt for code quality enforcement
- **Code Formatting**: KtLint for consistent code style
- **Build Verification**: Pre-commit hooks and CI checks
- **Performance Testing**: Memory and CPU usage optimization

## 🔒 Security & Privacy

### Child Safety Features
- **Secure Authentication**: PIN-based role switching
- **Data Encryption**: Sensitive information protection
- **Privacy Controls**: Minimal data collection
- **Parental Oversight**: Caregiver monitoring and controls

### Technical Security
- **Input Validation**: All user inputs sanitized
- **Secure Storage**: Android Keystore integration
- **Network Security**: Certificate pinning and secure communication
- **Code Obfuscation**: R8 proguard for release builds

## 📈 Success Metrics

### User Engagement
- **Task Completion Rate**: Percentage of assigned tasks completed
- **Daily Active Usage**: Consistent app usage patterns
- **Token Economy Health**: Balanced earning and spending patterns
- **Family Participation**: Multi-user engagement levels

### Technical Metrics
- **App Performance**: Launch time, responsiveness, stability
- **Accessibility Compliance**: TalkBack compatibility and usability
- **Test Coverage**: Comprehensive test suite maintenance
- **Code Quality**: Maintainable, well-documented codebase

---

## 📄 License

MIT License - see LICENSE file for details.

---

---

## 📚 Documentation Navigation

**Choose your path:**
- **👨‍💻 Developers**: Start with [Getting Started](docs/getting-started.md) → [Architecture](docs/architecture.md) → [Development Guide](docs/development.md)
- **🎯 Product Managers**: Review [Planning Hub](planning/README.md) → [Requirements](planning/requirements.md) → [MVP Scope](planning/mvp.md)
- **🎨 Designers**: Check [Wireframes](planning/wireframes.md) → [Theme System](planning/features/theme-system.md) → [Accessibility](planning/features/accessibility-features.md)
- **📋 Contributors**: Read [Contributing Guide](docs/contributing.md) → [Development Tools](docs/development-tools-guide.md) → [Testing Guide](docs/testing.md)

**Quick Access**: [📚 Technical Docs](docs/README.md) | [📋 Planning Docs](planning/README.md) | [🎯 Getting Started](docs/getting-started.md)