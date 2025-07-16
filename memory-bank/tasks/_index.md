# Tasks Index

## In Progress
- [TASK007] Remove Profile Tab from Navigation - Fixing PRD violations for 4-tab maximum

## Pending

### Bottom Navigation Bar Feature
- [TASK001] Define Navigation Routes and Items - Foundational setup for bottom navigation
- [TASK002] Create Role-Based Navigation Logic - ViewModel for bottom navigation
- [TASK003] Implement the Bottom Navigation Bar Composable - UI for bottom navigation
- [TASK004] Integrate Bottom Bar into Main App UI - Connecting bottom navigation to the app
- [TASK005] Update Main Navigation Host - Wiring up the navigation graph
- [TASK008] Add Admin Status to User Model - Support Users vs Children tab differentiation
- [TASK009] Update Auth Preferences for Admin Status - Track admin status in preferences
- [TASK010] Implement Admin-Based Tab Logic - Admin-aware tab selection logic
- [TASK011] Fix Tab Labels to Match PRD - Correct Dashboard label and others
- [TASK012] Update Tests for 4-Tab Structure - Test updates for PRD compliance

### Theme System Feature
- [TASK013] Define Theme Data Layer - DataStore and Repository for themes
- [TASK014] Provide Theme Dependencies - Hilt modules for theme system
- [TASK015] Create Theme State Management - ViewModel for theme system
- [TASK016] Implement Theme Selector Dialog - UI for changing themes
- [TASK017] Create a Top-Level Theme Provider - Applying themes to the entire app

### Top Navigation Bar Feature
- [TASK018] Define Top Bar State - State management for the top navigation bar
- [TASK019] Implement the Top Navigation Bar Composable - UI for the top bar
- [TASK020] Implement Required Dialogs - Creating dialogs triggered from the top bar
- [TASK021] Create a Dialog Management System - Centralized handling of dialogs
- [TASK022] Integrate Top Bar into Main App UI - Adding the top bar to the main screen
- [TASK023] Integrate Dialog into Settings - Connecting theme selection to settings
- [TASK024] Implement Unit and UI Tests for Top Bar - Testing for top bar functionality

### Final Tasks
- [TASK025] Implement Unit and UI Tests for Theme System - Testing for theme functionality
- [TASK026] Verify PRD Compliance - Final verification of all PRD requirements

## Completed
- [TASK006] Implement Unit and UI Tests - Testing for bottom navigation (completed with fixes)

## Abandoned
(No tasks abandoned yet)

---

## Task Reordering Summary

Tasks have been reordered to group related features together:

### ID Mapping (Old → New)
**Bottom Navigation Bar:**
- TASK020 → TASK007 (Remove Profile Tab)
- TASK021 → TASK008 (Add Admin Status)
- TASK022 → TASK009 (Update Auth Preferences)
- TASK023 → TASK010 (Admin-Based Tab Logic)
- TASK024 → TASK011 (Fix Tab Labels)
- TASK025 → TASK012 (Update Tests)

**Theme System:**
- TASK007 → TASK013 (Define Theme Data Layer)
- TASK008 → TASK014 (Provide Dependencies)
- TASK010 → TASK015 (Theme State Management)
- TASK011 → TASK016 (Theme Selector Dialog)
- TASK012 → TASK017 (Top-Level Provider)

**Top Navigation Bar:**
- TASK009 → TASK018 (Define Top Bar State)
- TASK015 → TASK019 (Top Bar Composable)
- TASK016 → TASK020 (Required Dialogs)
- TASK017 → TASK021 (Dialog Management)
- TASK018 → TASK022 (Integrate Top Bar)
- TASK013 → TASK023 (Integrate Dialog into Settings)
- TASK019 → TASK024 (Top Bar Tests)

**Final:**
- TASK014 → TASK025 (Theme System Tests)
- TASK026 → TASK026 (PRD Compliance - no change)

---

**Last Updated:** 2025-07-16