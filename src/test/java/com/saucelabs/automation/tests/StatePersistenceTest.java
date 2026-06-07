package com.saucelabs.automation.tests;

import com.saucelabs.automation.driver.DriverManager;
import com.saucelabs.automation.helpers.TestData;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

// First test tc_state_01_cartPersistsAfterRestart  will fail because app does not maintain the login state after app restart. 
// It means user need to login again if he re launches the app.

public class StatePersistenceTest extends BaseTest {

    @BeforeMethod(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndAddToCart() {
        step("Login and add one item to cart");
        loginScreen.openLoginScreen();
        loginScreen.waitForScreen();
        loginScreen.login(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);
        catalogScreen.waitForScreen();
        catalogScreen.openFirstProduct();
        productDetailScreen.waitForScreen();
        productDetailScreen.addToCart();
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC_STATE_01: Cart persists after app restart
    // ─────────────────────────────────────────────────────────────────────
    @Test(description = "Cart items should persist after a full app restart")
    public void tc_state_01_cartPersistsAfterRestart() {

        int badgeBefore = cartScreen.getBadgeCount();
        step("Cart badge before restart: " + badgeBefore);
        Assert.assertTrue(badgeBefore >= 1, "Pre-condition: cart should have ≥1 item");

        step("Restart the app (terminate + relaunch)");
        loginScreen.resetApp();

        step("Wait for login screen or catalog after restart");
        try {
            catalogScreen.waitForScreen();
            step("Auto-logged in after restart");
        } catch (Exception e) {
            step("Login screen shown after restart – re-logging in");
            loginScreen.waitForScreen();
            loginScreen.login(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);
            catalogScreen.waitForScreen();
        }

        step("Open cart and verify item count is preserved");
        cartScreen.openCart();
        int badgeAfter = cartScreen.getBadgeCount();
        step("Cart badge after restart: " + badgeAfter);
        Assert.assertEquals(
            badgeAfter, badgeBefore,
            "Cart item count should be the same after app restart"
        );
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC_STATE_02: Session cleared after full reset
    // ─────────────────────────────────────────────────────────────────────
    @Test(description = "User should be logged out after a full app data reset")
    public void tc_state_02_sessionClearedAfterFullReset() {

        step("Verify user is currently on catalog (logged in)");
        catalogScreen.waitForScreen();

        String appId = platform.name().equalsIgnoreCase("android")
            ? "com.saucelabs.mydemoapp.android"
            : "com.saucelabs.mydemoapp.ios";

        step("Terminate the app");
        DriverManager.getDriver().executeScript("mobile: terminateApp",
            Map.of("appId", appId));

        step("Re-launch the app");
        DriverManager.getDriver().executeScript("mobile: activateApp",
            Map.of("appId", appId));

        step("Verify login screen is shown (session was not retained)");
        loginScreen.waitForHomeScreen();
        Assert.assertTrue(
            loginScreen.verifyUserLoggedOutStateAfterAppReset(),
            "Login link should be displayed after a full reset – user should be logged out"
        );
    }
}
