package com.saucelabs.automation.tests;

import com.saucelabs.automation.helpers.TestData;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * CheckoutTest
 * ------------
 * Covers the complete purchase flow and cart behaviour.
 *
 *   TC_CHECKOUT_01 – Full flow: browse → add to cart → checkout → confirm
 *   TC_CHECKOUT_02 – Cart badge increments when item is added
 *   TC_CHECKOUT_03 – Cart persists after app is backgrounded and restored
 */
public class CheckoutTest extends BaseTest {

    /**
     * All checkout tests start from the product catalog.
     * Login once in @BeforeMethod (after driver is up from BaseTest).
     */
    @BeforeMethod(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginToCatalog() {
        step("Login with valid credentials");
    	loginScreen.openLoginScreen();
        loginScreen.waitForScreen();
        loginScreen.login(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);
        catalogScreen.waitForScreen();
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC_CHECKOUT_01: Full purchase flow
    // ─────────────────────────────────────────────────────────────────────
    @Test(description = "Full flow: browse catalog → add to cart → checkout → confirmation")
    public void tc_checkout_01_fullPurchaseFlow_shouldShowConfirmation() {

        step("Open first product from catalog");
        catalogScreen.openFirstProduct();

        step("Verify product detail screen is shown");
        productDetailScreen.waitForScreen();

        step("Add product to cart");
        productDetailScreen.addToCart();

        step("Open cart");
        cartScreen.openCart();

        step("Verify cart has at least one item");
        Assert.assertTrue(
            cartScreen.hasItems(),
            "Cart should contain at least one item after Add To Cart"
        );

        step("Proceed to checkout");
        cartScreen.proceedToCheckout();

        step("Fill in shipping information");
        checkoutScreen.fillShippingInfo(TestData.STANDARD_SHIPPING);

        step("Fill in payment information");
        checkoutScreen.fillPaymentInfo(TestData.STANDARD_PAYMENT);

        step("Place order");
        checkoutScreen.placeOrder();

        step("Verify order confirmation is displayed");
        checkoutScreen.waitForConfirmation();
        Assert.assertTrue(
            checkoutScreen.isConfirmationDisplayed(),
            "Order confirmation screen should be displayed after placing the order"
        );
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC_CHECKOUT_02: Cart badge increments
    // ─────────────────────────────────────────────────────────────────────
    @Test(description = "Cart badge count should increment when a product is added")
    public void tc_checkout_02_addToCart_badgeShouldIncrement() {

        step("Open first product");
        catalogScreen.openFirstProduct();
        productDetailScreen.waitForScreen();

        step("Add product to cart");
        productDetailScreen.addToCart();

        step("Verify cart badge shows count ≥ 1");
        int badgeCount = cartScreen.getBadgeCount();
        Assert.assertTrue(
            badgeCount >= 1,
            "Cart badge should show at least 1 after adding a product. Actual: " + badgeCount
        );
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC_CHECKOUT_03 (Bonus): Cart persists after app backgrounding
    // ─────────────────────────────────────────────────────────────────────
    @Test(description = "Cart items should persist after the app is backgrounded and restored")
    public void tc_checkout_03_cartPersistsAfterBackground() {

        step("Open first product and add to cart");
        catalogScreen.openFirstProduct();
        productDetailScreen.waitForScreen();
        productDetailScreen.addToCart();

        int badgeBefore = cartScreen.getBadgeCount();
        step("Badge before background: " + badgeBefore);

        step("Background app for 5 seconds");
        catalogScreen.backgroundApp(5);

        step("App returned to foreground – verify catalog is still shown");
        catalogScreen.waitForScreen();

        step("Verify cart badge count is unchanged after restore");
        int badgeAfter = cartScreen.getBadgeCount();
        Assert.assertEquals(
            badgeAfter, badgeBefore,
            "Cart badge count should be the same after backgrounding the app"
        );
    }
}
