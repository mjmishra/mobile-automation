package com.saucelabs.automation.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class ConfigManager {

    // ── Appium server ──────────────────────────────────────────────────────
    public static final String  APPIUM_HOST = get("APPIUM_HOST", "localhost");
    public static final int     APPIUM_PORT = getInt("APPIUM_PORT", 4723);
    public static final String  APPIUM_URL  =
        "http://" + APPIUM_HOST + ":" + APPIUM_PORT;

    // ── Timeouts (seconds) ────────────────────────────────────────────────
    public static final int EXPLICIT_TIMEOUT = getInt("EXPLICIT_TIMEOUT", 15);
    public static final int COMMAND_TIMEOUT  = getInt("COMMAND_TIMEOUT",  240);

    // ── App paths ─────────────────────────────────────────────────────────
    private static final Path APPS_DIR =
        Paths.get(System.getProperty("user.dir"), "apps");

    public static final String ANDROID_APP_PATH =
        get("ANDROID_APP_PATH", APPS_DIR.resolve("MyDemoApp.apk").toString());

    public static final String IOS_APP_PATH =
        get("IOS_APP_PATH", APPS_DIR.resolve("MyDemoApp.app.zip").toString());

    // ── Android device ────────────────────────────────────────────────────
    public static final String ANDROID_DEVICE_NAME =
        get("ANDROID_DEVICE", "Galaxy");
    public static final String ANDROID_VERSION =
        get("ANDROID_VERSION", "14");

    // ── iOS device ────────────────────────────────────────────────────────
    public static final String IOS_DEVICE_NAME =
        get("IOS_DEVICE", "iPhone 15");
    public static final String IOS_VERSION =
        get("IOS_VERSION", "17.2");

    // ── Helpers ───────────────────────────────────────────────────────────
    public static String get(String key, String defaultValue) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isBlank()) return sysProp.trim();
        String envVar = System.getenv(key);
        if (envVar != null && !envVar.isBlank()) return envVar.trim();
        return defaultValue;
    }

    private static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private ConfigManager() {}
}
