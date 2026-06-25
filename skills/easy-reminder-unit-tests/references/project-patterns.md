# Easy Reminder JVM test patterns

## Test surface

Use `app/src/test/java` only. Mirror the production package and name files `<ProductionType>Test.kt`.

The project already uses:

- JUnit 4 assertions and `@Test`
- `kotlinx-coroutines-test`
- hand-written repository and DAO fakes
- fixed epoch-millisecond timestamps
- explicit locale and time-zone configuration

Do not introduce MockK, Mockito, Robolectric, Turbine, parameterized-test libraries, or new Gradle dependencies. Use existing libraries and small fakes.

## Public seams by area

### Helpers and validators

Call public formatting, conversion, or validation methods with branch-focused inputs. Private helpers are covered only as consequences of public calls.

For `ReminderUpsertValidator.validate`, independently cover:

- blank title
- null date after a nonblank title
- null hour after title/date are valid
- null minute after title/date/hour are valid
- past, exactly-now, and future timestamps
- title lengths 50 and 51
- description lengths 200 and 201
- fully valid input

Use the existing protected clock seam only because production already exposes it. Do not add another seam.

For formatting code, include exact threshold values and values immediately around them. A single test that enters a broad `when` arm may leave rounding and pluralization branches uncovered.

### Repository

Create a recording `ReminderDao` in the test file. Verify public repository methods:

- delegate to the expected DAO method
- return the DAO result or flow unchanged
- construct the expected `Reminder`
- map a null ID to the entity default ID
- preserve an existing ID
- forward every field exactly

Prefer a configurable fake with captured arguments over a fake that recreates database behavior.

### ViewModels

Use a configurable fake `ReminderRepository`, `SavedStateHandle`, and `kotlinx-coroutines-test`.

Typical observable branches include:

- missing versus present navigation ID
- repository entity found versus absent
- accepted versus rejected title/description boundary updates
- validation failure versus successful save
- delete in create mode versus update mode
- closest reminder absent versus present
- flow loading, success, and exception states
- snackbar set and cleared

Assert public `StateFlow` values and fake call records. Advance pending coroutine work before assertions. Never access private flows or methods.

Calls that directly require Android framework behavior, real `Context`, `AlarmManager`, notification services, or Compose rendering may not be suitable for this unit-only skill. Cover logic reachable without a device and report the rest instead of changing production code or adding Robolectric.

## Branch-coverage tactics

JaCoCo branch counters are attached to source lines. For a line with multiple boolean operands, statement execution is insufficient.

Example:

```kotlin
return title.isBlank() || date == null || hour == null || minute == null
```

Use four tests that make each operand the first true condition, plus one all-false test. Earlier true operands short-circuit later checks, so a generic invalid-input test cannot cover the whole line.

For length checks, use exact boundaries:

```text
49, 50, 51
199, 200, 201
```

The nearest values around the threshold are usually enough; add farther values only when behavior differs.

For ordered conditions, ensure an input intended for a later condition passes every earlier condition.

## Deterministic global state

Locale and time zone are process-wide. Save and restore them:

```kotlin
private lateinit var originalLocale: Locale
private lateinit var originalTimeZone: TimeZone

@Before
fun setUp() {
    originalLocale = Locale.getDefault()
    originalTimeZone = TimeZone.getDefault()
    Locale.setDefault(Locale.US)
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
}

@After
fun tearDown() {
    Locale.setDefault(originalLocale)
    TimeZone.setDefault(originalTimeZone)
}
```

For ViewModels, install a test main dispatcher and reset it in teardown. Keep the rule in test source; do not modify production code.

## Coverage interpretation

Run `jacocoTestReport` for unit coverage. Its XML report is normally:

`app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

The helper script reports:

- lines with missed JaCoCo branches
- fully missed executable lines without branch counters
- aggregate branch and line percentages per source file

Prioritize public business logic. Kotlin may produce synthetic branches for null checks, coroutines, default arguments, Compose, or generated code. Do not violate the test boundaries to chase such branches. Document any remaining gaps precisely.

