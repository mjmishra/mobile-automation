package com.saucelabs.automation.screens;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class CheckoutScreen extends BasePage {

    // ── Step 1: Shipping ──────────────────────────────────────────────────

    private By fullNameField() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/fullNameET\")"),             
            AppiumBy.accessibilityId("test-First Name")
        );
    }

    private By addressLine1Field() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/address1ET\")"),             
            AppiumBy.accessibilityId("test-Last Name")
        );
    }

    private By addressLine2Field() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/address2ET\")"),             
                AppiumBy.accessibilityId("test-Address Line 1")
        );
    }

    private By cityField() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/cityET\")"),             
            AppiumBy.accessibilityId("test-City")
        );
    }

    private By stateField() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/stateET\")"),             
            AppiumBy.accessibilityId("test-State/Region")
        );
    }

    private By zipField() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/zipET\")"),             
            AppiumBy.accessibilityId("test-Zip Code")
        );
    }

    private By countryField() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/countryET\")"),             
            AppiumBy.accessibilityId("test-Country")
        );
    }

    private By toPaymentButton() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/paymentBtn\")"),             
            AppiumBy.accessibilityId("test-PAYMENT")
        );
    }

    // ── Step 2: Payment ───────────────────────────────────────────────────

    private By cardNumberField() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/cardNumberET\")"),             
            AppiumBy.accessibilityId("test-Card Number")
        );
    }

    private By expiryField() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/expirationDateET\")"),             
            AppiumBy.accessibilityId("test-Expiration Date")
        );
    }

    private By cvvField() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/securityCodeET\")"),             
            AppiumBy.accessibilityId("test-Security Code")
        );
    }

    private By cardHolderField() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/nameET\")"),             
            AppiumBy.accessibilityId("test-Card Holder Name")
        );
    }

    private By toReviewButton() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/paymentBtn\")"),             
            AppiumBy.accessibilityId("test-REVIEW ORDER")
        );
    }

    // ── Step 3: Review ────────────────────────────────────────────────────

    private By placeOrderButton() {
        return sel(
        		AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/paymentBtn\")"),  
            AppiumBy.accessibilityId("test-PLACE ORDER")
        );
    }

    // ── Step 4: Confirmation ──────────────────────────────────────────────

    private By confirmationHeader() {
        return sel(
        	AppiumBy.androidUIAutomator(
        	            "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/completeTV\")"),  
            AppiumBy.accessibilityId("test-THANK YOU FOR YOUR ORDER")
        );
    }

    // ── Actions ───────────────────────────────────────────────────────────

    /** Fill shipping address fields and move to the payment step. */
    public void fillShippingInfo(ShippingDetails details) {
        log.info("Filling shipping info");
        waitForVisible(fullNameField());
        typeInto(fullNameField(), details.firstName());
        typeInto(addressLine1Field(),  details.lastName());
        typeInto(addressLine2Field(),   details.address());
        typeInto(cityField(),      details.city());
        typeInto(stateField(),     details.state());
        typeInto(zipField(),       details.zip());
        typeInto(countryField(),   details.country());
        scrollToElement(toPaymentButton());
        tap(toPaymentButton());
    }

    /** Fill payment card fields and move to the review step. */
    public void fillPaymentInfo(PaymentDetails details) {
        log.info("Filling payment info");
        waitForVisible(cardNumberField());
        typeInto(cardNumberField(),  details.cardNumber());
        typeInto(expiryField(),      details.expiry());
        typeInto(cvvField(),         details.cvv());
        typeInto(cardHolderField(),  details.cardHolderName());
        scrollToElement(toReviewButton());
        tap(toReviewButton());
    }

    /** Tap the final Place Order button on the review screen. */
    public void placeOrder() {
        log.info("Placing order");
        scrollToElement(placeOrderButton());
        tap(placeOrderButton());
    }

    // ── Assertions ────────────────────────────────────────────────────────

    /** Waits up to 20 seconds for the confirmation screen to appear. */
    public void waitForConfirmation() {
        log.info("Waiting for order confirmation");
        waitForVisible(confirmationHeader(), 20);
    }

    public boolean isConfirmationDisplayed() {
        return isDisplayed(confirmationHeader());
    }

    // ── Nested data records ───────────────────────────────────────────────

    /** Immutable value object for shipping form data. */
    public record ShippingDetails(
        String firstName,
        String lastName,
        String address,
        String city,
        String state,
        String zip,
        String country
    ) {}

    /** Immutable value object for payment form data. */
    public record PaymentDetails(
        String cardNumber,
        String expiry,
        String cvv,
        String cardHolderName
    ) {}
}
