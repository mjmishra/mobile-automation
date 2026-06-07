package com.saucelabs.automation.driver;

import com.saucelabs.automation.config.CapabilityFactory;
import com.saucelabs.automation.config.ConfigManager;
import com.saucelabs.automation.config.Platform;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.Capabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * DriverManager
 * -------------
 * Manages one {@link AppiumDriver} instance per thread (safe for parallel runs).
 *
 * <pre>
 *   DriverManager.initDriver(Platform.ANDROID);
 *   AppiumDriver driver = DriverManager.getDriver();
 *   DriverManager.quitDriver();
 * </pre>
 *
 * Platform routing:
 *   ANDROID  →  {@link AndroidDriver}
 *   IOS      →  {@link IOSDriver}
 *
 * Both extend {@link AppiumDriver} which extends Selenium's RemoteWebDriver,
 * so all standard Selenium APIs (findElement, WebDriverWait, etc.) work unchanged.
 */
public final class DriverManager {

    private static final Logger LOG = LoggerFactory.getLogger(DriverManager.class);

    /** One driver instance per thread – required for TestNG parallel execution. */
    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    // ── Initialisation ────────────────────────────────────────────────────

    /**
     * Creates and stores a new driver for the given platform.
     * Called once per test method from BaseTest.setUp().
     */
    public static void initDriver(Platform platform) {
        LOG.info("Initialising {} driver → {}", platform, ConfigManager.APPIUM_URL);

        Capabilities caps = CapabilityFactory.build(platform);
        URL serverUrl      = parseUrl(ConfigManager.APPIUM_URL);
        AppiumDriver driver;

        driver = switch (platform) {
            case ANDROID -> new AndroidDriver(serverUrl, caps);
            case IOS     -> new IOSDriver(serverUrl, caps);
        };

        DRIVER.set(driver);
        LOG.info("{} driver ready. Session ID: {}", platform, driver.getSessionId());
    }

    // ── Accessor ──────────────────────────────────────────────────────────

    /**
     * Returns the driver for the current thread.
     *
     * @throws IllegalStateException if initDriver() has not been called yet.
     */
    public static AppiumDriver getDriver() {
        AppiumDriver d = DRIVER.get();
        if (d == null) {
            throw new IllegalStateException(
                "Driver is not initialised. Call DriverManager.initDriver() first."
            );
        }
        return d;
    }

    // ── Teardown ──────────────────────────────────────────────────────────

    /**
     * Quits the driver and removes it from ThreadLocal.
     * Safe to call even when no driver is present.
     */
    public static void quitDriver() {
        AppiumDriver d = DRIVER.get();
        if (d != null) {
            try {
                d.quit();
                LOG.info("Driver session closed.");
            } catch (Exception e) {
                LOG.warn("Error while closing driver: {}", e.getMessage());
            } finally {
                DRIVER.remove();
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private static URL parseUrl(String raw) {
        try {
            return new URL(raw);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Appium URL: " + raw, e);
        }
    }

    private DriverManager() {}
}
