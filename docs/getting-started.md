# Getting Started Guide

[🏠 Back to Main README](../README.md)

A comprehensive guide to setting up and developing Arthur's Life family task management native Android application built with Kotlin and Jetpack Compose.

## 📋 Page Navigation

| Section | Description |
|---------|-------------|
| [Prerequisites](#prerequisites) | Required software and tools |
| [Installation](#installation) | Step-by-step setup guide |
| [Running the Application](#running-the-application) | Build and run instructions |
| [Development Workflow](#development-workflow) | Daily development tasks |
| [Troubleshooting](#troubleshooting) | Common issues and solutions |

## 🔗 Related Documentation

| Topic | Link |
|-------|------|
| **Contributing Guide** | [contributing.md](contributing.md) |
| **Architecture** | [architecture.md](architecture.md) |
| **Development** | [development.md](development.md) |
| **Tech Stack** | [tech-stack.md](tech-stack.md) |

## Prerequisites

Before you begin, ensure you have the following installed on your development machine:

### Required Software

- **Android Studio** (latest version recommended) - [Download from developer.android.com](https://developer.android.com/studio)
- **Java Development Kit (JDK 21 preferred, JDK 17 fallback)** - Required for Android development
- **Git** - Version control system

### Platform-Specific Requirements

#### For Android Development

- **Android Studio** - Official IDE for Android development
- **Android SDK** (API level 24 or higher, with API 35 for compilation)
- **Android Virtual Device (AVD)** - Set up through Android Studio
- **Java Development Kit (JDK 21 preferred, JDK 17 fallback)** - Modern Java version for optimal Android development

### Java Installation

#### Linux/Ubuntu
```bash
# Install OpenJDK 21 (Preferred)
sudo apt update && sudo apt install openjdk-21-jdk

# Install OpenJDK 17 (Fallback)
sudo apt update && sudo apt install openjdk-17-jdk

# Verify installation
java -version && javac -version
```

#### macOS
```bash
# Java 21 (Preferred)
brew install openjdk@21
export JAVA_HOME="/opt/homebrew/opt/openjdk@21"

# Java 17 (Fallback)
brew install openjdk@17
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
```

#### Windows
Download and install from [Adoptium](https://adoptium.net/) - choose JDK 21 (preferred) or JDK 17 (fallback)

## Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd arthurs-life-app
```
cd arthurs-life-app/android-kotlin
```

### 2. Java Setup

#### Install Java 21 (Preferred) or Java 17 (Fallback)

**Java 21 (Recommended):**
```bash
# Linux/Ubuntu
sudo apt update && sudo apt install openjdk-21-jdk

# macOS (using Homebrew)
brew install openjdk@21
export JAVA_HOME="/opt/homebrew/opt/openjdk@21"

# Arch Linux
sudo pacman -S jdk21-openjdk

# Verify installation
java -version  # Should show Java 21
```

**Java 17 (Fallback if compatibility issues arise):**
```bash
# Linux/Ubuntu
sudo apt update && sudo apt install openjdk-17-jdk

# macOS (using Homebrew)
brew install openjdk@17
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"

# Arch Linux
sudo pacman -S jdk17-openjdk

# Verify installation
java -version  # Should show Java 17
```

#### Set Environment Variables

Add to your shell profile (`~/.bashrc`, `~/.zshrc`, etc.):

```bash
export JAVA_HOME=/path/to/java21  # Set to your Java 21 (or 17) installation
export ANDROID_HOME=$HOME/Library/Android/sdk  # macOS
export ANDROID_HOME=$HOME/Android/Sdk          # Linux
export PATH=$PATH:$JAVA_HOME/bin
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

### 3. Android Studio Configuration

1. Open Android Studio
2. Configure Project SDK to use Java 21 (preferred) or Java 17 (fallback):
   - Go to File → Project Structure → Project
   - Set Project SDK to Java 21 (or Java 17 if compatibility issues arise)
   - Set Project Language Level to 21 (or 17) or compatible
3. Configure Gradle JVM:
   - Go to Settings → Build, Execution, Deployment → Build Tools → Gradle
   - Set Gradle JVM to Java 21 (or Java 17 as fallback)
4. Create or start an Android Virtual Device (AVD) through AVD Manager

## Verify Installation

```bash
# Test your setup
cd arthurs-life-app/android-kotlin
./gradlew build
./gradlew test

# If successful, you're ready to develop!
```

## Running the Application

### Development Mode

#### Build and Run

```bash
# Build debug version
./gradlew assembleDebug

# Install and run on connected device/emulator
./gradlew installDebug

# Or run directly from Android Studio
# Click the "Run" button or use Shift+F10
```

#### Using Android Studio

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Select a device/emulator from the device dropdown
4. Click the "Run" button (green triangle) or press Shift+F10

### Physical Device Testing

#### Android Device Setup

1. Enable Developer Options on your Android device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Go to Settings → Developer Options
   - Enable "USB Debugging"
3. Connect via USB and authorize the device
4. Run: `./gradlew installDebug`

## Next Steps

After completing setup:

1. **Explore the codebase** - [Architecture Guide](architecture.md)
2. **Review development standards** - [Contributing Guide](contributing.md)  
3. **Understand our testing approach** - [Testing Documentation](testing.md)
4. **Learn about our tools** - [Development Tools](development-tools-guide.md)

## Debugging

### Android Studio Debugging

1. Set breakpoints in your Kotlin code
2. Run the app in debug mode (Debug button or Shift+F9)
3. Use the debugger to step through code execution
4. Inspect variables and call stack

### Logging

```kotlin
import android.util.Log

// Use structured logging
Log.d("UserViewModel", "Loading user data for userId: $userId")
Log.e("ApiService", "Failed to fetch data", exception)
```

### Compose Layout Inspector

1. Run your app on a device/emulator
2. Go to Tools → Layout Inspector
3. Inspect Compose UI hierarchy and properties

## Troubleshooting

### Common Issues

#### Gradle Build Issues

```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug

# Clear Gradle cache
./gradlew cleanBuildCache
```

#### Java Version Issues

**Java Version Compatibility Problems:**
```bash
# Verify Java 21 (preferred) or Java 17 (fallback) is being used
java -version
./gradlew -version

# Check Android Studio JDK settings
# File → Settings → Build Tools → Gradle → Gradle JVM
```

**Hilt Compilation Errors (Class file major version issues):**
If you encounter "Unsupported class file major version" errors:

1. **First, try using Java 21:**
   ```bash
   # Set Java 21 as default
   export JAVA_HOME=/path/to/java21
   ./gradlew --stop
   ./gradlew clean build
   ```

2. **If Java 21 causes compatibility issues, fallback to Java 17:**
   ```bash
   # Install Java 17
   sudo pacman -S jdk17-openjdk  # Arch Linux
   sudo apt install openjdk-17-jdk  # Ubuntu/Debian
   
   # Update gradle.properties
   echo "org.gradle.java.home=/path/to/java17" >> gradle.properties
   
   # Update build.gradle.kts
   # Change compileOptions and kotlinOptions to use JavaVersion.VERSION_17 and jvmTarget = "17"
   ```

3. **Update Gradle memory settings if needed:**
   ```bash
   # In gradle.properties
   org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m
   ```

#### Dependency Issues

```bash
# Refresh dependencies
./gradlew --refresh-dependencies

# Clear dependency cache
rm -rf ~/.gradle/caches/
```

#### Emulator Issues

```bash
# Reset ADB
adb kill-server && adb start-server

# List connected devices
adb devices

# Clear app data
adb shell pm clear com.arthurslife.app
```

## Performance Tips

- Use Compose performance best practices
- Implement proper state hoisting
- Use `remember` and `derivedStateOf` appropriately
- Profile with Android Studio profiler
- Optimize database queries with Room

## Accessibility Testing

Arthur's Life is built accessibility-first. Here's how our accessibility architecture works:

![Accessibility Architecture](diagrams/accessibility-architecture.svg)

*How accessibility features integrate with Android system services*

When testing:
- Enable TalkBack to test screen reader functionality
- Test with large text sizes (Settings → Display → Font size)
- Use high contrast mode to verify color accessibility
- Test keyboard navigation with external keyboard

## Getting Help

### Documentation Resources

- [Project Requirements](requirements.md)
- [Architecture Planning](planning.md)
- [Domain-Driven Design Guide](ddd.md)
- [System Architecture Diagrams](diagrams/README.md)
- [Technology Stack](tech-stack.md)

### Development Resources

- [Android Developer Documentation](https://developer.android.com/)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Domain-Driven Design Resources](https://martinfowler.com/tags/domain%20driven%20design.html)

### Community Support

- Android Developers Slack
- Kotlin Slack
- Stack Overflow (tag: android, kotlin, jetpack-compose)
- GitHub Issues for project-specific problems

---

## Next Steps

After completing the setup:

1. **Explore the Codebase**: Review existing domain entities and services
2. **Run Tests**: Ensure all tests pass in your environment (`./gradlew test`)
3. **Review Architecture**: Study the project structure and DDD documentation
4. **Start Development**: Pick up a task from the project backlog

Welcome to the Arthur's Life Android App development team! 🚀

---

[🏠 Back to Main README](../README.md) | [📝 Contributing Guide](contributing.md) | [🏗️ Architecture](architecture.md)