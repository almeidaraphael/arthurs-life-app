---
post_title: Switch User Feature
author1: Arthur's Life Team
post_slug: switch-user-feature
microsoft_alias: n/a
featured_image: https://example.com/images/switch-user-feature.png
categories: [core-features, authentication, accessibility]
tags: [switch-user, role-management, PIN, session, navigation, accessibility]
ai_note: "AI-assisted PRD creation"
summary: "Enables secure, accessible, and seamless role switching between Child and Caregiver profiles on shared devices, with PIN protection and session management."
post_date: 2025-07-15
---

# PRD: Switch User Feature

## 1. Product overview

### 1.1 Document title and version
* PRD: Switch User Feature
* Version: 1.0

### 1.2 Product summary
The Switch User feature allows families to use Arthur's Life App on shared devices, enabling seamless switching between any available profiles. Any user can switch to any profile at any time, with PIN authentication required only for elevated (Caregiver) roles. This ensures session integrity, privacy, and child safety while maintaining a trusted, accessible, and user-customizable theme experience. The feature is integrated into the top navigation bar for easy access.

## 2. Goals

### 2.1 Business goals
* Support multi-user families on shared devices
* Enforce secure, role-based access
* Improve user experience for families with multiple roles

### 2.2 User goals
* Easily switch to any available profile
* Secure role switching with PIN protection for elevated roles
* Maintain privacy and session integrity

### 2.3 Non-goals
* External authentication providers
* Simultaneous multi-user sessions
* Social media login

## 3. User personas

### 3.1 Key user types
* Child
* Caregiver

### 3.2 Basic persona details
* **Child**: Can switch to any available profile using the Switch User dialog; UI adapts to their selected theme preference; PIN is only required for switching to elevated (Caregiver) roles.
* **Caregiver**: Can switch to any available profile, including Child and Caregiver roles; PIN is required for switching to Caregiver role.

### 3.3 Role-based access
* **All users**: Can access the Switch User dialog and switch to any profile at any time. PIN authentication is only enforced when switching to an elevated (Caregiver) role. The admin account is always the first caregiver registered and has elevated permissions for switching roles or managing users.
* **No restrictions**: There are no limitations on user switching beyond PIN protection for elevated roles. The app assumes a trusted environment for shared devices and offline use.

## 4. Requirements

### 4.1 Functional requirements (FR)
* **FR-SU-10**: User Selector Dialog
  * The user selector dialog is accessible from the settings dialog (opened via the top bar) and from the Switch User button in the top bar.
  * The dialog displays a list of users, each with avatar and name, a button to select a user, and a close button to exit without switching.
  * The dialog is fully accessible (TalkBack, color contrast, minimum tap targets) and theme-aware.
* **FR-SU-11**: Child Selector Dialog
  * The child selector dialog is accessible from the top bar in caregiver mode (by tapping the selected child).
  * The dialog displays a list of children, each with avatar and name, a button to select a child, and a close button to exit without switching.
  * The dialog is fully accessible (TalkBack, color contrast, minimum tap targets) and theme-aware.
* **FR-SU-01**: Role switching via top navigation bar  
  * Any user can access the Switch User dialog from the top navigation bar on any screen and switch to any available profile.
* **FR-SU-02**: PIN authentication for elevated roles  
  * PIN entry is required only when switching to an elevated (Caregiver) role.
* **FR-SU-03**: Session management  
  * Session updates securely on role switch; auto-logout on timeout.
* **FR-SU-04**: Role-based UI and navigation update  
  * UI and navigation update instantly to reflect the selected role.
* **FR-SU-05**: Secure storage of session and PIN  
  * PINs and session data are stored securely and encrypted.
* **FR-SU-06**: Accessibility support  
  * All dialogs and flows support TalkBack and meet 4.5:1 color contrast.
* **FR-SU-07**: PIN reset flow  
  * Users can reset their PIN via a secure, accessible dialog.
* **FR-SU-08**: Audit log for role switches  
  * All role switches are recorded for security review.
* **FR-SU-09**: Error handling and feedback  
  * Clear, child-appropriate error messages for incorrect PIN, session timeout, and other failures.

### 4.2 Non-functional requirements (NFR)
* **NFR-SU-01**: Responsive UI for phones/tablets
* **NFR-SU-02**: Secure PIN and session storage (encryption)
* **NFR-SU-03**: Error messages in child-appropriate language
* **NFR-SU-04**: Android 7.0+ (API Level 24) minimum support
* **NFR-SU-05**: Portrait and landscape orientation support
* **NFR-SU-06**: Material Design 3 compliance with theme system
* **NFR-SU-07**: Clean architecture with separation of concerns
* **NFR-SU-08**: Consistent navigation patterns across roles
* **NFR-SU-09**: Dialog open/close within 300ms for perceived instant response
* **NFR-SU-10**: Accessibility compliance verified by automated and manual testing
* **NFR-SU-11**: Audit log retention for minimum 90 days, with privacy controls
* **NFR-SU-12**: Efficient battery and resource usage for all Switch User flows

## 5. User experience

### 5.1 Entry points & first-time user flow
* Switch User button is always visible in the top navigation bar.
* First-time users are prompted to set a PIN for secure role switching.

### 5.2 Core experience
* **Role Switching**: User taps Switch User button, selects any available profile, enters PIN if required for elevated roles, and the app updates to the selected role.
  * Ensures secure, intuitive, and fast role switching for all users, with PIN required only for elevated roles.
* **Feedback**: User receives immediate confirmation for successful switches and clear error messages for failures.
* **Accessibility**: Focus is set to PIN entry field; screen reader announces dialog state and errors.

### 5.3 Advanced features & edge cases
* PIN reset flow for forgotten PIN
* Session timeout and auto-logout
* Error handling for incorrect PIN
* Accessibility for all dialogs

### 5.4 UI/UX highlights
* Theme-aware dialog components
* Semantic icon mapping for roles
* Explicit content descriptions for all interactive elements
* No hardcoded strings (use resources)
* 4.5:1 color contrast minimum
* Animated transitions for dialog open/close and role switch
* Minimum tap target size of 48x48dp for all buttons
* Visual feedback (e.g., loading spinner, error highlight) for all actions
* PIN entry field masks input and displays error state visually

## 6. Narrative

Families using Arthur's Life App on shared devices can securely and easily switch between any available profiles using the Switch User button in the top navigation bar. PIN protection ensures only authorized users access elevated roles, while the UI instantly updates to reflect the selected profile. Accessibility and child safety are prioritized throughout the experience, and user switching is unrestricted except for PIN protection for elevated roles.

## 7. Success metrics

### 7.1 User-centric metrics
* Percentage of successful role switches
* Number of PIN reset requests
* Session timeout events

### 7.2 Business metrics
* Support tickets related to role switching
* Retention rate for multi-role families

### 7.3 Technical metrics
* Zero Detekt violations
* 80%+ test coverage for domain logic
* No blocking operations on main thread
* Audit log completeness

## 8. Technical considerations

### 8.1 Integration points
* Domain: User aggregate, role, session, PIN value objects
* Infrastructure: Secure PIN storage, session repository
* Presentation: Theme-aware Compose dialogs, user selector dialog, child selector dialog, settings dialog, top bar

### 8.2 Data storage & privacy
* Secure local storage (encrypted PIN/session)
* Audit log for role switches
* COPPA-compliant for child data

### 8.3 Scalability & performance
* Efficient session management
* Minimal main thread operations
* Lazy loading for role data

### 8.4 Potential challenges
* PIN security and reset
* Accessibility for dialogs
* Session timeout handling
* Role-based UI complexity

## 9. User stories

### 9.8. Access User Selector Dialog
* **ID**: SU-08
* **Description**: As a user, I want to open the user selector dialog from the settings dialog or Switch User button, view all users with avatars and names, select a user, or close the dialog without switching.
* **Acceptance criteria**:
  * Given I am in the settings dialog (opened from the top bar) or tap the Switch User button, When I select "switch user", Then the user selector dialog opens with a list of users (avatar, name), a select button, and a close button.
  * Given the user selector dialog is open, When I tap a user, Then my session updates and the UI reflects the new user.
  * Given the user selector dialog is open, When I tap the close button, Then the dialog closes without switching users.
  * Given any theme is selected, When I view the user selector dialog, Then its style and icons match the selected theme.

### 9.9. Access Child Selector Dialog (Caregiver)
* **ID**: SU-09
* **Description**: As a caregiver, I want to open the child selector dialog from the top bar, view all children with avatars and names, select a child, or close the dialog without switching.
* **Acceptance criteria**:
  * Given I am in caregiver mode and tap the selected child in the top bar, When the child selector dialog opens, Then I see a list of children (avatar, name), a select button, and a close button.
  * Given the child selector dialog is open, When I tap a child, Then the UI updates to reflect the selected child.
  * Given the child selector dialog is open, When I tap the close button, Then the dialog closes without switching children.
  * Given any theme is selected, When I view the child selector dialog, Then its style and icons match the selected theme.

### 9.1. Access Switch User Dialog
* **ID**: SU-01
* **Description**: As a user, I want to access the Switch User dialog from the top navigation bar.
* **Acceptance criteria**:
  * Given the app is open, When I tap the Switch User button in the top navigation bar, Then the Switch User dialog is displayed.

### 9.2. Switch role with PIN
* **ID**: SU-02
* **Description**: As a user, I want to switch to any profile, with PIN required only for elevated roles.
* **Acceptance criteria**:
  * Given the Switch User dialog is open, When I select a non-elevated role, Then my session updates and the UI reflects the new role without requiring a PIN.
  * Given the Switch User dialog is open, When I select an elevated role and enter the correct PIN, Then my session updates and the UI reflects the new role.
  * Given the Switch User dialog is open, When I enter an incorrect PIN for an elevated role, Then an error message is displayed and the role is not switched.

### 9.3. Session management on role switch
* **ID**: SU-03
* **Description**: As a user, I want my session to be managed securely when switching roles.
* **Acceptance criteria**:
  * Given my role is switched, When the session times out, Then I am automatically logged out.
  * Given my role is switched, When I log out manually, Then my session data is securely cleared.

### 9.4. PIN reset flow
* **ID**: SU-04
* **Description**: As a user, I want to reset my PIN if forgotten.
* **Acceptance criteria**:
  * Given I have forgotten my PIN, When I access the PIN reset dialog and complete verification, Then I can set a new PIN and use it for future role switches.

### 9.5. Audit log for role switches
* **ID**: SU-05
* **Description**: As a caregiver, I want to see an audit log of role switches for security.
* **Acceptance criteria**:
  * Given I am a caregiver, When I access the audit log, Then I see a list of all role switches with timestamps and user details.

### 9.6. Error handling and feedback
* **ID**: SU-06
* **Description**: As a user, I want to receive clear feedback when a role switch fails.
* **Acceptance criteria**:
  * Given I attempt a role switch, When the switch fails due to incorrect PIN or session error, Then I see a clear, child-appropriate error message.

### 9.7. Accessibility for Switch User dialog
* **ID**: SU-07
* **Description**: As a user with accessibility needs, I want the Switch User dialog to be fully accessible.
* **Acceptance criteria**:
  * Given I use a screen reader, When the Switch User dialog opens, Then all elements are announced and navigable.
  * Given I use keyboard navigation, When the dialog is open, Then I can access all fields and buttons.
