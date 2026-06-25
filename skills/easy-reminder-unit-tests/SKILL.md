---
name: easy-reminder-unit-tests
description: Create, extend, or improve local JVM unit tests for the Easy Reminder Android project, especially when increasing JaCoCo or SonarCloud line and branch coverage. Use for tests under app/src/test, public-behavior testing of Kotlin helpers, validators, repositories, flows, and ViewModels, boundary-case design, fake collaborators, coroutine tests, and investigation of partially covered JaCoCo lines. Never use this skill to create instrumented/Compose tests, test private methods through reflection, or modify production code merely to make tests possible.
---

# Easy Reminder Unit Tests

Create focused JUnit 4 tests under `app/src/test` that exercise production behavior only through public APIs. Optimize for meaningful branch coverage while preserving production code exactly.

## Establish scope

1. Read the repository `AGENTS.md` instructions and `docs/TECHNICAL.md`.
2. Inspect the target production class, its existing unit tests, `app/build.gradle.kts`, and nearby test fakes.
3. Keep all test changes under `app/src/test`.
4. Do not add or edit instrumented tests under `app/src/androidTest`.
5. Do not edit `app/src/main`, manifests, resources, Gradle configuration, or dependencies to enable a unit test.
6. If code cannot be exercised as a local JVM test without a production or build change, report that limitation and cover the reachable public behavior instead.

Read [references/project-patterns.md](references/project-patterns.md) when selecting test seams, coroutine patterns, fakes, or branch cases.

## Preserve test boundaries

- Test private implementation indirectly through a public constructor, property, or method.
- Never use reflection, `setAccessible`, private-member lookup, generated accessors, or bytecode tricks.
- Never change private visibility, add a production-only test hook, extract production code, or modify constructor parameters for testability.
- Do not directly test a private function even when Kotlin/JVM reflection makes it technically reachable.
- Subclass only an existing non-final production type through an already exposed `open` or `protected` seam. Do not change production declarations to create one.
- Assert observable outcomes: return values, public state, emitted flow values, and calls recorded by a test fake.

## Design a branch matrix

Before writing tests, enumerate the decisions reachable from the public API:

- For `a || b || c`, make each operand independently decisive: `a=true`; `a=false,b=true`; `a=false,b=false,c=true`; and all false.
- For `a && b`, cover the left side false, the left side true/right side false, and both true.
- For ordered `if` or `when` chains, reach every arm plus the fall-through path.
- For nullable inputs, cover each null position independently when short-circuit branches differ.
- For thresholds, cover below, equal, and above values.
- For collections, cover empty, one item, multiple items, and relevant ordering or filtering cases.
- For success/failure flows, cover initial, success, empty, and exception states when publicly observable.
- For update/create logic, cover both identity paths and verify every forwarded field, including `notified = false` on saves.

Prefer one clear test per behavior or boundary. Parameterize only when failures remain easy to identify.

## Build deterministic tests

- Use JUnit 4 and the dependencies already present in the project.
- Use `runTest` only for suspend functions, flows, or coroutine-driven APIs.
- Use small in-test fakes that record inputs and expose configurable outputs. Implement every collaborator method reached by the scenario; unrelated methods may fail fast.
- Avoid time-sensitive values such as `System.currentTimeMillis()` when an exact timestamp communicates intent.
- Set locale and time zone explicitly for dependent tests and restore their prior values in `finally` or `@After`.
- Control `Dispatchers.Main` with a test dispatcher and reset it after ViewModel tests when needed.
- Advance the test scheduler before asserting work launched in `viewModelScope`.
- Do not sleep, poll, depend on test order, use the network, or schedule real alarms.
- Keep assertions behavioral. Do not duplicate production algorithms in expected-value calculations.

## Implement and iterate

1. Add or update the narrowest relevant `*Test.kt` file with `apply_patch`.
2. Run the targeted class first:

   ```powershell
   .\gradlew.bat testDebugUnitTest --tests "fully.qualified.TestClass"
   ```

3. Generate unit-test coverage:

   ```powershell
   .\gradlew.bat jacocoTestReport
   ```

4. Locate branch gaps:

   ```powershell
   python skills/easy-reminder-unit-tests/scripts/report_jacoco_gaps.py
   ```

   When using the installed personal skill, invoke the same script from that skill directory and pass the report path explicitly if needed.

5. Inspect each reported source line and add inputs that make every reachable branch outcome occur through the public API.
6. Re-run the targeted test and coverage report until the intended branches are covered or a branch is demonstrably compiler-generated/unreachable without violating this skill.
7. Run the required handoff checks:

   ```powershell
   .\gradlew.bat testDebugUnitTest assembleDebug
   ```

Do not run `connectedDebugAndroidTest` for a unit-test-only request.

## Review before handoff

- Confirm the diff contains no production, resource, manifest, Gradle, or instrumented-test changes.
- Confirm no reflection or private-member access appears in tests.
- Confirm public behavior, not implementation details, drives every test.
- Confirm short-circuit operands and exact boundaries are independently covered.
- Report targeted tests, full unit tests, assembly, and coverage commands actually run.
- Report remaining missed branches with their file and line, explaining whether they are reachable, Android-runtime-only, or Kotlin compiler artifacts.

