package com.SWP391.G3PCoffee;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static CartPage cartPage;

    @BeforeAll
    public static void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        cartPage = new CartPage(driver);
    }

    @Test
    @Order(1)
    public void testEmptyCart() throws InterruptedException {
        cartPage.navigateToCart();

        if (cartPage.isCartEmpty()) {
            WebElement continueShoppingLink = driver.findElement(By.cssSelector(".continue-shopping"));
            Assertions.assertTrue(continueShoppingLink.isDisplayed(), "Continue shopping button should be displayed for empty cart");

            continueShoppingLink.click();
            wait.until(ExpectedConditions.urlContains("/shop"));

            cartPage.addProductToCart();

            Thread.sleep(2000);

            cartPage.navigateToCart();

            List<WebElement> cartItems = cartPage.getCartItems();
            Assertions.assertFalse(cartItems.isEmpty(), "Cart should contain items after adding a product");
        } else {
            List<WebElement> cartItems = cartPage.getCartItems();
            Assertions.assertFalse(cartItems.isEmpty(), "Cart should contain items");
        }
    }

    @Test
    @Order(2)
    public void testQuantityUpdate() {
        cartPage.ensureCartHasItems();
        cartPage.navigateToCart();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".order-item")));

        List<WebElement> cartItems = cartPage.getCartItems();
        WebElement firstItem = cartItems.get(0);

        WebElement quantityElement = cartPage.getQuantityElement(firstItem);
        String cartItemId = quantityElement.getAttribute("id").replace("quantity-", "");

        int initialQuantity = Integer.parseInt(quantityElement.getText().trim());

        WebElement subtotalElement = driver.findElement(By.id("subtotal-" + cartItemId));
        String initialSubtotal = subtotalElement.getText().trim();

        WebElement increaseButton = cartPage.getIncreaseButton(firstItem);
        increaseButton.click();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.textToBePresentInElement(quantityElement, String.valueOf(initialQuantity))));

        int newQuantity = Integer.parseInt(quantityElement.getText().trim());
        Assertions.assertEquals(initialQuantity + 1, newQuantity, "Quantity should increase by 1");

        wait.until(ExpectedConditions.not(
                ExpectedConditions.textToBePresentInElement(subtotalElement, initialSubtotal)));

        String updatedSubtotal = subtotalElement.getText().trim();
        Assertions.assertNotEquals(initialSubtotal, updatedSubtotal, "Subtotal should change after quantity update");

        WebElement decreaseButton = cartPage.getDecreaseButton(firstItem);
        decreaseButton.click();

        wait.until(ExpectedConditions.textToBePresentInElement(quantityElement, String.valueOf(initialQuantity)));

        int finalQuantity = Integer.parseInt(quantityElement.getText().trim());
        Assertions.assertEquals(initialQuantity, finalQuantity, "Quantity should decrease back to initial value");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        subtotalElement = driver.findElement(By.id("subtotal-" + cartItemId));
        String finalSubtotal = subtotalElement.getText().trim();

        Assertions.assertNotEquals(updatedSubtotal, finalSubtotal, "Subtotal should change after quantity decrease");
    }

    @Test
    @Order(3)
    public void testUpdateItemOptions() {
        cartPage.ensureCartHasItems();
        cartPage.navigateToCart();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".order-item")));

        List<WebElement> cartItems = cartPage.getCartItems();
        WebElement firstItem = cartItems.get(0);

        List<WebElement> sizeSelects = driver.findElements(By.cssSelector("select[id^='size-select-']"));
        if (!sizeSelects.isEmpty()) {
            WebElement sizeSelect = sizeSelects.get(0);

            String cartItemId = sizeSelect.getAttribute("id").replace("size-select-", "");
            WebElement subtotalElement = driver.findElement(By.id("subtotal-" + cartItemId));
            String initialSubtotal = subtotalElement.getText().trim();

            Select select = new Select(sizeSelect);
            List<WebElement> options = select.getOptions();

            if (options.size() > 1) {
                int currentIndex = select.getFirstSelectedOption().equals(options.get(0)) ? 1 : 0;
                select.selectByIndex(currentIndex);

                wait.until(ExpectedConditions.not(
                        ExpectedConditions.textToBePresentInElement(subtotalElement, initialSubtotal)));

                String newSubtotal = subtotalElement.getText().trim();
                Assertions.assertNotEquals(initialSubtotal, newSubtotal, "Subtotal should change after updating size");
            }
        }

        List<WebElement> toppingsToggles = driver.findElements(By.cssSelector("[id^='toppings-toggle-']"));
        if (!toppingsToggles.isEmpty()) {
            WebElement toppingsToggle = toppingsToggles.get(0);
            String cartItemId = toppingsToggle.getAttribute("id").replace("toppings-toggle-", "");

            WebElement subtotalElement = driver.findElement(By.id("subtotal-" + cartItemId));
            String initialSubtotal = subtotalElement.getText().trim();

            toppingsToggle.click();

            WebElement toppingsMenu = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("toppings-menu-" + cartItemId)));

            List<WebElement> toppingOptions = toppingsMenu.findElements(By.cssSelector(".dropdown-option"));
            if (!toppingOptions.isEmpty()) {
                toppingOptions.get(0).click();

                wait.until(ExpectedConditions.not(
                        ExpectedConditions.textToBePresentInElement(subtotalElement, initialSubtotal)));

                String newSubtotal = subtotalElement.getText().trim();
                Assertions.assertNotEquals(initialSubtotal, newSubtotal, "Subtotal should change after adding a topping");
            }
        }
    }

    @Test
    @Order(4)
    public void testRemoveItem() {
        cartPage.ensureCartHasItems();
        cartPage.navigateToCart();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".order-item")));

        List<WebElement> initialCartItems = cartPage.getCartItems();
        int initialCount = initialCartItems.size();
        Assertions.assertTrue(initialCount > 0, "Cart should not be empty for this test");

        WebElement totalElement = cartPage.getTotalAmountElement();
        String initialTotal = totalElement.getText().trim();

        WebElement removeButton = cartPage.getRemoveButton();

        ((JavascriptExecutor) driver).executeScript("window.confirm = function() { return true; }");

        removeButton.click();

        wait.until(ExpectedConditions.stalenessOf(removeButton));

        List<WebElement> emptyCartMessage = driver.findElements(By.cssSelector(".empty-cart"));
        if (!emptyCartMessage.isEmpty()) {
            Assertions.assertTrue(initialCount == 1, "Cart should be empty after removing the only item");
            return;
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".order-item")));
        List<WebElement> updatedCartItems = cartPage.getCartItems();
        int updatedCount = updatedCartItems.size();

        Assertions.assertEquals(initialCount - 1, updatedCount, "Cart item count should decrease by 1");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("totalAmount")));
        WebElement updatedTotalElement = cartPage.getTotalAmountElement();
        String updatedTotal = updatedTotalElement.getText().trim();

        Assertions.assertNotEquals(initialTotal, updatedTotal, "Total amount should change after removing an item");
    }

    @Test
    @Order(5)
    public void testApplyValidPromotion() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));

        cartPage.ensureCartHasItems();
        cartPage.navigateToCart();

        longWait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));

        try {
            WebElement promotionsSection = longWait.until(ExpectedConditions.presenceOfElementLocated(By.id("available-promotions")));

            List<WebElement> applyButtons = driver.findElements(By.cssSelector(".apply-promotion-btn"));

            if (applyButtons.isEmpty()) {
                Assertions.fail("No promotions available to test");
                return;
            }

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", applyButtons.get(0));

            WebElement clickableButton = longWait.until(ExpectedConditions.elementToBeClickable(applyButtons.get(0)));
            clickableButton.click();

            boolean notificationOrPromotionFound = longWait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.id("notification-container")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("#applied-promotions .bg-green-50")),
                    ExpectedConditions.presenceOfElementLocated(By.className("promotion-success"))
            )) != null;

            Assertions.assertTrue(notificationOrPromotionFound, "Promotion application result should be visible");

        } catch (TimeoutException e) {
            System.out.println("Page source: " + driver.getPageSource());
            System.out.println("Current URL: " + driver.getCurrentUrl());
            Assertions.fail("Promotion application test failed: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    public void testCheckoutProcess() {
        cartPage.ensureCartHasItems();
        cartPage.navigateToCart();

        WebElement checkoutButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".checkout-btn")));

        checkoutButton.click();

        wait.until(ExpectedConditions.urlContains("/checkout"));

        boolean checkoutPageLoaded = driver.getCurrentUrl().contains("/checkout");
        Assertions.assertTrue(checkoutPageLoaded, "Should be redirected to checkout page");
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}