package com.saucelabs.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ExtentReportManager
 * -------------------
 * Singleton that owns the single {@link ExtentReports} instance for the run.
 *
 * Each test thread gets its own {@link ExtentTest} via a ThreadLocal, so
 * parallel execution does not mix log entries across tests.
 *
 * Lifecycle (called from the TestNG listener):
 *   1. ExtentReportManager.init()          ← once before the suite
 *   2. ExtentReportManager.createTest(name) ← once per test method
 *   3. ExtentReportManager.getTest()        ← inside test / screen objects to log steps
 *   4. ExtentReportManager.flush()          ← once after the suite
 */
public final class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    // ── Init ──────────────────────────────────────────────────────────────

    public static void init(String platform) {
        String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String reportPath = Paths.get("reports",
            "ExtentReport_" + platform + "_" + timestamp + ".html")
            .toAbsolutePath().toString();

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("Mobile Automation Report");
        spark.config().setReportName("Sauce Labs – " + platform.toUpperCase());
        spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Platform",     platform.toUpperCase());
        extent.setSystemInfo("Appium",       "2.x");
        extent.setSystemInfo("Java",         System.getProperty("java.version"));
        extent.setSystemInfo("Executed by",  System.getProperty("user.name"));
    }

    // ── Per-test ──────────────────────────────────────────────────────────

    public static void createTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        TEST.set(test);
    }

    public static ExtentTest getTest() {
        return TEST.get();
    }

    public static void removeTest() {
        TEST.remove();
    }

    // ── Flush ─────────────────────────────────────────────────────────────

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }

    private ExtentReportManager() {}
}
