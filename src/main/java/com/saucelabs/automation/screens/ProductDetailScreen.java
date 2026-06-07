package com.saucelabs.automation.screens;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class ProductDetailScreen extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────

    private By productDetailsScreen() {
        return sel(
            AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/productTV\")"),   
            AppiumBy.accessibilityId("ProductDetails-screen")
        );
    }

    private By addToCartButton() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/cartBt\")"),  
            AppiumBy.accessibilityId("Add To Cart")
        );
    }

    private By productHighlightsTitle() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/productHeightLightsTV\")"), 
            AppiumBy.accessibilityId("Product Highlights")
        );
    }

    private By subtractAmountButton() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/minusIV\")"), 
            AppiumBy.accessibilityId("SubtractMinus Icons")     // iOS - from PageObject.swift
        );
    }

    private By addAmountButton() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/plusIV\")"), 
            AppiumBy.accessibilityId("AddPlus Icons")
        );
    }

    // ── Actions ───────────────────────────────────────────────────────────

    public ProductDetailScreen waitForScreen() {
        waitForVisible(productDetailsScreen());
        log.info("ProductDetailScreen is displayed.");
        return this;
    }

    public void addToCart() {
        log.info("Tapping Add To Cart");
        tap(addToCartButton());
    }

    public void increaseQuantity() {
        tap(addAmountButton());
    }

    public void decreaseQuantity() {
        tap(subtractAmountButton());
    }

    // ── Assertions ────────────────────────────────────────────────────────

    public String getTitle() {
        return getText(productDetailsScreen());
    }

    public boolean isProductHighlightsSectionVisible() {
        return isDisplayed(productHighlightsTitle());
    }
}
