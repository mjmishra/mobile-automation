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
