---
mode: agent
---

# Agent Compliance Protocol

**Follow every instruction below exactly. Be concise, clear, and actionable.**

> **MANDATORY:** All Gradle and project commands must be executed from the `android-kotlin` directory. Always `cd android-kotlin` before running any build, test, detekt, or formatting command.

## Core Rules
- Never skip a step or end your turn until the checklist is fully complete.
- Represent all planning and progress as a single markdown checklist in `arthurs-life-app/.github/tmp/tmp_progress.md`. Update and show this checklist after each step.
- Store all temporary and intermediate files in `arthurs-life-app/.github/tmp`.
- Always run Gradle commands from the `android-kotlin` directory.
- For non-Gradle commands: redirect output to a `tmp_` file in `arthurs-life-app/.github/tmp` and read from there.
- For Gradle commands (detekt, detektFormat, test, build): read directly from generated reports in `android-kotlin/app/build/`.
- Always read large file chunks (e.g., 2000 lines) for context.
- For detekt issues, always run `detektFormat` before manual fixes.

## Step-by-Step Protocol

1. **Goal Identification**
   - Clearly identify and understand the user's goal before taking action.

2. **Planning**
   - Develop a step-by-step plan as a markdown checklist. Save and update it in `tmp_progress.md` after each step.

3. **Sequential Execution**
   - Complete each checklist step in order. Never skip or reorder steps.

4. **Output File Workflow**
   - **For non-Gradle commands:** Redirect all command/process output to a `tmp_` file in `arthurs-life-app/.github/tmp` for anything that takes more than 2 seconds.
   - **For Gradle commands:** Read directly from generated reports and logs in `android-kotlin/app/build/`:
     - `detekt`: Read from `android-kotlin/app/build/reports/detekt/detekt.xml` or `detekt.html`
     - `test`: Read from `android-kotlin/app/build/reports/tests/` and `android-kotlin/app/build/test-results/`
     - `build`: Check terminal output and `android-kotlin/app/build/` for compilation errors
   - Only read output after the process is finished and files are fully written.

5. **Output Verification**
   - **For non-Gradle commands:** Scan the last 100–200 lines of the tmp output file for success/failure indicators.
   - **For Gradle commands:** Read from the appropriate build reports:
     - **detekt:** Check `android-kotlin/app/build/reports/detekt/detekt.xml` for `<error>` tags or issues count
     - **test:** Check `android-kotlin/app/build/test-results/` for test completion and pass/fail status
     - **build:** Look for `BUILD SUCCESSFUL`, `BUILD FAILED`, `FAILURE`, `error`, `Exception`, `FAIL` in terminal output
   - Look for clear success or failure indicators:
     - Success: `BUILD SUCCESSFUL`, `X tests completed`, `0 issues found`, etc.
     - Failure: `BUILD FAILED`, `FAILURE`, `error`, `Exception`, `FAIL`, test failures, detekt violations
   - If output is incomplete or only shows setup/configuration, wait and re-check, or ask the user to confirm completion.

6. **Verification and Iteration**
   - After each major step, verify completion by analyzing output files for success indicators. Reflect and iterate as needed.

7. **Plan Adjustment**
   - If the goal is not achieved, revise and update the checklist in `arthurs-life-app/.github/tmp/tmp_progress.md` and continue.

8. **Result Summarization**
   - Once the goal is achieved, create a concise summary and save it to `arthurs-life-app/.github/tmp/tmp_summary.md`.

9. **Detekt Issue Handling**
   - For detekt issues, always run `detektFormat` first. Only attempt manual fixes if issues remain.

10. **Recursive URL Fetching**
    - If a URL is provided, fetch its content. If more URLs are found, fetch them recursively until all context is gathered.

## Key Reminders
- Maintain a clear audit trail in `arthurs-life-app/.github/tmp` for all planning, progress, and results.
- Strictly comply with every instruction in this protocol.
- If the user says "resume", "continue", or "try again", continue from the last incomplete checklist step.
- Non-compliance is not permitted.