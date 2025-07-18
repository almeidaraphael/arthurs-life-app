# LemonQwest Android App - Makefile
# Modern industry-standard build automation for Android/Kotlin development
# Version: 1.0.0

# ==============================================================================
# CONFIGURATION
# ==============================================================================

# Colors for better terminal output
RED := \\033[0;31m
GREEN := \\033[0;32m
YELLOW := \\033[1;33m
BLUE := \\033[0;34m
PURPLE := \\033[0;35m
CYAN := \\033[0;36m
WHITE := \\033[1;37m
NC := \\033[0m # No Color

# Project configuration
PROJECT_NAME := LemonQwest
ANDROID_DIR := android-kotlin
DOCS_DIR := docs
DIAGRAMS_DIR := $(DOCS_DIR)/diagrams
MEMORY_BANK_DIR := memory-bank

# Gradle wrapper
GRADLEW := ./gradlew
GRADLE_ARGS := --console=plain

# Build variants
DEBUG_VARIANT := Debug
RELEASE_VARIANT := Release

# Ensure gradlew exists and is executable
check-gradlew: | $(ANDROID_DIR)
	@if [ ! -f "$(ANDROID_DIR)/gradlew" ]; then \
		echo -e "$(RED)❌ Gradle wrapper not found: $(ANDROID_DIR)/gradlew$(NC)"; \
		exit 1; \
	fi
	@chmod +x $(ANDROID_DIR)/gradlew

# ==============================================================================
# DEFAULT TARGET
# ==============================================================================

.DEFAULT_GOAL := help

# ==============================================================================
# HELP TARGET
# ==============================================================================

.PHONY: help
help: ## Show this help message
	@echo -e "$(CYAN)╔══════════════════════════════════════════════════════════════════════════════╗$(NC)"
	@echo -e "$(CYAN)║                          $(WHITE)$(PROJECT_NAME) Build System$(CYAN)         ║$(NC)"
	@echo -e "$(CYAN)╚══════════════════════════════════════════════════════════════════════════════╝$(NC)"
	@echo ""
	@echo -e "$(WHITE)Available targets:$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[0;36m%-20s\033[0m %s\n", $$1, $$2}' | sort
	@echo ""
	@echo -e "$(YELLOW)Build Pipeline (recommended order):$(NC)"
	@echo -e "  $(GREEN)make setup → make format → make lint → make build → make test → make install$(NC)"
	@echo ""
	@echo -e "$(YELLOW)Quality Assurance:$(NC)"
	@echo -e "  $(GREEN)make qa$(NC) - Run complete quality pipeline"
	@echo ""
	@echo -e "$(YELLOW)Parallel Testing:$(NC)"
	@echo -e "  $(GREEN)make test PARALLEL=true$(NC) - Run tests in parallel mode"
	@echo -e "  $(GREEN)make test-unit PARALLEL=true$(NC) - Run unit tests in parallel"
	@echo -e "  $(GREEN)make test-filter PATTERN=\"*Test*\" PARALLEL=true$(NC) - Run filtered tests in parallel"
	@echo ""
	@echo -e "$(YELLOW)Documentation:$(NC)"
	@echo -e "  $(GREEN)make docs$(NC) - Generate all documentation"
	@echo ""
	@echo -e "$(YELLOW)📚 Detailed Documentation:$(NC)"
	@echo -e "  $(CYAN)docs/makefile-usage.md$(NC) - Comprehensive usage guide with examples"
	@echo -e "  $(CYAN)docs/makefile-implementation.md$(NC) - Technical implementation details"

# ==============================================================================
# SETUP AND ENVIRONMENT
# ==============================================================================

.PHONY: setup
setup: ## Setup development environment and dependencies
	@echo -e "$(BLUE)🔧 Setting up development environment...$(NC)"
	@echo -e "$(YELLOW)Checking Java version...$(NC)"
	@java -version
	@echo -e "$(YELLOW)Checking Android SDK...$(NC)"
	@if [ -z "$$ANDROID_HOME" ]; then \
		echo -e "$(RED)❌ ANDROID_HOME not set. Please install Android SDK.$(NC)"; \
		exit 1; \
	fi
	@echo -e "$(GREEN)✅ Android SDK found at: $$ANDROID_HOME$(NC)"
	@echo -e "$(YELLOW)Making gradlew executable...$(NC)"
	@chmod +x ./gradlew
	@echo -e "$(YELLOW)Downloading dependencies...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) dependencies --write-verification-metadata sha256
	@echo -e "$(GREEN)✅ Development environment setup complete!$(NC)"

.PHONY: clean-all
clean-all: ## Clean all build artifacts and caches
	@echo -e "$(BLUE)🧹 Cleaning all build artifacts...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) clean
	@rm -rf $(ANDROID_DIR)/.gradle
	@rm -rf $(ANDROID_DIR)/build
	@rm -rf $(ANDROID_DIR)/app/build
	@rm -rf $(ANDROID_DIR)/app/.gradle # Remove app module Gradle tasks cache
	@echo -e "$(GREEN)✅ All build artifacts cleaned!$(NC)"

# ==============================================================================
# CODE FORMATTING AND LINTING
# ==============================================================================

.PHONY: format
format: check-gradlew ## Format code with detekt and kotlinter
	@echo -e "$(BLUE)🎨 Formatting code...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) detektFormat
	@echo -e "$(GREEN)✅ Code formatting complete!$(NC)"

.PHONY: lint
lint: check-gradlew ## Run static analysis (detekt)
	@echo -e "$(BLUE)🔍 Running static analysis...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) detekt
	@echo -e "$(GREEN)✅ Static analysis complete!$(NC)"

.PHONY: lint-fix
lint-fix: ## Run lint and apply safe fixes
	@echo -e "$(BLUE)🔧 Running lint with auto-fix...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) lintFix
	@echo -e "$(GREEN)✅ Lint fixes applied!$(NC)"

.PHONY: detekt-baseline
detekt-baseline: ## Create detekt baseline for existing issues
	@echo -e "$(BLUE)📋 Creating detekt baseline...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) detektBaseline
	@echo -e "$(GREEN)✅ Detekt baseline created!$(NC)"

# ==============================================================================
# BUILDING
# ==============================================================================

.PHONY: build
build: check-gradlew ## Build all variants (debug + release)
	@echo -e "$(BLUE)🔨 Building all variants...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) build
	@echo -e "$(GREEN)✅ Build complete!$(NC)"

.PHONY: build-debug
build-debug: ## Build debug variant
	@echo -e "$(BLUE)🔨 Building debug variant...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) assemble$(DEBUG_VARIANT)
	@echo -e "$(GREEN)✅ Debug build complete!$(NC)"

.PHONY: build-release
build-release: ## Build release variant
	@echo -e "$(BLUE)🔨 Building release variant...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) assemble$(RELEASE_VARIANT)
	@echo -e "$(GREEN)✅ Release build complete!$(NC)"

.PHONY: bundle
bundle: ## Build Android App Bundle (AAB)
	@echo -e "$(BLUE)📦 Building Android App Bundle...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) bundle
	@echo -e "$(GREEN)✅ App Bundle build complete!$(NC)"

# ==============================================================================
# TESTING
# ==============================================================================

.PHONY: test
test: ## Run all tests (unit + integration) - supports PARALLEL=true for parallel execution
	@echo -e "$(BLUE)🧪 Running all tests...$(NC)"
	@if [ "$(PARALLEL)" = "true" ]; then \
		echo -e "$(YELLOW)⚡ Running tests in parallel mode$(NC)"; \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) --parallel test; \
	else \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) test; \
	fi
	@echo -e "$(GREEN)✅ All tests completed!$(NC)"

.PHONY: test-unit
test-unit: ## Run unit tests only - supports PARALLEL=true for parallel execution
	@echo -e "$(BLUE)🧪 Running unit tests...$(NC)"
	@if [ "$(PARALLEL)" = "true" ]; then \
		echo -e "$(YELLOW)⚡ Running tests in parallel mode$(NC)"; \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) --parallel testDebugUnitTest; \
	else \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) testDebugUnitTest; \
	fi
	@echo -e "$(GREEN)✅ Unit tests completed!$(NC)"

.PHONY: test-integration
test-integration: ## Run instrumentation tests (requires connected device/emulator) - supports PARALLEL=true
	@echo -e "$(BLUE)🧪 Running instrumentation tests...$(NC)"
	@echo -e "$(YELLOW)⚠️  Make sure device/emulator is connected and unlocked$(NC)"
	@if [ "$(PARALLEL)" = "true" ]; then \
		echo -e "$(YELLOW)⚡ Running tests in parallel mode$(NC)"; \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) --parallel connectedDebugAndroidTest; \
	else \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) connectedDebugAndroidTest; \
	fi
	@echo -e "$(GREEN)✅ Instrumentation tests completed!$(NC)"

.PHONY: test-coverage
test-coverage: ## Generate test coverage report - supports PARALLEL=true for parallel execution
	@echo -e "$(BLUE)📊 Generating test coverage report...$(NC)"
	@if [ "$(PARALLEL)" = "true" ]; then \
		echo -e "$(YELLOW)⚡ Running tests in parallel mode$(NC)"; \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) --parallel jacocoTestReport; \
	else \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) jacocoTestReport; \
	fi
	@echo -e "$(GREEN)✅ Coverage report generated!$(NC)"
	@echo -e "$(CYAN)📂 Coverage report: $(ANDROID_DIR)/app/build/reports/jacoco/jacocoTestReport/html/index.html$(NC)"

.PHONY: test-coverage-verify
test-coverage-verify: ## Verify test coverage meets minimum thresholds - supports PARALLEL=true
	@echo -e "$(BLUE)🎯 Verifying test coverage thresholds...$(NC)"
	@if [ "$(PARALLEL)" = "true" ]; then \
		echo -e "$(YELLOW)⚡ Running tests in parallel mode$(NC)"; \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) --parallel jacocoCoverageVerification; \
	else \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) jacocoCoverageVerification; \
	fi
	@echo -e "$(GREEN)✅ Coverage verification complete!$(NC)"

.PHONY: test-filter
test-filter: ## Run unit tests with filter pattern - supports PARALLEL=true (usage: make test-filter PATTERN="*YourTest*" PARALLEL=true)
	@if [ -z "$(PATTERN)" ]; then \
		echo -e "$(RED)❌ Error: PATTERN is required. Usage: make test-filter PATTERN=\"*YourTest*\"$(NC)"; \
		exit 1; \
	fi
	@echo -e "$(BLUE)🧪 Running unit tests with pattern: $(PATTERN)$(NC)"
	@if [ "$(PARALLEL)" = "true" ]; then \
		echo -e "$(YELLOW)⚡ Running tests in parallel mode$(NC)"; \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) --parallel :app:testDebugUnitTest --tests "$(PATTERN)"; \
	else \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) :app:testDebugUnitTest --tests "$(PATTERN)"; \
	fi
	@echo -e "$(GREEN)✅ Filtered unit tests completed!$(NC)"

.PHONY: test-class
test-class: ## Run tests for a specific class - supports PARALLEL=true (usage: make test-class CLASS="YourTestClass" PARALLEL=true)
	@if [ -z "$(CLASS)" ]; then \
		echo -e "$(RED)❌ Error: CLASS is required. Usage: make test-class CLASS=\"YourTestClass\"$(NC)"; \
		exit 1; \
	fi
	@echo -e "$(BLUE)🧪 Running tests for class: $(CLASS)$(NC)"
	@if [ "$(PARALLEL)" = "true" ]; then \
		echo -e "$(YELLOW)⚡ Running tests in parallel mode$(NC)"; \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) --parallel :app:testDebugUnitTest --tests "*$(CLASS)*"; \
	else \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) :app:testDebugUnitTest --tests "*$(CLASS)*"; \
	fi
	@echo -e "$(GREEN)✅ Class tests completed!$(NC)"

.PHONY: test-package
test-package: ## Run tests for a specific package - supports PARALLEL=true (usage: make test-package PACKAGE="com.example.domain" PARALLEL=true)
	@if [ -z "$(PACKAGE)" ]; then \
		echo -e "$(RED)❌ Error: PACKAGE is required. Usage: make test-package PACKAGE=\"com.example.domain\"$(NC)"; \
		exit 1; \
	fi
	@echo -e "$(BLUE)🧪 Running tests for package: $(PACKAGE)$(NC)"
	@if [ "$(PARALLEL)" = "true" ]; then \
		echo -e "$(YELLOW)⚡ Running tests in parallel mode$(NC)"; \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) --parallel :app:testDebugUnitTest --tests "$(PACKAGE).*"; \
	else \
		cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) :app:testDebugUnitTest --tests "$(PACKAGE).*"; \
	fi
	@echo -e "$(GREEN)✅ Package tests completed!$(NC)"

# ==============================================================================
# PARALLEL TESTING SHORTCUTS
# ==============================================================================

.PHONY: test-parallel
test-parallel: ## Run all tests in parallel mode (shortcut for PARALLEL=true)
	@$(MAKE) test PARALLEL=true

.PHONY: test-unit-parallel
test-unit-parallel: ## Run unit tests in parallel mode (shortcut for PARALLEL=true)
	@$(MAKE) test-unit PARALLEL=true

.PHONY: test-coverage-parallel
test-coverage-parallel: ## Generate test coverage report in parallel mode
	@$(MAKE) test-coverage PARALLEL=true

# ==============================================================================
# INSTALLATION AND DEPLOYMENT
# ==============================================================================

.PHONY: install
install: ## Install debug APK on connected device/emulator
	@echo -e "$(BLUE)📱 Installing debug APK...$(NC)"
	@echo -e "$(YELLOW)⚠️  Make sure device/emulator is connected and USB debugging enabled$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) installDebug
	@echo -e "$(GREEN)✅ Debug APK installed!$(NC)"

.PHONY: install-release
install-release: ## Install release APK on connected device/emulator
	@echo -e "$(BLUE)📱 Installing release APK...$(NC)"
	@echo -e "$(YELLOW)⚠️  Make sure device/emulator is connected and USB debugging enabled$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) installRelease
	@echo -e "$(GREEN)✅ Release APK installed!$(NC)"

.PHONY: uninstall
uninstall: ## Uninstall app from all connected devices
	@echo -e "$(BLUE)🗑️  Uninstalling app...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) uninstallAll
	@echo -e "$(GREEN)✅ App uninstalled!$(NC)"

# ==============================================================================
# QUALITY ASSURANCE PIPELINE
# ==============================================================================

.PHONY: qa
qa: format lint build test ## Run complete quality assurance pipeline
	@echo -e "$(GREEN)🎉 Quality assurance pipeline completed successfully!$(NC)"

.PHONY: qa-full
qa-full: format lint build test test-coverage-verify ## Run full QA pipeline with coverage verification
	@echo -e "$(GREEN)🎉 Full quality assurance pipeline completed successfully!$(NC)"

.PHONY: ci
ci: qa ## Alias for continuous integration pipeline
	@echo -e "$(GREEN)🤖 CI pipeline completed successfully!$(NC)"

# ==============================================================================
# DOCUMENTATION
# ==============================================================================

.PHONY: docs
docs: docs-diagrams ## Generate all documentation
	@echo -e "$(GREEN)📚 All documentation generated!$(NC)"

.PHONY: docs-diagrams
docs-diagrams: ## Generate architecture diagrams
	@echo -e "$(BLUE)🎨 Generating architecture diagrams...$(NC)"
	@$(MAKE) -C $(DIAGRAMS_DIR) all
	@echo -e "$(GREEN)✅ Architecture diagrams generated!$(NC)"

.PHONY: docs-clean
docs-clean: ## Clean generated documentation
	@echo -e "$(BLUE)🧹 Cleaning generated documentation...$(NC)"
	@$(MAKE) -C $(DIAGRAMS_DIR) clean
	@echo -e "$(GREEN)✅ Documentation cleaned!$(NC)"

# ==============================================================================
# DEVELOPMENT UTILITIES
# ==============================================================================

.PHONY: dependencies
dependencies: ## Show dependency tree
	@echo -e "$(BLUE)🌳 Showing dependency tree...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) dependencies

.PHONY: tasks
tasks: ## Show all available Gradle tasks
	@echo -e "$(BLUE)📋 Showing all Gradle tasks...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) tasks --all

.PHONY: version
version: ## Show version information
	@echo -e "$(BLUE)📋 Project Version Information$(NC)"
	@echo -e "$(YELLOW)Project:$(NC) $(PROJECT_NAME)"
	@echo -e "$(YELLOW)Gradle Wrapper:$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew --version
	@echo -e "$(YELLOW)Java:$(NC)"
	@java -version

.PHONY: check-env
check-env: ## Check development environment
	@echo -e "$(BLUE)🔍 Checking development environment...$(NC)"
	@echo -e "$(YELLOW)ANDROID_HOME:$(NC) $$ANDROID_HOME"
	@echo -e "$(YELLOW)JAVA_HOME:$(NC) $$JAVA_HOME"
	@echo -e "$(YELLOW)PATH:$(NC) $$PATH"
	@echo -e "$(YELLOW)Current directory:$(NC) $$(pwd)"
	@echo -e "$(YELLOW)Gradle wrapper:$(NC) $$(ls -la ./gradlew 2>/dev/null || echo 'Not found')"

# ==============================================================================
# MEMORY BANK INTEGRATION
# ==============================================================================

.PHONY: memory-bank-status
memory-bank-status: ## Show Memory Bank status and context
	@echo -e "$(BLUE)🧠 Memory Bank Status$(NC)"
	@echo -e "$(YELLOW)Active Context:$(NC)"
	@if [ -f "$(MEMORY_BANK_DIR)/activeContext.md" ]; then \
		head -n 10 "$(MEMORY_BANK_DIR)/activeContext.md"; \
	else \
		echo -e "$(RED)❌ Active context not found$(NC)"; \
	fi
	@echo -e "$(YELLOW)Recent Progress:$(NC)"
	@if [ -f "$(MEMORY_BANK_DIR)/progress.md" ]; then \
		tail -n 10 "$(MEMORY_BANK_DIR)/progress.md"; \
	else \
		echo -e "$(RED)❌ Progress log not found$(NC)"; \
	fi

# ==============================================================================
# ADVANCED BUILD FEATURES
# ==============================================================================

.PHONY: build-cache-stats
build-cache-stats: ## Show Gradle build cache statistics
	@echo -e "$(BLUE)📊 Build cache statistics...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) --build-cache build --info | grep -i cache || true

.PHONY: profile-build
profile-build: ## Profile build performance
	@echo -e "$(BLUE)⏱️  Profiling build performance...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) --profile build
	@echo -e "$(CYAN)📂 Build profile: $(ANDROID_DIR)/build/reports/profile/$(NC)"

.PHONY: dry-run
dry-run: ## Dry run of build process
	@echo -e "$(BLUE)🏃 Performing build dry run...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) --dry-run build

# ==============================================================================
# SECURITY AND COMPLIANCE
# ==============================================================================

.PHONY: security-scan
security-scan: ## Run security analysis
	@echo -e "$(BLUE)🔒 Running security analysis...$(NC)"
	@echo -e "$(YELLOW)Note: Add security scanning tools like OWASP dependency-check$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) lint
	@echo -e "$(GREEN)✅ Security scan completed!$(NC)"

.PHONY: license-check
license-check: ## Check dependency licenses
	@echo -e "$(BLUE)⚖️  Checking dependency licenses...$(NC)"
	@echo -e "$(YELLOW)Note: Consider adding license checking plugin$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) dependencies | grep -i license || echo "No license information found"

# ==============================================================================
# DEBUGGING AND TROUBLESHOOTING
# ==============================================================================

.PHONY: debug-info
debug-info: ## Show debug information for troubleshooting
	@echo -e "$(BLUE)🐛 Debug Information$(NC)"
	@echo -e "$(YELLOW)Build environment:$(NC)"
	@$(MAKE) check-env
	@echo ""
	@echo -e "$(YELLOW)Gradle daemon status:$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew --status || true
	@echo ""
	@echo -e "$(YELLOW)Recent build logs:$(NC)"
	@find $(ANDROID_DIR)/build -name "*.log" -type f -exec ls -la {} \; 2>/dev/null | head -5 || echo "No log files found"

.PHONY: stop-daemons
stop-daemons: ## Stop all Gradle daemons
	@echo -e "$(BLUE)🛑 Stopping Gradle daemons...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew --stop
	@echo -e "$(GREEN)✅ Gradle daemons stopped!$(NC)"

# ==============================================================================
# INTEGRATION WITH GITHUB COPILOT INSTRUCTIONS
# ==============================================================================

.PHONY: copilot-pipeline
copilot-pipeline: ## Run the mandatory Copilot pipeline from .github/copilot-instructions.md
	@echo -e "$(BLUE)🤖 Running GitHub Copilot mandatory pipeline...$(NC)"
	@echo -e "$(YELLOW)Following .github/copilot-instructions.md requirements$(NC)"
	@cd $(ANDROID_DIR) && \
	if [ "$$(basename $$(pwd))" != "android-kotlin" ]; then \
		echo -e "$(RED)❌ Not in android-kotlin directory$(NC)"; \
		exit 1; \
	fi && \
	echo "$(CYAN)Step 1: Format code...$(NC)" && \
	./gradlew $(GRADLE_ARGS) detektFormat && \
	echo "$(CYAN)Step 2: Static analysis (ZERO violations required)...$(NC)" && \
	./gradlew $(GRADLE_ARGS) detekt && \
	echo "$(CYAN)Step 3: Build...$(NC)" && \
	./gradlew $(GRADLE_ARGS) build && \
	echo "$(CYAN)Step 4: Test...$(NC)" && \
	./gradlew $(GRADLE_ARGS) test && \
	echo "$(CYAN)Step 5: Install debug...$(NC)" && \
	./gradlew $(GRADLE_ARGS) installDebug
	@echo -e "$(GREEN)🎉 GitHub Copilot pipeline completed successfully!$(NC)"
	@echo -e "$(WHITE)All requirements from .github/copilot-instructions.md satisfied$(NC)"

# ==============================================================================
# FILE WATCHING AND CONTINUOUS DEVELOPMENT
# ==============================================================================

.PHONY: watch-test
watch-test: ## Watch for changes and run tests continuously (requires entr)
	@echo -e "$(BLUE)👀 Watching for changes and running tests...$(NC)"
	@echo -e "$(YELLOW)Note: Requires 'entr' utility. Install with: brew install entr (macOS) or apt-get install entr (Ubuntu)$(NC)"
	@find $(ANDROID_DIR)/app/src -name "*.kt" | entr -c make test-unit

# ==============================================================================
# CLEANUP TARGETS
# ==============================================================================

.PHONY: clean
clean: ## Clean build artifacts
	@echo -e "$(BLUE)🧹 Cleaning build artifacts...$(NC)"
	@cd $(ANDROID_DIR) && ./gradlew $(GRADLE_ARGS) clean
	@echo -e "$(GREEN)✅ Build artifacts cleaned!$(NC)"

.PHONY: deep-clean
deep-clean: clean-all docs-clean stop-daemons ## Deep clean everything (build, cache, docs, daemons)
	@echo -e "$(GREEN)🧹 Deep clean completed!$(NC)"

# ==============================================================================
# VALIDATION
# ==============================================================================

# Ensure android-kotlin directory exists
$(ANDROID_DIR):
	@echo -e "$(RED)❌ Android project directory not found: $(ANDROID_DIR)$(NC)"
	@exit 1

# ==============================================================================
# PHONY DECLARATIONS
# ==============================================================================

.PHONY: all help setup clean-all format lint lint-fix detekt-baseline
.PHONY: build build-debug build-release bundle
.PHONY: test test-unit test-integration test-coverage test-coverage-verify test-filter test-class test-package
.PHONY: test-parallel test-unit-parallel test-coverage-parallel
.PHONY: install install-release uninstall
.PHONY: qa qa-full ci
.PHONY: docs docs-diagrams docs-clean
.PHONY: dependencies tasks version check-env
.PHONY: memory-bank-status
.PHONY: build-cache-stats profile-build dry-run
.PHONY: security-scan license-check
.PHONY: debug-info stop-daemons
.PHONY: copilot-pipeline
.PHONY: watch-test
.PHONY: clean deep-clean
