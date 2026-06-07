package com.saucelabs.automation.screens;

import com.saucelabs.automation.config.ConfigManager;
import com.saucelabs.automation.config.Platform;
import com.saucelabs.automation.driver.DriverManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    // ── Driver / platform ─────────────────────────────────────────────────

    protected AppiumDriver driver() {
        return DriverManager.getDriver();
    }

    protected boolean isAndroid() {
        return driver() instanceof AndroidDriver;
    }

    protected boolean isIOS() {
        return driver() instanceof IOSDriver;
    }

    protected Platform platform() {
        return isAndroid() ? Platform.ANDROID : Platform.IOS;
    }

    // ── Selector resolution ───────────────────────────────────────────────

    protected By sel(By androidLocator, By iosLocator) {
        return isAndroid() ? androidLocator : iosLocator;
    }

    /** Convenience: both platforms share the same accessibility id. */
    protected By byAccessibility(String id) {
        return AppiumBy.accessibilityId(id);
    }

    /** Android UiAutomator2 selector. */
    protected By byAndroid(String uiAutomatorExpression) {
        return AppiumBy.androidUIAutomator(uiAutomatorExpression);
    }

    /** iOS NSPredicate selector. */
    protected By byIosPredicate(String predicate) {
        return AppiumBy.iOSNsPredicateString(predicate);
    }

    /** iOS Class Chain selector. */
    protected By byIosClassChain(String chain) {
        return AppiumBy.iOSClassChain(chain);
    }

    // ── Explicit waits ────────────────────────────────────────────────────

    protected WebDriverWait getWait() {
        return new WebDriverWait(driver(),
            Duration.ofSeconds(ConfigManager.EXPLICIT_TIMEOUT));
    }

    protected WebDriverWait getWait(int seconds) {
        return new WebDriverWait(driver(), Duration.ofSeconds(seconds));
    }

    /**
     * Waits for an element to be visible and returns it.
     */
    protected WebElement waitForVisible(By locator) {
        log.debug("Waiting for visible: {}", locator);
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForVisible(By locator, int timeoutSeconds) {
        return getWait(timeoutSeconds)
            .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits for an element to be clickable (visible + enabled).
     */
    protected WebElement waitForClickable(By locator) {
        return getWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits until an element is no longer visible.
     */
    protected void waitForAbsent(By locator) {
        getWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected void waitForAbsent(By locator, int timeoutSeconds) {
        getWait(timeoutSeconds)
            .until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Returns true if the element is currently displayed; no wait.
     */
    protected boolean isDisplayed(By locator) {
        try {
            return driver().findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    // ── Interactions ──────────────────────────────────────────────────────

    /**
     * Waits for clickable then taps.
     */
    protected void tap(By locator) {
        waitForClickable(locator).click();
    }

    /**
     * Clears the field and types text.
     * Hides the keyboard on iOS after typing to prevent obstruction.
     */
    protected void typeInto(By locator, String text) {
        WebElement el = waitForClickable(locator);
        el.clear();
        el.sendKeys(text);
        if (isIOS()) {
            try { driver().executeScript("mobile: hideKeyboard"); }
            catch (Exception ignored) { /* keyboard may already be hidden */ }
        }
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText();
    }

    // ── Scroll / Swipe ────────────────────────────────────────────────────

    /**
     * Scrolls down using a platform-appropriate gesture.
     *
     *  Android: W3C Actions (PointerInput swipe)
     *  iOS:     mobile: scroll execute script
     */
    protected void scrollDown() {
        Dimension size = driver().manage().window().getSize();
        int midX   = size.width  / 2;
        int startY = (int) (size.height * 0.75);
        int endY   = (int) (size.height * 0.25);

        if (isAndroid()) {
            swipe(midX, startY, midX, endY);
        } else {
            driver().executeScript("mobile: scroll", Map.of("direction", "down"));
        }
    }

    protected void scrollUp() {
        Dimension size = driver().manage().window().getSize();
        int midX   = size.width  / 2;
        int startY = (int) (size.height * 0.25);
        int endY   = (int) (size.height * 0.75);

        if (isAndroid()) {
            swipe(midX, startY, midX, endY);
        } else {
            driver().executeScript("mobile: scroll", Map.of("direction", "up"));
        }
    }

    /**
     * Scrolls until the element identified by {@code locator} is visible,
     * up to {@code maxScrolls} times.
     */
    protected WebElement scrollToElement(By locator, int maxScrolls) {
        for (int i = 0; i < maxScrolls; i++) {
            if (isDisplayed(locator)) return driver().findElement(locator);
            scrollDown();
        }
        throw new NoSuchElementException(
            "Element not found after " + maxScrolls + " scrolls: " + locator);
    }

    protected WebElement scrollToElement(By locator) {
        return scrollToElement(locator, 8);
    }

    /**
     * W3C Pointer Actions swipe – used for Android and custom gestures.
     */
    protected void swipe(int startX, int startY, int endX, int endY) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 0);
        swipe.addAction(finger.createPointerMove(Duration.ZERO,
            PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(600),
            PointerInput.Origin.viewport(), endX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver().perform(List.of(swipe));
    }

    // ── App lifecycle ─────────────────────────────────────────────────────

    /**
     * Terminates then re-launches the app – guarantees a clean state.
     */
    public void resetApp() {
        if (isAndroid()) {
            ((AndroidDriver) driver()).terminateApp("com.saucelabs.mydemoapp.android");
            ((AndroidDriver) driver()).activateApp("com.saucelabs.mydemoapp.android");
        } else {
            ((IOSDriver) driver()).terminateApp("com.saucelabs.mydemoapp.ios");
            ((IOSDriver) driver()).activateApp("com.saucelabs.mydemoapp.ios");
        }
    }

    /**
     * Sends the app to background for {@code seconds}, then restores it.
     * Uses the Appium {@code mobile: backgroundApp} execute script.
     */
    public void backgroundApp(int seconds) {
        driver().executeScript("mobile: backgroundApp",
            Map.of("seconds", seconds));
    }

    // ── Alert handling ────────────────────────────────────────────────────

    public void dismissAlertIfPresent() {
        try {
            driver().switchTo().alert().dismiss();
        } catch (NoAlertPresentException ignored) {}
    }

    // ── Native Android ADB helpers ────────────────────────────────────────

    /**
     * Toggles airplane mode via ADB shell (Android only; no-op on iOS).
     */
    public void setAirplaneMode(boolean enable) {
        if (!isAndroid()) return;
        String state = enable ? "1" : "0";
        driver().executeScript("mobile: shell",
            Map.of("command", "settings",
                   "args", List.of("put", "global", "airplane_mode_on", state)));
        driver().executeScript("mobile: shell",
            Map.of("command", "am",
                   "args", List.of("broadcast", "-a",
                       "android.intent.action.AIRPLANE_MODE",
                       "--ez", "state", String.valueOf(enable))));
        try { Thread.sleep(1500); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
