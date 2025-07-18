---
post_title: Top Navigation Bar
author1: LemonQwest Team
post_slug: top-navigation-bar
microsoft_alias: n/a
featured_image: n/a
categories: [UI, Navigation, Accessibility]
tags: [top navigation bar, dialogs, user-based UI, accessibility, theme]
ai_note: true
summary: Comprehensive requirements for the Top Navigation Bar in LemonQwest App, covering persistent visibility, role-based content, dialog triggers, accessibility, and user-selectable theme awareness.
post_date: 2025-07-15
---

# PRD: Top Navigation Bar

## 1. Product overview

### 1.1 Document title and version

- PRD: Top Navigation Bar
- Version: 1.0

### 1.2 Product summary

- The Top Navigation Bar is a persistent, transparent UI element present on all screens in LemonQwest App. It adapts its content based on user role (Child, Caregiver) and current screen, providing quick access to profile, settings, and contextual information (tokens, progress, rewards, achievements, child selector). It displays content according to the user's selected theme and is the primary entry point for dialogs (profile, settings, user selector) with full accessibility support.

## 2. Goals

### 2.1 Business goals

- Ensure consistent navigation and contextual information across all screens
- Support role-based UI and quick access to dialogs
- Enhance accessibility and user engagement

### 2.2 User goals

- Instantly view relevant information (tokens, progress, rewards, achievements)
- Access profile and settings dialogs quickly
- Switch users and customize experience easily

### 2.3 Non-goals

- Custom top bar layouts per user
- Third-party integrations in top bar

## 3. User personas

### 3.1 Key user types

- Child
- Caregiver

### 3.2 Basic persona details

- **Child**: Needs clear, gamified feedback and quick access to profile, tokens, and progress
- **Caregiver**: Needs oversight, child selection, and quick access to settings

### 3.3 Role-based access

- **Child**: Sees avatar, tokens, progress, achievements, settings
- **Caregiver**: Sees avatar, selected child, settings. The first caregiver is always assigned the Family Admin role by default.

## 4. Requirements

### 4.1 Functional requirements (FR)

#### 4.1.1 Top Bar Elements by Role and Screen

- **FR-1: Persistent Top Bar**
  - The Top Navigation Bar is present on all screens, with a transparent background.
- **FR-2: Role-Based Content and Screen-Specific Elements**
  - The Top Navigation Bar displays the following elements depending on user role and screen:
    - **Children Mode**
      - **Home Screen:**
        - User avatar (left, opens profile dialog)
        - Token balance (next to avatar)
        - Tasks progress (next to token balance)
        - Settings button (right, opens settings dialog)
      - **Tasks Screen:**
        - User avatar (left, opens profile dialog)
        - Token balance (next to avatar)
        - Total tokens earned (next to token balance)
        - Tasks progress (next to total tokens earned)
        - Settings button (right, opens settings dialog)
      - **Rewards Screen:**
        - User avatar (left, opens profile dialog)
        - Token balance (next to avatar)
        - Rewards available (next to token balance)
        - Settings button (right, opens settings dialog)
      - **Achievements Screen:**
        - User avatar (left, opens profile dialog)
        - Achievements progress (next to avatar)
        - Settings button (right, opens settings dialog)
    - **Caregiver Mode**
      - **Home Screen:**
        - User avatar (left, opens profile dialog)
        - Selected child (next to avatar, opens child selector dialog)
        - Settings button (right, opens settings dialog)
      - **Tasks Screen:**
        - User avatar (left, opens profile dialog)
        - Settings button (right, opens settings dialog)
      - **Rewards Screen:**
        - User avatar (left, opens profile dialog)
        - Settings button (right, opens settings dialog)
      - **Children Management Screen:**
        - User avatar (left, opens profile dialog)
        - Settings button (right, opens settings dialog)
- **FR-3: Dialog Triggers**
  - Avatar opens profile dialog; settings icon opens settings dialog; selected child (caregiver) opens child selector dialog.
- **FR-4: Dialog Requirements**
  - The following dialogs must be accessible from the top bar or its dialogs:
    - **Settings Dialog:**
      - Icon to switch users (opens user selector dialog)
      - Options: change language (opens language selector), change theme (opens theme selector)
    - **User Profile Dialog:**
      - User avatar (option to change, opens avatar dialog)
      - User name (editable)
      - User role (display only)
      - Change PIN button (Caregiver only)
      - Save, cancel, and close actions
    - **Avatar Dialog:**
      - List of pre-defined avatars
      - Select avatar, close, take photo, select from gallery
    - **User Selector Dialog:**
      - List of users (avatar, name)
      - Select user, close
    - **Child Selector Dialog:**
      - List of children (avatar, name)
      - Select child, close
    - **Theme Selector Dialog:**
      - List of available themes (Mario Classic, Material Light, Material Dark)
      - Select theme, close
    - **Language Selector Dialog:**
      - List of available languages (EN-US, PT-BR)
      - Select language, close
- **FR-5: Accessibility**
  - All elements support TalkBack, semantic roles, and color contrast.
- **FR-6: Theme Awareness**
  - The Top Navigation Bar adapts to Mario Classic or Material themes; semantic icons update per theme.

### 4.2 Non-functional requirements (NFR)

- **NFR-1: Performance**
  - The Top Navigation Bar updates instantly on role/screen change.
- **NFR-2: Scalability**
  - Supports future themes and user roles.
- **NFR-3: Responsiveness**
  - Layout adapts to different device sizes and orientations.
- **NFR-4: Zero Accessibility Violations**
  - All accessibility checks pass at all times.

## 5. User experience

### 5.1 Entry points & first-time user flow

- Top bar visible on app launch and all navigation
- Avatar and settings icons are always accessible

### 5.2 Core experience

- **Quick Access**: Tap avatar for profile, settings for customization
  - Ensures fast, intuitive navigation

### 5.3 Advanced features & edge cases

- Dynamic updates based on role/screen
- Accessibility support for all elements
- Responsive layout for different devices

### 5.4 UI/UX highlights

- Transparent background, floating elements
- Semantic icon usage
- Consistent placement and spacing

## 6. Narrative

The Top Navigation Bar provides users with instant access to their most important information and actions, adapting to their role and current context. It ensures a seamless, accessible, and engaging experience for all family members.

## 7. Success metrics

### 7.1 User-centric metrics

- Time to access profile/settings
- User satisfaction with navigation
- Accessibility compliance

### 7.2 Business metrics

- Engagement rate
- Feature adoption (dialogs, child selector)

### 7.3 Technical metrics

- Zero accessibility violations
- Responsive performance
- Instant update latency on role/screen change

## 8. Technical considerations

### 8.1 Integration points

- Navigation system
- Dialog components
- Theme system

### 8.2 Data storage & privacy

- No sensitive data stored in top bar

### 8.3 Scalability & performance

- Efficient updates on role/screen change
- Support for future themes and roles

### 8.4 Potential challenges

- Maintaining accessibility across themes
- Responsive layout for all devices
- Ensuring instant updates without lag

## 9. User stories

### 9.1. Top Bar Visibility

- **ID**: TB-1
- **Description**: The Top Navigation Bar is visible and functional on all screens.
- **Acceptance criteria**:
  - Given the app is running, When the user navigates to any screen, Then the Top Navigation Bar is present with a transparent background and responsive layout.

### 9.2. Role-Based Content and Screen-Specific Elements

- **ID**: TB-2
- **Description**: Top bar displays correct elements for each role and screen.
- **Acceptance criteria**:
  - Given the user is in Children mode and on the Home screen, When the screen is displayed, Then the top bar shows avatar, token balance, tasks progress, and settings button as specified.
  - Given the user is in Children mode and on the Tasks screen, When the screen is displayed, Then the top bar shows avatar, token balance, total tokens earned, tasks progress, and settings button as specified.
  - Given the user is in Children mode and on the Rewards screen, When the screen is displayed, Then the top bar shows avatar, token balance, rewards available, and settings button as specified.
  - Given the user is in Children mode and on the Achievements screen, When the screen is displayed, Then the top bar shows avatar, achievements progress, and settings button as specified.
  - Given the user is in Caregiver mode and on the Home screen, When the screen is displayed, Then the top bar shows avatar, selected child, and settings button as specified.
  - Given the user is in Caregiver mode and on the Tasks, Rewards, or Children Management screens, When the screen is displayed, Then the top bar shows avatar and settings button as specified.
  - Given the user changes role or screen, When the change occurs, Then the Top Navigation Bar updates content instantly.

### 9.3. Dialog Triggers and Requirements

- **ID**: TB-3
- **Description**: Top bar elements trigger dialogs and dialogs meet requirements.
- **Acceptance criteria**:
  - Given the Top Navigation Bar is visible, When the user taps the avatar, Then the profile dialog opens with all specified elements and actions.
  - Given the Top Navigation Bar is visible, When the user taps the settings icon, Then the settings dialog opens with all specified options and actions.
  - Given the user is in Caregiver mode and a child is selected, When the user taps the selected child, Then the child selector dialog opens with all specified elements and actions.
  - Given the user is in any dialog, When the user selects an option to open another dialog (e.g., theme, language, avatar), Then the correct dialog opens with all specified elements and actions.

### 9.4. Accessibility

- **ID**: TB-4
- **Description**: Top bar and all dialogs support accessibility standards.
- **Acceptance criteria**:
  - Given the Top Navigation Bar or any dialog is visible, When accessibility checks are run, Then all elements have semantic roles, color contrast meets 4.5:1, and TalkBack is fully supported.

### 9.5. Theme Awareness

- **ID**: TB-5
- **Description**: Top bar and dialogs adapt to selected theme.
- **Acceptance criteria**:
  - Given the user changes theme, When the theme is Mario Classic, Material Light, or Material Dark, Then the Top Navigation Bar and all dialogs adapt their style and semantic icons accordingly.
