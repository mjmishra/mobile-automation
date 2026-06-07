package com.saucelabs.automation.tests;

import com.saucelabs.automation.config.Platform;
import com.saucelabs.automation.driver.DriverManager;
import com.saucelabs.automation.screens.*;
import com.saucelabs.automation.utils.ExtentReportManager;
import com.saucelabs.automation.utils.TestListener;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public abstract class BaseTest {

    protected Platform platform;

    protected LoginScreen          loginScreen;
    protected ProductCatalogScreen catalogScreen;
    protected ProductDetailScreen  productDetailScreen;
    protected CartScreen           cartScreen;
    protected CheckoutScreen       checkoutScreen;

    @BeforeSuite(alwaysRun = true)
    public void resolvePlatform() {
        platform = resolvePlatformSafely();
        System.out.println("[BaseTest] Platform in @BeforeSuite: " + platform);
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        platform = resolvePlatformSafely();
        System.out.println("[BaseTest] Platform in @BeforeMethod: " + platform);
        DriverManager.initDriver(platform);
        initScreens();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        // Step 1 — Screenshot FIRST, driver still alive
        if (result.getStatus() == ITestResult.FAILURE) {
            TestListener.captureScreenshot(result);
        }

        // Step 2 — Quit driver
        DriverManager.quitDriver();

        // Step 3 — Clean up report thread local
        ExtentReportManager.removeTest();
    }

    private Platform resolvePlatformSafely() {
        String sysProp = System.getProperty("platform");
        System.out.println("[BaseTest] system property platform = " + sysProp);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            try {
                return Platform.valueOf(sysProp.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("[BaseTest] WARNING unknown platform: " + sysProp);
            }
        }
        String envVar = System.getenv("PLATFORM");
        System.out.println("[BaseTest] env variable PLATFORM = " + envVar);
        if (envVar != null && !envVar.trim().isEmpty()) {
            try {
                return Platform.valueOf(envVar.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("[BaseTest] WARNING unknown env: " + envVar);
            }
        }
        System.out.println("[BaseTest] defaulting to ANDROID");
        return Platform.ANDROID;
    }

    private void initScreens() {
        loginScreen         = new LoginScreen();
        catalogScreen       = new ProductCatalogScreen();
        productDetailScreen = new ProductDetailScreen();
        cartScreen          = new CartScreen();
        checkoutScreen      = new CheckoutScreen();
    }

    protected void step(String message) {
        System.out.println("[STEP] " + message);
        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().info(message);
        }
    }
}