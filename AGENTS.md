# AGENTS.md

## Scope

These instructions apply to the entire repository.

Easy Reminder is a single-module Android application written in Kotlin. It uses Jetpack Compose, Material 3, MVVM, Hilt, Room, coroutines/Flow, exact alarms, and Android notifications.

Read `docs/TECHNICAL.md` before making architectural, persistence, scheduling, or test-infrastructure changes.

## Project map

- `app/src/main/java/rek/remindme/common`: date/time helpers, alarm scheduling, receiver, constants.
- `app/src/main/java/rek/remindme/data`: repository contract and implementation.
- `app/src/main/java/rek/remindme/data/local`: Room database, DAO, entity, and database DI.
- `app/src/main/java/rek/remindme/ui`: activity and Compose navigation.
- `app/src/main/java/rek/remindme/ui/reminder`: screens, ViewModels, validation, and UI state.
- `app/src/main/java/rek/remindme/ui/components`: reusable Compose UI.
- `app/src/test`: JVM unit tests.
- `app/src/androidTest`: Hilt and Compose instrumented tests.
- `app/schemas`: committed Room schema history.
- `gradle/libs.versions.toml`: dependency and plugin versions.
- `.github/workflows/build.yml`: CI, coverage, emulator, and SonarCloud workflow.

## Architecture invariants

- Keep UI state and business actions in ViewModels; Composables should render state and emit events.
- Access reminder data through `ReminderRepository`. Do not inject or call `ReminderDao` from UI code.
- Keep Room as the source of truth for the reminder list.
- Keep navigation arguments centralized in `Consts.Route`.
- After create, update, or delete operations, recalculate the closest future reminder and replace the single scheduled alarm.
- `AlarmReceiver` must always finish its asynchronous receiver work.
- Do not schedule real alarms from debug or instrumented-test manifests; both intentionally remove `AlarmReceiver`.
- Timestamps are epoch milliseconds interpreted using the device locale and time zone.
- Saving a reminder resets `notified` to `false`.

## Development environment

- Use JDK 21.
- Compile and target Android API 36; minimum API is 23.
- Use the checked-in Gradle wrapper.
- Keep machine-specific SDK/JDK paths out of commits. `local.properties` and local `org.gradle.java.home` values are developer-specific.
- Do not edit generated files under `build/` or `.gradle/`.

Common Windows commands:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest
.\gradlew.bat jacocoTestReport
```

Use the equivalent `./gradlew` commands on macOS/Linux.

Run the narrowest relevant tests while iterating, then run `testDebugUnitTest assembleDebug` before handoff. Run `connectedDebugAndroidTest` when changing navigation, Compose behavior, Hilt test wiring, localization-sensitive UI, or end-to-end reminder workflows.

## Kotlin and Compose conventions

- Follow the existing Kotlin official style and package structure.
- Prefer immutable public state exposed as `StateFlow`; keep mutable flows private.
- Collect flows in Composables with `collectAsStateWithLifecycle`.
- Pass callbacks and state into reusable Composables rather than resolving dependencies inside them.
- Keep user-visible text in string resources.
- Add previews for reusable or visually significant Composables when practical.
- Preserve accessibility content descriptions.
- Preserve existing `Consts.TestTag` values. Add new stable tags there when instrumented tests need selectors.
- Avoid selectors in tests that depend on layout position or translated Material internals when a stable semantic tag can be used.

## Persistence rules

- Keep production persistence behind `ReminderRepository`.
- Room entity changes require a database version increase, a migration or auto-migration, an updated schema JSON in `app/schemas`, and migration/query tests.
- Do not use destructive migration as a shortcut.
- Be aware that “clear notified reminders” currently deletes by past timestamp, while notification delivery uses the `notified` flag.
- SQL timestamps are compared to `strftime('%s','now') * 1000`; preserve millisecond units.

## Alarm and notification rules

- There is one exact alarm, represented by a broadcast `PendingIntent` with request code `0`.
- Preserve `FLAG_IMMUTABLE | FLAG_UPDATE_CURRENT` unless platform behavior is intentionally redesigned.
- Android 12+ exact-alarm capability and Android 13+ notification permission are separate concerns.
- Test scheduling changes on relevant API levels, including denied permissions.
- `AlarmReceiver` handles both `BOOT_COMPLETED` and `Consts.System.ALARM_RECEIVER_ID`.
- Do not perform long blocking work directly in `BroadcastReceiver.onReceive`.
- If changing boot behavior, explicitly test the case where future reminders exist but none are due.
- Notification IDs use reminder IDs, allowing reminders to create distinct notifications.

## Testing conventions

- Unit tests use JUnit 4 and `kotlinx-coroutines-test`.
- Instrumented UI tests use Compose test APIs, Hilt, `HiltTestRunner`, and `FakeReminderRepository`.
- The fake repository begins with one notified reminder; workflow tests depend on that initial state.
- Extend fake implementations when new code paths call currently unimplemented repository methods.
- Locale/time-zone-dependent tests must set deterministic values and restore them when cross-test leakage is possible.
- Keep English and French expectations aligned when changing localized UI text.
- For Room DAO behavior, prefer database-backed tests over hand-written DAO fakes when query ordering or SQL predicates matter.

## Resource and dependency changes

- Update both `values/strings.xml` and `values-fr/strings.xml` for user-facing strings.
- Manage dependency versions through `gradle/libs.versions.toml`.
- Keep Compose libraries aligned through the Compose BOM.
- Keep Room schema export enabled.
- Do not commit secrets, signing keys, Sonar tokens, SDK paths, or generated APKs.

## Change checklist

Before handoff:

1. Review the diff and preserve unrelated user changes.
2. Confirm architecture invariants still hold.
3. Add or update tests for behavior changes.
4. Run the relevant Gradle verification commands.
5. Update `docs/TECHNICAL.md` when architecture, workflows, versions, commands, permissions, or known constraints change.
6. Report any test that could not be run, including the concrete environmental reason.
