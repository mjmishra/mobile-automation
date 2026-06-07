package com.saucelabs.automation.screens;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * CartScreen
 * ----------
 * iOS locators sourced directly from PageObject.swift:
 *   app.buttons["Cart-tab-item"]              → cart icon in tab bar
 *   app.otherElements["Cart-screen"]          → cart screen container
 *   cartScreen.staticTexts["No Items"]        → empty cart message
 *   cartScreen.buttons["GoShopping"]          → go shopping button when cart is empty
 *   cartScreen.staticTexts[productName]       → item row by product name
 */
public class CartScreen extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────

    private By cartTabIcon() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/cartRL\")"),             
            AppiumBy.accessibilityId("Cart-tab-item")
        );
    }
    

    private By cartScreen() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/productTV\")"), 
            AppiumBy.accessibilityId("Cart-screen") 
        );
    }
 

    private By checkoutButton() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/cartBt\")"),             
            AppiumBy.accessibilityId("Proceed To Checkout")
        );
    }

    private By emptyCartMessage() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/noItemTitleTV\")"), 
            AppiumBy.accessibilityId("No Items") 
        );
    }

    private By goShoppingButton() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/shoppingBt\")"), 
            AppiumBy.accessibilityId("GoShopping")    
        );
    }

    private By cartBadge() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/cartTV\")"), 
            AppiumBy.iOSNsPredicateString(
                "name == 'Cart-tab-item' AND value != ''")      // iOS - badge value on tab icon
        );
    }

    /** Locates a specific product row in the cart by its name. */
    private By cartItemByName(String productName) {
        return sel(
            AppiumBy.androidUIAutomator(
                "new UiSelector().text(\"" + productName + "\")"),
            AppiumBy.accessibilityId(productName)               // iOS - from PageObject.swift: cartScreen.staticTexts[productName]
        );
    }

    // ── Actions ───────────────────────────────────────────────────────────

    /** Tap the cart icon in the tab bar and wait for cart screen. */
    public CartScreen openCart() {
        log.info("Opening cart via tab icon");
        tap(cartTabIcon());
        waitForVisible(cartScreen());
        return this;
    }

    public void proceedToCheckout() {
        log.info("Proceeding to checkout");
        tap(checkoutButton());
    }

    public void tapGoShopping() {
        tap(goShoppingButton());
    }

    // ── Assertions ────────────────────────────────────────────────────────

    public CartScreen waitForScreen() {
        waitForVisible(cartScreen());
        log.info("CartScreen is displayed.");
        return this;
    }

    public boolean isCartEmpty() {
        return isDisplayed(emptyCartMessage());
    }

    public boolean hasItems() {
        return !isCartEmpty();
    }

    public boolean isItemInCart(String productName) {
        return isDisplayed(cartItemByName(productName));
    }

    /**
     * Returns the numeric badge count on the cart tab icon.
     * Returns 0 if badge is not visible.
     */
    public int getBadgeCount() {
        try {
            String text = driver().findElement(cartBadge()).getText();
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
