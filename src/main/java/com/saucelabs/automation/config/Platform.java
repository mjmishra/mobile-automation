package com.saucelabs.automation.config;

/**
 * Supported mobile platforms.
 * Resolved at runtime from the system property {@code -Dplatform=android|ios}.
 */
public enum Platform {
    ANDROID, IOS;

    /**
     * Reads the {@code platform} system property and returns the matching enum.
     * Defaults to {@code ANDROID} when the property is absent or unrecognised.
     */
    public static Platform fromSystem() {
        String value = System.getProperty("platform", "android").trim().toUpperCase();
        try {
            return Platform.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Unsupported platform: '" + value + "'. Use 'android' or 'ios'."
            );
        }
    }
}
