package com.saucelabs.automation.helpers;

import com.saucelabs.automation.screens.CheckoutScreen.PaymentDetails;
import com.saucelabs.automation.screens.CheckoutScreen.ShippingDetails;

/**
 * TestData
 * --------
 * Central repository for all test input data.
 *
 */
public final class TestData {

    // ── Credentials ───────────────────────────────────────────────────────

    public static final String VALID_USERNAME   = "bob@example.com";
    public static final String VALID_PASSWORD   = "10203040";

    public static final String INVALID_USERNAME = "      ";
    public static final String INVALID_PASSWORD = "      ";

    public static final String EMPTY            = "";

    // ── Shipping ──────────────────────────────────────────────────────────

    public static final ShippingDetails STANDARD_SHIPPING = new ShippingDetails(
        "Jane",
        "Doe",
        "123 Test Lane",
        "San Francisco",
        "CA",
        "94105",
        "United States"
    );

    // ── Payment ───────────────────────────────────────────────────────────

    public static final PaymentDetails STANDARD_PAYMENT = new PaymentDetails(
        "4111111111111111",
        "03/30",
        "737",
        "Jane Doe"
    );

    private TestData() {}
}
