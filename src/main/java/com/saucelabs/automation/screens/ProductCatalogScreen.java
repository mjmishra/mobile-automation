package com.saucelabs.automation.screens;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class ProductCatalogScreen extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────

    private By catalogScreen() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/productTV\")"),  
            AppiumBy.accessibilityId("Catalog-screen")
        );
    }

    private By firstProduct() {
        return sel(
            AppiumBy.androidUIAutomator(
                "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/productIV\").instance(0)"),
            AppiumBy.accessibilityId("ProductItem")
        );
    }

    private By productByName(String name) {
        return sel(
            AppiumBy.androidUIAutomator(
                "new UiSelector().text(\"" + name + "\")"),
            AppiumBy.iOSNsPredicateString("label == \"" + name + "\"")
        );
    }

    // ── Actions ───────────────────────────────────────────────────────────

    public ProductCatalogScreen waitForScreen() {
        waitForVisible(catalogScreen());
        log.info("ProductCatalogScreen is displayed.");
        return this;
    }

    public void openFirstProduct() {
        tap(firstProduct());
    }

    public void openProductByName(String name) {
        scrollToElement(productByName(name));
        tap(productByName(name));
    }

    // ── Assertions ────────────────────────────────────────────────────────

    public boolean isDisplayed() {
        return isDisplayed(catalogScreen());
    }
}
