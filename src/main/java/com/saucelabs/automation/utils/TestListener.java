package com.saucelabs.automation.utils;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.saucelabs.automation.driver.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestListener implements ITestListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestListener.class);

    public static final String SCREENSHOT_KEY = "failure_screenshot";

    @Override
    public void onStart(ITestContext context) {
        String platform = System.getProperty("platform", "android");
        LOG.info("Test suite starting – platform: {}", platform.toUpperCase());
        ExtentReportManager.init(platform);
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flush();
        LOG.info("Test suite finished. Report written.");
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getTestClass().getRealClass().getSimpleName()
            + " :: " + result.getMethod().getMethodName();
        ExtentReportManager.createTest(testName);
        LOG.info("▶ START  {}", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().log(Status.PASS, "Test PASSED ✅");
        }
        LOG.info("✅ PASS   {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("❌ FAIL   {} – {}",
            result.getMethod().getMethodName(),
            result.getThrowable() != null ? result.getThrowable().getMessage() : "unknown");

        if (ExtentReportManager.getTest() == null) {
            LOG.warn("ExtentTest is null for failed test – skipping report entry");
            return;
        }

        // Log exception stack trace
        ExtentReportManager.getTest().log(Status.FAIL,
            "<pre>" + result.getThrowable() + "</pre>");

        // Read screenshot stored by tearDown() before driver was quit
        Object screenshotAttr = result.getAttribute(SCREENSHOT_KEY);
        if (screenshotAttr instanceof String base64Screenshot) {
            try {
                ExtentReportManager.getTest().fail(
                    "Failure Screenshot",
                    MediaEntityBuilder
                        .createScreenCaptureFromBase64String(base64Screenshot)
                        .build()
                );
                LOG.info("Screenshot attached to report successfully.");
            } catch (Exception e) {
                LOG.warn("Could not attach screenshot to report: {}", e.getMessage());
            }
        } else {
            LOG.warn("No screenshot found – ensure captureScreenshot() is called in tearDown()");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().log(Status.SKIP,
                "Test SKIPPED" +
                    (result.getThrowable() != null ? ": " + result.getThrowable() : ""));
        }
        LOG.warn("⏭ SKIP   {}", result.getMethod().getMethodName());
    }

    /**
     * Called from BaseTest.tearDown() BEFORE driver.quit().
     * Captures screenshot and stores it in ITestResult attributes
     * so onTestFailure() can retrieve it after the driver is closed.
     */
    public static void captureScreenshot(ITestResult result) {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver instanceof TakesScreenshot ts) {
                String base64 = ts.getScreenshotAs(OutputType.BASE64);
                result.setAttribute(SCREENSHOT_KEY, base64);
                LOG.info("Screenshot captured for: {}",
                    result.getMethod().getMethodName());
            }
        } catch (Exception e) {
            LOG.warn("Could not capture screenshot in tearDown: {}", e.getMessage());
        }
    }
}