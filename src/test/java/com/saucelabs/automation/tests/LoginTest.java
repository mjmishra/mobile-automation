package com.saucelabs.automation.tests;

import com.saucelabs.automation.helpers.TestData;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTest
 * ---------
 * Covers all login-related scenarios:
 *
 *   TC_LOGIN_01 – Valid credentials   → user reaches product catalog
 *   TC_LOGIN_02 – Invalid credentials → error message is shown
 *   TC_LOGIN_03 – Empty fields        → error message is shown
 */
public class LoginTest extends BaseTest {

    // ─────────────────────────────────────────────────────────────────────
    // TC_LOGIN_01: Successful login
    // ─────────────────────────────────────────────────────────────────────
    @Test(description = "Valid credentials should navigate to the product catalog")
    public void tc_login_01_validCredentials_shouldReachCatalog() {
    	
    	step("Open login screen from menu");
    	loginScreen.openLoginScreen();

        step("Wait for login screen");
        loginScreen.waitForScreen();

        step("Enter valid username: " + TestData.VALID_USERNAME);
        step("Enter valid password");
        loginScreen.login(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);

        step("Verify product catalog is displayed");
        catalogScreen.waitForScreen();
        // waitForScreen() will throw a TimeoutException if catalog never appears,
        // which TestNG converts to a test failure automatically.
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC_LOGIN_02: Invalid credentials
    // ─────────────────────────────────────────────────────────────────────
    @Test(description = "Invalid credentials should show an error message")
    public void tc_login_02_invalidCredentials_shouldShowError() {
    	
    	step("Open login screen from menu");
    	loginScreen.openLoginScreen();

        step("Wait for login screen");
        loginScreen.waitForScreen();

        step("Enter invalid username: " + TestData.INVALID_USERNAME);
        loginScreen.login(TestData.INVALID_USERNAME, TestData.INVALID_PASSWORD);

        step("Verify error message is displayed");
        Assert.assertTrue(
            loginScreen.isErrorDisplayed(),
            "Expected an error message to be shown for invalid credentials"
        );
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC_LOGIN_03: Empty credentials
    // ─────────────────────────────────────────────────────────────────────
    @Test(description = "Empty credentials should show an error message")
    public void tc_login_03_emptyCredentials_shouldShowError() {
    	
    	step("Open login screen from menu");
    	loginScreen.openLoginScreen();

        step("Wait for login screen");
        loginScreen.waitForScreen();

        step("Submit login form with empty username and password");
        loginScreen.login(TestData.EMPTY, TestData.EMPTY);

        step("Verify error message is displayed");
        Assert.assertTrue(
            loginScreen.isErrorDisplayed(),
            "Expected an error message when credentials are empty"
        );
    }
}
