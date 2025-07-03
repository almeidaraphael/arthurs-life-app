# Testing Guide

[🏠 Back to Main README](../README.md)

Comprehensive guide to testing strategies, practices, and implementation for Arthur's Life Android Kotlin application.

## 📋 Page Navigation

| Section | Description |
|---------|-------------|
| [Testing Philosophy](#testing-philosophy) | Our approach to testing |
| [Testing Strategy](#testing-strategy) | Testing pyramid and coverage |
| [Unit Testing](#unit-testing) | Domain and service layer tests |
| [Integration Testing](#integration-testing) | Repository and database tests |
| [UI Testing](#ui-testing) | Compose UI testing |
| [Testing Tools](#testing-tools) | Frameworks and utilities |

## 🔗 Related Documentation

| Topic | Link |
|-------|------|
| **Setup Guide** | [getting-started.md](getting-started.md) |
| **Contributing** | [contributing.md](contributing.md) |
| **Architecture** | [architecture.md](architecture.md) |
| **Development** | [development.md](development.md) |

## Testing Philosophy

Arthur's Life app prioritizes reliability and maintainability, making comprehensive testing essential. Our testing strategy ensures:

- **Business Logic Integrity**: Domain services and use cases work correctly
- **Token Economy Accuracy**: Precise calculations for token earnings and spending
- **Child Safety**: Secure authentication and data protection
- **Offline Functionality**: App works reliably without internet connection
- **Code Quality**: Maintainable codebase through comprehensive test coverage

## Testing Strategy

### Testing Pyramid

```
                    /\
                   /  \
                  / E2E \
                 /______\
                /        \
               /Integration\
              /____________\
             /              \
            /   Unit Tests    \
           /________________\
```

### Coverage Requirements
- **Domain Layer**: 80%+ unit test coverage (business logic critical)
- **Use Cases**: 100% coverage for business operations
- **ViewModels**: Test state changes and error handling
- **UI Components**: Test user interactions and accessibility
- **Integration**: Test database operations and repository implementations

### Testing Levels

#### Unit Tests (Foundation)
- **Domain Entities**: Business rule validation and state changes
- **Use Cases**: Business logic and error scenarios
- **Utilities**: Helper functions and extensions
- **Value Objects**: Immutability and validation

#### Integration Tests (Middle)
- **Repository Implementations**: Data access and mapping
- **Database Operations**: Room DAO and entity mapping
- **Use Case Orchestration**: Cross-service interactions

#### End-to-End Tests (Top)
- **User Workflows**: Complete task completion flows
- **Role Switching**: Authentication and permission validation
- **Data Persistence**: Cross-session data integrity
            /   Unit Tests   \
           /________________\
```

1. **Unit Tests (70%)**: Domain entities, services, and utility functions
2. **Integration Tests (20%)**: Component interactions and data flow
3. **End-to-End Tests (10%)**: Critical user journeys and accessibility

## Testing Stack

### Core Testing Framework

- **Jest** - JavaScript testing framework
- **React Native Testing Library** - Component testing utilities
- **@testing-library/jest-native** - Additional Jest matchers for React Native

### Additional Tools

- **Detox** - End-to-end testing for React Native
- **React Native Accessibility Testing** - Accessibility compliance testing
- **MSW (Mock Service Worker)** - API mocking for integration tests
- **@storybook/react-native** - Component documentation and visual testing

## Installation and Setup

### Install Testing Dependencies

```bash
# Core testing dependencies
npm install --save-dev jest @testing-library/react-native @testing-library/jest-native

# Additional testing utilities
npm install --save-dev react-test-renderer jest-environment-node

# E2E testing with Detox
npm install --save-dev detox jest-circus

# Accessibility testing
npm install --save-dev @testing-library/jest-native jest-axe
```

### Jest Configuration

Create or update `jest.config.js`:

```javascript
module.exports = {
  preset: 'react-native',
  setupFilesAfterEnv: [
    '@testing-library/jest-native/extend-expect',
    '<rootDir>/src/test/setup.ts',
  ],
  testEnvironment: 'node',
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'node'],
  transform: {
    '^.+\\.(js|jsx|ts|tsx)$': 'babel-jest',
  },
  testMatch: [
    '**/__tests__/**/*.(ts|tsx|js|jsx)',
    '**/*.(test|spec).(ts|tsx|js|jsx)',
  ],
  collectCoverageFrom: [
    'src/**/*.{ts,tsx}',
    '!src/**/*.d.ts',
    '!src/**/*.stories.{ts,tsx}',
    '!src/test/**/*',
  ],
  coverageThreshold: {
    global: {
      branches: 80,
      functions: 80,
      lines: 80,
      statements: 80,
    },
  },
  moduleNameMapping: {
    '^@/(.*)$': '<rootDir>/src/$1',
  },
};
```

### Test Setup File

Create `src/test/setup.ts`:

```typescript
import '@testing-library/jest-native/extend-expect';

// Mock React Native modules
jest.mock('react-native/Libraries/Animated/NativeAnimatedHelper');

// Mock AsyncStorage
jest.mock('@react-native-async-storage/async-storage', () =>
  require('@react-native-async-storage/async-storage/jest/async-storage-mock')
);

// Mock Sound for audio feedback
jest.mock('react-native-sound', () => {
  return {
    setCategory: jest.fn(),
  };
});

// Global test timeout
jest.setTimeout(10000);
```

## Testing Practices

### Domain Layer Testing

#### Entity Testing

```typescript
// src/domain/user/__tests__/User.test.ts
import { User, UserRole, AccessibilitySettings } from '../User';

describe('User Entity', () => {
  const mockAccessibilitySettings: AccessibilitySettings = {
    textToSpeech: true,
    largeText: false,
    highContrast: true,
  };

  const mockUser: User = {
    id: 'user-123',
    name: 'Arthur',
    role: UserRole.CHILD,
    accessibilitySettings: mockAccessibilitySettings,
    createdAt: new Date('2024-01-01'),
    lastActiveAt: new Date('2024-01-15'),
  };

  it('should create a valid user entity', () => {
    expect(mockUser.id).toBe('user-123');
    expect(mockUser.role).toBe(UserRole.CHILD);
    expect(mockUser.accessibilitySettings.textToSpeech).toBe(true);
  });

  it('should have required properties', () => {
    expect(mockUser).toHaveProperty('id');
    expect(mockUser).toHaveProperty('name');
    expect(mockUser).toHaveProperty('role');
    expect(mockUser).toHaveProperty('accessibilitySettings');
  });
});
```

#### Service Testing

```typescript
// src/domain/token/__tests__/TokenService.test.ts
import { TokenService, TokenServiceError } from '../TokenService';
import { TokenType, TokenDifficulty } from '../Token';

describe('TokenService', () => {
  let tokenService: TokenService;
  let mockTokenRepository: jest.Mocked<TokenRepository>;

  beforeEach(() => {
    mockTokenRepository = {
      saveToken: jest.fn(),
      getTokensByUserId: jest.fn(),
      getTokensByType: jest.fn(),
      updateToken: jest.fn(),
      getExpiringTokens: jest.fn(),
    };

    tokenService = new TokenService(mockTokenRepository);
  });

  describe('awardTokens', () => {
    it('should award tokens successfully', async () => {
      const mockToken = {
        id: 'token-123',
        userId: 'user-456',
        amount: 5,
        type: TokenType.TASK_COMPLETION,
        reason: 'Completed morning routine',
        earnedAt: new Date(),
        isSpent: false,
      };

      mockTokenRepository.saveToken.mockResolvedValue(mockToken);

      const result = await tokenService.awardTokens('user-456', {
        amount: 5,
        type: TokenType.TASK_COMPLETION,
        reason: 'Completed morning routine',
      });

      expect(result).toEqual(mockToken);
      expect(mockTokenRepository.saveToken).toHaveBeenCalledWith(
        expect.objectContaining({
          userId: 'user-456',
          amount: 5,
          type: TokenType.TASK_COMPLETION,
        })
      );
    });

    it('should throw error for invalid amount', async () => {
      await expect(
        tokenService.awardTokens('user-456', {
          amount: 0,
          type: TokenType.TASK_COMPLETION,
          reason: 'Invalid amount',
        })
      ).rejects.toThrow(TokenServiceError);
    });
  });

  describe('calculateTokenAmount', () => {
    it('should calculate correct amounts for different difficulties', () => {
      expect(tokenService.calculateTokenAmount(TokenDifficulty.EASY)).toBe(1);
      expect(tokenService.calculateTokenAmount(TokenDifficulty.MEDIUM)).toBe(3);
      expect(tokenService.calculateTokenAmount(TokenDifficulty.HARD)).toBe(5);
    });

    it('should apply streak bonus correctly', () => {
      const baseAmount = tokenService.calculateTokenAmount(
        TokenDifficulty.MEDIUM
      );
      const bonusAmount = tokenService.calculateTokenAmount(
        TokenDifficulty.MEDIUM,
        true
      );

      expect(bonusAmount).toBeGreaterThan(baseAmount);
      expect(bonusAmount).toBe(Math.round(baseAmount * 1.5));
    });
  });
});
```

### Component Testing

#### Basic Component Testing

```typescript
// src/ui/components/__tests__/TokenDisplay.test.tsx
import React from 'react';
import { render, screen } from '@testing-library/react-native';
import { TokenDisplay } from '../TokenDisplay';

describe('TokenDisplay Component', () => {
  it('should display token balance correctly', () => {
    render(<TokenDisplay balance={25} />);

    expect(screen.getByText('25')).toBeTruthy();
    expect(screen.getByLabelText('Token balance: 25 tokens')).toBeTruthy();
  });

  it('should be accessible to screen readers', () => {
    render(<TokenDisplay balance={10} />);

    const tokenElement = screen.getByLabelText('Token balance: 10 tokens');
    expect(tokenElement).toHaveAccessibilityRole('text');
  });

  it('should handle zero balance', () => {
    render(<TokenDisplay balance={0} />);

    expect(screen.getByText('0')).toBeTruthy();
  });
});
```

#### Accessibility Testing

```typescript
// src/ui/components/__tests__/TaskButton.accessibility.test.tsx
import React from 'react';
import { render } from '@testing-library/react-native';
import { axe, toHaveNoViolations } from 'jest-axe';
import { TaskButton } from '../TaskButton';

expect.extend(toHaveNoViolations);

describe('TaskButton Accessibility', () => {
  it('should not have accessibility violations', async () => {
    const { container } = render(
      <TaskButton
        title="Brush Teeth"
        onPress={() => {}}
        accessibilityLabel="Mark brush teeth as complete"
      />
    );

    const results = await axe(container);
    expect(results).toHaveNoViolations();
  });

  it('should have proper accessibility properties', () => {
    const { getByRole } = render(
      <TaskButton
        title="Brush Teeth"
        onPress={() => {}}
        accessibilityLabel="Mark brush teeth as complete"
        accessibilityHint="Tap to earn tokens for completing this task"
      />
    );

    const button = getByRole('button');
    expect(button).toHaveAccessibilityLabel('Mark brush teeth as complete');
    expect(button).toHaveAccessibilityHint('Tap to earn tokens for completing this task');
  });
});
```

### Integration Testing

```typescript
// src/application/__tests__/EarnToken.integration.test.ts
import { EarnTokenUseCase } from '../usecases/EarnToken';
import { TokenService } from '../../domain/token/TokenService';
import { UserService } from '../../domain/user/UserService';

describe('EarnToken Integration', () => {
  let earnTokenUseCase: EarnTokenUseCase;
  let tokenService: TokenService;
  let userService: UserService;

  beforeEach(() => {
    // Setup with real services but mocked repositories
    // This tests the integration between use case and domain services
  });

  it('should complete full token earning flow', async () => {
    // Test the complete flow from use case through domain services
    const result = await earnTokenUseCase.execute({
      userId: 'arthur-123',
      taskId: 'task-456',
      difficulty: TokenDifficulty.MEDIUM,
    });

    expect(result.success).toBe(true);
    expect(result.tokensEarned).toBe(3);
  });
});
```

## Testing Commands

Add these scripts to your `package.json`:

```json
{
  "scripts": {
    "test": "jest",
    "test:watch": "jest --watch",
    "test:coverage": "jest --coverage",
    "test:unit": "jest --testPathPattern='__tests__'",
    "test:integration": "jest --testPathPattern='integration'",
    "test:e2e": "detox test",
    "test:accessibility": "jest --testPathPattern='accessibility'",
    "test:debug": "jest --detectOpenHandles --forceExit"
  }
}
```

### Running Tests

```bash
# Run all tests
npm test

# Run tests in watch mode (for development)
npm run test:watch

# Run tests with coverage report
npm run test:coverage

# Run only unit tests
npm run test:unit

# Run integration tests
npm run test:integration

# Run end-to-end tests
npm run test:e2e

# Run accessibility tests
npm run test:accessibility

# Debug test issues
npm run test:debug
```

## E2E Testing with Detox

### Detox Configuration

Create `detox.config.js`:

```javascript
module.exports = {
  testRunner: 'jest',
  runnerConfig: 'e2e/config.json',
  apps: {
    'ios.debug': {
      type: 'ios.app',
      binaryPath:
        'ios/build/Build/Products/Debug-iphonesimulator/ArthursLife.app',
      build:
        'xcodebuild -workspace ios/ArthursLife.xcworkspace -scheme ArthursLife -configuration Debug -sdk iphonesimulator -derivedDataPath ios/build',
    },
    'android.debug': {
      type: 'android.apk',
      binaryPath: 'android/app/build/outputs/apk/debug/app-debug.apk',
      build:
        'cd android && ./gradlew assembleDebug assembleAndroidTest -DtestBuildType=debug',
    },
  },
  devices: {
    simulator: {
      type: 'ios.simulator',
      device: {
        type: 'iPhone 12',
      },
    },
    emulator: {
      type: 'android.emulator',
      device: {
        avdName: 'Pixel_4_API_30',
      },
    },
  },
  configurations: {
    'ios.sim.debug': {
      device: 'simulator',
      app: 'ios.debug',
    },
    'android.emu.debug': {
      device: 'emulator',
      app: 'android.debug',
    },
  },
};
```

### E2E Test Example

```typescript
// e2e/token-earning.e2e.ts
import { device, element, by, expect } from 'detox';

describe('Token Earning Flow', () => {
  beforeAll(async () => {
    await device.launchApp();
  });

  beforeEach(async () => {
    await device.reloadReactNative();
  });

  it('should complete task and earn tokens', async () => {
    // Navigate to tasks screen
    await element(by.id('tasks-tab')).tap();

    // Find and tap a task
    await element(by.id('task-brush-teeth')).tap();

    // Confirm task completion
    await element(by.id('confirm-task-button')).tap();

    // Verify token balance updated
    await expect(element(by.id('token-balance'))).toHaveText('5');

    // Check success message
    await expect(
      element(by.text('Great job! You earned 5 tokens!'))
    ).toBeVisible();
  });

  it('should work with accessibility features enabled', async () => {
    // Enable VoiceOver/TalkBack simulation
    await device.enableAccessibility();

    // Test that all elements are accessible
    await element(by.id('tasks-tab')).tap();
    await element(by.id('task-brush-teeth')).tap();

    // Verify accessibility announcements
    await expect(element(by.id('accessibility-announcement'))).toHaveText(
      'Task completed! 5 tokens earned!'
    );
  });
});
```

## Mock Data and Test Utilities

### Test Data Factories

```typescript
// src/test/factories/UserFactory.ts
import { User, UserRole, AccessibilitySettings } from '../../domain/user/User';

export class UserFactory {
  static createChild(overrides: Partial<User> = {}): User {
    return {
      id: 'child-123',
      name: 'Arthur',
      role: UserRole.CHILD,
      accessibilitySettings: {
        textToSpeech: true,
        largeText: true,
        highContrast: false,
      },
      createdAt: new Date('2024-01-01'),
      lastActiveAt: new Date(),
      ...overrides,
    };
  }

  static createParent(overrides: Partial<User> = {}): User {
    return {
      id: 'parent-456',
      name: 'Parent',
      role: UserRole.PARENT,
      accessibilitySettings: {
        textToSpeech: false,
        largeText: false,
        highContrast: false,
      },
      createdAt: new Date('2024-01-01'),
      lastActiveAt: new Date(),
      ...overrides,
    };
  }
}
```

### Custom Test Utilities

```typescript
// src/test/utils/renderWithProviders.tsx
import React from 'react';
import { render } from '@testing-library/react-native';
import { AccessibilityProvider } from '../../ui/providers/AccessibilityProvider';

export function renderWithProviders(
  ui: React.ReactElement,
  options: {
    accessibilitySettings?: AccessibilitySettings
  } = {}
) {
  function Wrapper({ children }: { children: React.ReactNode }) {
    return (
      <AccessibilityProvider settings={options.accessibilitySettings}>
        {children}
      </AccessibilityProvider>
    );
  }

  return render(ui, { wrapper: Wrapper, ...options });
}
```

## Coverage Requirements

### Minimum Coverage Thresholds

- **Domain Layer**: 90% (business logic is critical)
- **Application Layer**: 85% (use cases must be reliable)
- **UI Components**: 75% (focus on user interactions)
- **Infrastructure**: 70% (data access patterns)

### Coverage Reports

```bash
# Generate detailed coverage report
npm run test:coverage

# View coverage in browser
open coverage/lcov-report/index.html
```

## Continuous Integration

### GitHub Actions Example

```yaml
# .github/workflows/test.yml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '16'
          cache: 'npm'

      - name: Install dependencies
        run: npm ci

      - name: Run unit tests
        run: npm run test:coverage

      - name: Upload coverage reports
        uses: codecov/codecov-action@v3

      - name: Run accessibility tests
        run: npm run test:accessibility
```

## Testing Best Practices

### General Guidelines

1. **Write Tests First**: Follow TDD for domain logic
2. **Test Behavior, Not Implementation**: Focus on what the code does, not how
3. **Keep Tests Simple**: One assertion per test when possible
4. **Use Descriptive Names**: Test names should explain the scenario
5. **Arrange-Act-Assert Pattern**: Structure tests clearly

### Domain Testing Best Practices

1. **Test Business Rules**: Ensure domain logic is thoroughly tested
2. **Mock External Dependencies**: Keep domain tests isolated
3. **Test Edge Cases**: Handle boundary conditions and error scenarios
4. **Validate Invariants**: Ensure domain rules are never violated

### Accessibility Testing Best Practices

1. **Test with Screen Readers**: Use VoiceOver/TalkBack in tests
2. **Verify Semantic Markup**: Ensure proper accessibility roles and labels
3. **Test Keyboard Navigation**: Ensure app works without touch
4. **Check Color Contrast**: Verify accessibility color requirements
5. **Test Large Text**: Ensure UI scales properly with large fonts

### Token Economy Testing

1. **Precision is Critical**: Token calculations must be exact
2. **Test All Transaction Types**: Earning, spending, expiring
3. **Validate Balances**: Ensure balance calculations are always correct
4. **Test Concurrency**: Handle simultaneous token operations
5. **Audit Trail**: Ensure all token operations are logged

## Troubleshooting

### Common Issues

#### Jest Configuration Problems

```bash
# Clear Jest cache
npx jest --clearCache

# Reset modules
rm -rf node_modules && npm install
```

#### React Native Testing Issues

```bash
# Reset Metro bundler
npx react-native start --reset-cache

# Clear Detox cache
detox clean-framework-cache && detox build-framework-cache
```

#### Mock Issues

- Ensure all React Native modules are properly mocked
- Check that AsyncStorage and other native modules have test implementations
- Verify that date/time mocks are consistent across tests

### Debugging Tests

1. **Use `console.log`** sparingly in tests for debugging
2. **Run Single Tests**: `npm test -- --testNamePattern="specific test"`
3. **Debug Mode**: Use VS Code debugger with Jest
4. **Check Test Output**: Read Jest error messages carefully

## Additional Resources

### Documentation

- [Jest Documentation](https://jestjs.io/docs/getting-started)
- [React Native Testing Library](https://callstack.github.io/react-native-testing-library/)
- [Detox Documentation](https://github.com/wix/Detox)
- [Accessibility Testing Guide](https://reactnative.dev/docs/accessibility)

### Best Practices

- [Testing Trophy](https://kentcdodds.com/blog/the-testing-trophy-and-testing-classifications)
- [Android Testing Best Practices](https://developer.android.com/training/testing)
- [Domain-Driven Design Testing](https://martinfowler.com/articles/practical-test-pyramid.html)

---

This testing documentation ensures that Arthur's Life app maintains high
quality, accessibility, and reliability standards throughout development.

---

[🏠 Back to Main README](../README.md) | [🚀 Setup Guide](getting-started.md) | [📝 Contributing](contributing.md) | [🏗️ Architecture](ddd.md)
