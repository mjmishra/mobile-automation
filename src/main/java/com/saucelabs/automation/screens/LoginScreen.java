package com.saucelabs.automation.screens;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.testng.Assert;

public class LoginScreen extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────
	
    private By hamburgerMenu() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/menuIV\")"),             
            AppiumBy.accessibilityId("Username")
        );
    }
    
    private By logInLink() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().text(\"Log In\")"),             
            AppiumBy.accessibilityId("Username")
        );
    }
    

    private By usernameField() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/nameET\")"),             
            AppiumBy.accessibilityId("Username")
        );
    }

    private By passwordField() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/passwordET\")"),
            AppiumBy.accessibilityId("Password")
        );
    }

    private By loginButton() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/loginBtn\")"),
            AppiumBy.accessibilityId("Login")
        );
    }


    private By errorMessage() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/usernameErrorIV\")"),
            AppiumBy.accessibilityId("Error message")
        );
    }

    // ── Actions ───────────────────────────────────────────────────────────
    
    public LoginScreen waitForHomeScreen() {
        waitForVisible(hamburgerMenu());
        log.info("HomeScreen is displayed.");
        return this;
    	
    }
    public boolean verifyUserLoggedOutStateAfterAppReset()
    {
        tap(hamburgerMenu());
        waitForVisible(logInLink());
        return isDisplayed(logInLink());	
    }
    
    public void openLoginScreen()
    {
    	waitForVisible(hamburgerMenu());
    	log.info("HomeScreen is displayed.");
    	tap(hamburgerMenu());
    	waitForVisible(logInLink());
    	tap(logInLink());	
    }

    public LoginScreen waitForScreen() {
        waitForVisible(usernameField());
        log.info("LoginScreen is displayed.");
        return this;
    }

    public void login(String username, String password) {
        log.info("Logging in as '{}'", username);
        typeInto(usernameField(), username);
        typeInto(passwordField(), password);
        tap(loginButton());
    }

    // ── Assertions ────────────────────────────────────────────────────────

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage());
    }

    public String getErrorText() {
        return getText(errorMessage());
    }
}
