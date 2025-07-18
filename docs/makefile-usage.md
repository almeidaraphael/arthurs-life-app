# LemonQwest Makefile Usage Guide

This Makefile provides a comprehensive build automation system for the LemonQwest Android application, following modern industry standards and best practices.

## Quick Start

```bash
# Show all available targets
make help

# Setup development environment
make setup

# Run the complete quality pipeline
make qa

# Follow GitHub Copilot mandatory pipeline
make copilot-pipeline
```

## Key Features

### 🎨 **Colored Output**
Beautiful terminal output with color-coded messages for better developer experience.

### 🔧 **Environment Validation**
Automatic checks for Android SDK, Java version, and Gradle wrapper.

### 📋 **Comprehensive Help**
Self-documenting Makefile with detailed help for each target.

### 🤖 **GitHub Copilot Integration**
Special `copilot-pipeline` target that follows the mandatory build sequence from `.github/copilot-instructions.md`.

## Most Common Workflows

### Development Workflow
```bash
make setup          # One-time setup
make format          # Format code
make lint            # Static analysis
make build           # Build all variants
make test            # Run tests
make install         # Install on device
```

### Quality Assurance
```bash
make qa              # Complete QA pipeline
make qa-full         # QA + coverage verification
make test-coverage   # Generate coverage report
```

### GitHub Copilot Workflow
```bash
make copilot-pipeline   # Mandatory pipeline: format → lint → build → test → install
```

## Target Categories

### 🔧 **Setup & Environment**
- `setup` - Setup development environment
- `check-env` - Check development environment
- `clean-all` - Clean all build artifacts

### 🎨 **Code Quality**
- `format` - Format code with detekt
- `lint` - Run static analysis
- `lint-fix` - Apply safe lint fixes
- `detekt-baseline` - Create detekt baseline

### 🔨 **Building**
- `build` - Build all variants
- `build-debug` - Build debug variant
- `build-release` - Build release variant
- `bundle` - Build Android App Bundle

### 🧪 **Testing**
- `test` - Run all tests
- `test-unit` - Unit tests only
- `test-integration` - Instrumentation tests
- `test-coverage` - Generate coverage report

### 📱 **Installation**
- `install` - Install debug APK
- `install-release` - Install release APK
- `uninstall` - Uninstall from all devices

### 📚 **Documentation**
- `docs` - Generate all documentation
- `docs-diagrams` - Generate architecture diagrams
- `memory-bank-status` - Show Memory Bank status

### 🔍 **Utilities**
- `dependencies` - Show dependency tree
- `tasks` - Show all Gradle tasks
- `version` - Show version information
- `debug-info` - Debug information

## Advanced Features

### Build Cache and Performance
- `build-cache-stats` - Show build cache statistics
- `profile-build` - Profile build performance
- `dry-run` - Dry run of build process

### Security and Compliance
- `security-scan` - Run security analysis
- `license-check` - Check dependency licenses

### Debugging
- `debug-info` - Show debug information
- `stop-daemons` - Stop all Gradle daemons

## Prerequisites

1. **Java 17 or 21** - Required for Android development
2. **Android SDK** - Set `ANDROID_HOME` environment variable
3. **Docker** - Optional, for generating architecture diagrams

## Environment Variables

- `ANDROID_HOME` - Path to Android SDK (required)
- `JAVA_HOME` - Path to Java installation (optional but recommended)

## Project Structure

The Makefile expects this directory structure:
```
lemonqwest-app/
├── Makefile                 # This file
├── android-kotlin/          # Android project directory
│   ├── gradlew             # Gradle wrapper
│   └── ...
├── docs/                   # Documentation
│   └── diagrams/           # Architecture diagrams
└── memory-bank/            # Memory Bank files
```

## Troubleshooting

### Common Issues

**"gradlew not found"**
```bash
make setup  # This will set up the gradle wrapper
```

**"ANDROID_HOME not set"**
```bash
export ANDROID_HOME=/path/to/android/sdk
```

**Build failures**
```bash
make clean-all  # Clean everything
make setup      # Re-setup environment
make qa         # Try again
```

**Daemon issues**
```bash
make stop-daemons  # Stop all Gradle daemons
make clean-all     # Clean build cache
```

### Debug Information
```bash
make debug-info    # Show comprehensive debug information
make check-env     # Check environment setup
```

## Integration with GitHub Copilot

This Makefile is specifically designed to work with GitHub Copilot's engineering standards defined in `.github/copilot-instructions.md`. The `copilot-pipeline` target ensures:

1. ✅ **Zero Detekt violations** (mandatory)
2. ✅ **All tests passing** (80%+ domain coverage)
3. ✅ **Successful build and installation**
4. ✅ **Theme compatibility** across all roles
5. ✅ **Accessibility compliance**

## Memory Bank Integration

The Makefile integrates with the Memory Bank system:
- `memory-bank-status` - Shows current context and progress
- Supports the documentation-driven development workflow
- Maintains traceability between PRDs, IPDs, and tasks

## Contributing

When adding new targets:
1. Follow the existing naming conventions
2. Add appropriate help documentation (`## Help text`)
3. Use color coding for output
4. Add dependencies where appropriate
5. Update this README

## License

This Makefile is part of the LemonQwest project and follows the same license terms.
