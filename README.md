# Task 1: Test Strategy
# Test Strategy

## 1. What Types of Tests Would You Automate for This App?

### Login and Authentication
- Valid login
- Invalid credentials
- Empty field submission
- Login state retained after app relaunch

### Product Catalog and Navigation
- Browsing the product list
- Opening a product detail page
- Scrolling through items
- Tapping the back button

### Cart Management
- Adding a product to the cart
- Removing products from the cart using the quantity stepper
- Verifying the badge count increments and decrements
- Confirming items persist in the cart after app relaunch

### End-to-End Checkout Flow
The full flow from login, adding a product through to the order confirmation screen
represents the highest business value in the app. This is the core user journey and
must be covered with automation.

### Error Handling
Error states such as invalid login, empty form submission, and network failure responses
should be automated because they are easy to overlook during manual testing.

### State Persistence
Verifying that cart contents survive an app restart and that session state behaves
correctly after backgrounding and restoring the app are edge cases that should be automated.

---

## 2. What Would You Intentionally Not Automate, and Why?

| Area | Reason |
|---|---|
| **Visual and Pixel-Level Accuracy** | Verifying fonts, colours, spacing, and layout pixel-correctness is not suited to functional automation tools like Appium. These checks are better handled by dedicated snapshot testing tools. |
| **Exploratory Testing** | It is always recommended to perform exploratory testing using stable, production-like data rather than outdated mock data before signing off on a release. This helps uncover defects that may have been missed due to limitations in mock data, interactions with other feature experiments, or edge-case scenarios not covered by automated or scripted testing. |
| **Biometric Authentication** | Face ID and fingerprint login cannot be reliably simulated on emulators or simulators. The workarounds are complex, platform-specific, and fragile. This is better covered through manual testing on real devices. |
| **Push Notifications and Deep Links** | These require OS-level configuration and external services to trigger reliably. The setup complexity adds significant maintenance overhead with limited test coverage value. |

---

## 3. How Would You Structure the Automation to Support Android, iOS, Multiple Devices, and Long-Term Maintainability?

### Screen Object Model
Every screen in the app is represented by a dedicated Java class that encapsulates all
locators and interactions for that screen. Tests never interact with elements directly —
they only call methods on screen objects. This means when the UI changes, only the screen
class needs updating, not every test that uses it.

### Single Codebase, Platform-Aware Locators
Android and iOS share the same test classes and screen objects. Platform differences are
handled inside each screen class using a `sel()` helper method that selects the correct
locator at runtime based on the active driver. Tests themselves contain no
platform-specific logic.

### Centralised Configuration
All environment-specific values such as device names, platform versions, app paths, and
timeouts are stored in a single `ConfigManager` class. These can be overridden at runtime
via system properties or environment variables without touching any code, making it
straightforward to run against different devices in CI.

### Capability Factory
Android and iOS capabilities are built in a dedicated `CapabilityFactory` class. Adding
a new platform or device profile means adding one new method in this class with no
changes required elsewhere.

### ThreadLocal Driver Management
The `DriverManager` class holds one driver instance per thread using `ThreadLocal`. This
makes the framework safe for parallel execution across multiple devices simultaneously
without sessions interfering with each other.

### Explicit Waits Only
All element interactions wait for the element to be visible and enabled before acting,
using Selenium's `WebDriverWait` and `ExpectedConditions`. There are no hard-coded
`Thread.sleep()` calls anywhere in the framework, which keeps the suite fast and
eliminates timing-related flakiness.

---

## 4. How Would You Integrate the Tests into CI/CD?

The framework integrates into CI/CD using GitHub Actions with two parallel jobs — one
for Android and one for iOS — triggered on every pull request and on a nightly schedule.

### Android Job
Runs on an `ubuntu-latest` runner. It boots an Android emulator using the
`reactivecircus/android-emulator-runner` action, starts the Appium server, downloads
the APK, and runs the suite with:
```bash
mvn test -Dplatform=android
```

### iOS Job
Runs on a `macos-14` runner, which is required for XCUITest. It boots an iOS Simulator
using `xcrun simctl`, starts the Appium server, and runs the suite with:
```bash
mvn test -Dplatform=ios
```

### Parallel Execution
Both jobs run in parallel to minimise total pipeline duration. On completion, the Extent
HTML report and log files are uploaded as build artifacts so test results are accessible
directly from the GitHub Actions run page.

### Device Matrix
The `strategy.matrix` feature in GitHub Actions can be used to run the same job against
multiple API levels or iOS versions simultaneously — for example Android 12 and
Android 13 in parallel.

### Release Pipelines
The same suite can be pointed at a Sauce Labs cloud device farm by replacing the Appium
server URL and capabilities with Sauce Labs credentials, enabling testing across hundreds
of real device and OS combinations without managing local infrastructure.

---

## 5. What Are the Biggest Risks in Mobile Automation for This App?

| Risk | Impact | Mitigation |
|---|---|---|
| **Flaky Element Detection** | Tests fail intermittently without any real application bug due to asynchronous rendering and variable screen transition speeds | Use explicit waits on every interaction — never use hard sleeps |
| **Locator Drift** | Tests break silently when developers rename components or restructure layouts between releases | Agree a naming convention with the development team and use accessibility IDs rather than XPath |
| **Emulator and Simulator Reliability** | Emulators can hang during boot or produce inconsistent timing behaviour compared to real devices | Use AVD snapshots for fast cold starts, set generous command timeouts, and run release sign-off on real physical devices |
| **App State Bleed Between Tests** | A half-completed checkout or leftover cart state causes the next test to fail for the wrong reason | Terminate and relaunch the app before each test method to guarantee a clean starting state |
| **Platform Divergence** | A feature may work correctly on Android and silently fail on iOS because the two builds are maintained in separate repositories | Run the full suite on both platforms on every pull request rather than treating one platform as the primary target |
| **CI Infrastructure Cost** | Long pipelines with emulator boot time discourage developers from running tests frequently | Keep the core suite lean and focused on critical paths — reserve the full device matrix for nightly runs only |


# Task 2: Automation Implementation
# Mobile Test Automation – Sauce Labs Sample App

**Stack:** Java 17 · Appium 2 · Selenium 4 · TestNG · ExtentReports  
**Platforms:** Android (UiAutomator2) · iOS (XCUITest) — single codebase  

---

## Project Structure

```
mobile-automation/
├── pom.xml                                        # Maven build + dependencies
├── apps/
│   ├── MyDemoApp.apk                              # Android build (add manually)
│   └── MyDemoApp.app.zip                          # iOS Simulator build (add manually)
├── src/
│   ├── main/java/com/saucelabs/automation/
│   │   ├── config/
│   │   │   ├── Platform.java                      # ANDROID / IOS enum
│   │   │   ├── ConfigManager.java                 # All config values + env overrides
│   │   │   └── CapabilityFactory.java             # Builds UiAutomator2 / XCUITest caps
│   │   ├── driver/
│   │   │   └── DriverManager.java                 # ThreadLocal driver lifecycle
│   │   ├── screens/
│   │   │   ├── BasePage.java                      # Waits, gestures, platform helpers
│   │   │   ├── LoginScreen.java
│   │   │   ├── ProductCatalogScreen.java
│   │   │   ├── ProductDetailScreen.java
│   │   │   ├── CartScreen.java
│   │   │   └── CheckoutScreen.java
│   │   └── utils/
│   │       ├── ExtentReportManager.java           # HTML report setup
│   │       └── TestListener.java                  # TestNG listener → report + screenshots
│   └── test/java/com/saucelabs/automation/
│       ├── helpers/
│       │   └── TestData.java                      # All test input data
│       └── tests/
│           ├── BaseTest.java                      # Driver init/quit + screen instantiation
│           ├── LoginTest.java                     # TC_LOGIN_01/02/03
│           ├── CheckoutTest.java                  # TC_CHECKOUT_01/02/03
│           └── StatePersistenceTest.java          # TC_STATE_01/02
│   └── test/resources/
│       ├── testng-android.xml                     # Android suite
│       ├── testng-ios.xml                         # iOS suite
│       ├── testng-all.xml                         # Both platforms (CI matrix)
│       └── logback.xml                            # Log config
└── .github/workflows/
    └── e2e.yml                                    # GitHub Actions CI pipeline
```

---

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Java | 17+ | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Node.js | 18+ | Required by Appium |
| Appium | 2.x | `npm install -g appium` |
| `uiautomator2` driver | latest | Android |
| `xcuitest` driver | latest | iOS / macOS only |
| Android SDK + `adb` | API 30+ | Set `ANDROID_HOME` |
| Xcode | 15+ | macOS only |

### Install Appium drivers (once)
```bash
appium driver install uiautomator2
appium driver install xcuitest
```

---

## Setup

```bash
# 1. Clone and enter the project
git clone <repo-url>
cd mobile-automation

# 2. Install Maven dependencies
mvn install -DskipTests

# 3. Add app builds
mkdir -p apps
# Android:
curl -L https://github.com/saucelabs/my-demo-app-android/releases/latest/download/mda-2.0.0-13.apk \
  -o apps/MyDemoApp.apk
# iOS Simulator:
curl -L https://github.com/saucelabs/my-demo-app-ios/releases/latest/download/MyRNDemoApp.app.zip \
  -o apps/MyDemoApp.app.zip

# 4. Start Appium server (separate terminal)
appium server --port 4723 --log-level info
```

---

## Running Tests

### Android
```bash
# Start emulator first
emulator -avd Pixel_6_API_33 &

mvn test -Dplatform=android
```

### iOS (macOS only)
```bash
# Boot simulator first
xcrun simctl boot "iPhone 15"

mvn test -Dplatform=ios
```

### Run a single test class
```bash
mvn test -Dplatform=android -Dtest=LoginTest
```

### Override device / version
```bash
mvn test -Dplatform=android \
         -DANDROID_DEVICE=emulator-5556 \
         -DANDROID_VERSION=12.0
```

---

## Configuration Reference

All values can be set as `-D` flags on the Maven command or as environment variables.

| Key | Default | Description |
|-----|---------|-------------|
| `platform` | `android` | Target platform (`android` / `ios`) |
| `APPIUM_HOST` | `localhost` | Appium server host |
| `APPIUM_PORT` | `4723` | Appium server port |
| `EXPLICIT_TIMEOUT` | `15` | WebDriverWait timeout in seconds |
| `ANDROID_DEVICE` | `emulator-5554` | Android device name / AVD |
| `ANDROID_VERSION` | `13.0` | Android platform version |
| `ANDROID_APP_PATH` | `apps/MyDemoApp.apk` | Path to APK |
| `IOS_DEVICE` | `iPhone 15` | iOS Simulator name |
| `IOS_VERSION` | `17.2` | iOS platform version |
| `IOS_APP_PATH` | `apps/MyDemoApp.app.zip` | Path to .app.zip |

---

## Test Cases

| ID | Class | Description |
|----|-------|-------------|
| TC_LOGIN_01 | LoginTest | Valid credentials → product catalog shown |
| TC_LOGIN_02 | LoginTest | Invalid credentials → error message shown |
| TC_LOGIN_03 | LoginTest | Empty credentials → error message shown |
| TC_CHECKOUT_01 | CheckoutTest | Full flow: browse → cart → checkout → confirmation |
| TC_CHECKOUT_02 | CheckoutTest | Cart badge increments after Add To Cart |
| TC_CHECKOUT_03 | CheckoutTest | Cart persists after app is backgrounded |
| TC_STATE_01 | StatePersistenceTest | Cart survives full app restart |
| TC_STATE_02 | StatePersistenceTest | Session cleared after full reset |

---

## Reports

After each run, an HTML report is written to `reports/ExtentReport_<platform>_<timestamp>.html`.  
Open it in any browser. Failed tests include an embedded screenshot.

---

## CI/CD

See `.github/workflows/e2e.yml`. The pipeline runs Android and iOS jobs in parallel:
- Android on `ubuntu-latest` using the `reactivecircus/android-emulator-runner` action
- iOS on `macos-14` (Apple Silicon) using `xcrun simctl`
- Reports are uploaded as build artifacts on every run
