package com.saucelabs.automation.config;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;

import java.nio.file.Paths;

import static com.saucelabs.automation.config.ConfigManager.*;

public final class CapabilityFactory {

    public static Capabilities build(Platform platform) {
        return switch (platform) {
            case ANDROID -> buildAndroid();
            case IOS     -> buildIOS();
        };
    }

    // ── Android ───────────────────────────────────────────────────────────
    private static Capabilities buildAndroid() {
        MutableCapabilities caps = new MutableCapabilities();

        caps.setCapability("platformName",                "Android");
        caps.setCapability("appium:automationName",       "UiAutomator2");
        caps.setCapability("appium:deviceName",           ANDROID_DEVICE_NAME);
        caps.setCapability("appium:platformVersion",      ANDROID_VERSION);
        caps.setCapability("appium:app",                  Paths.get(ANDROID_APP_PATH).toAbsolutePath().toString());
        caps.setCapability("appium:appPackage",           "com.saucelabs.mydemoapp.android");
        caps.setCapability("appium:appActivity",          "com.saucelabs.mydemoapp.android.view.activities.SplashActivity");
        caps.setCapability("appium:autoGrantPermissions", true);
        caps.setCapability("appium:newCommandTimeout",    COMMAND_TIMEOUT);
        caps.setCapability("appium:noReset",              false);
        caps.setCapability("appium:fullReset",            false);

        return caps;
    }

    // ── iOS ───────────────────────────────────────────────────────────────
    private static Capabilities buildIOS() {
        MutableCapabilities caps = new MutableCapabilities();

        caps.setCapability("platformName",                   "iOS");
        caps.setCapability("appium:automationName",          "XCUITest");
        caps.setCapability("appium:deviceName",              IOS_DEVICE_NAME);
        caps.setCapability("appium:platformVersion",         IOS_VERSION);
        caps.setCapability("appium:app",                     Paths.get(IOS_APP_PATH).toAbsolutePath().toString());
        caps.setCapability("appium:bundleId",                "com.saucelabs.mydemoapp.ios");
        caps.setCapability("appium:autoAcceptAlerts",        true);
        caps.setCapability("appium:newCommandTimeout",       COMMAND_TIMEOUT);
        caps.setCapability("appium:noReset",                 false);
        caps.setCapability("appium:fullReset",               false);
        caps.setCapability("appium:simulatorStartupTimeout", 180000);

        return caps;
    }

    private CapabilityFactory() {}
}
