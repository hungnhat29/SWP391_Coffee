package com.SWP391.G3PCoffee;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By contentWrapperLocator = By.cssSelector(".content-wrapper");
    private By emptyCartMessageLocator = By.cssSelector(".empty-cart");
    private By continueShoppingLinkLocator = By.cssSelector(".continue-shopping");
    private By cartItemsLocator = By.cssSelector(".order-item");
    private By productsLocator = By.cssSelector(".card");
    private By firstProductLocator = By.cssSelector(".card a");
    private By productTitleLocator = By.className("product-title");
    private By sizeOptionsLocator = By.className("size-option");
    private By addToCartButtonLocator = By.cssSelector("button.btn-order");
    private By removeButtonLocator = By.cssSelector(".remove-btn");
    private By totalAmountLocator = By.id("totalAmount");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateToCart() {
        driver.get("http://localhost:8080/cart");
        wait.until(ExpectedConditions.visibilityOfElementLocated(contentWrapperLocator));
    }

    public boolean isCartEmpty() {
        return !driver.findElements(emptyCartMessageLocator).isEmpty();
    }

    public void addProductToCart() {
        // Navigate to shop page
        driver.get("http://localhost:8080/shop");

        // Wait for products to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(productsLocator));

        // Click on the first product to go to its detail page
        WebElement firstProduct = driver.findElement(firstProductLocator);
        firstProduct.click();

        // Wait for product detail page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(productTitleLocator));

        // Select size if available
        List<WebElement> sizeOptions = driver.findElements(sizeOptionsLocator);
        if (!sizeOptions.isEmpty()) {
            sizeOptions.get(0).click();
        }

        // Click on "Đặt giao tận nơi" button to add to cart
        WebElement addToCartButton = driver.findElement(addToCartButtonLocator);
        addToCartButton.click();

        // Wait for sweet alert confirmation
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("swal2-popup")));
    }

    public void ensureCartHasItems() {
        navigateToCart();

        if (isCartEmpty()) {
            addProductToCart();
            navigateToCart();
        }
    }

    public List<WebElement> getCartItems() {
        return driver.findElements(cartItemsLocator);
    }

    public WebElement getQuantityElement(WebElement cartItem) {
        return cartItem.findElement(By.cssSelector("[id^='quantity-']"));
    }

    public WebElement getIncreaseButton(WebElement cartItem) {
        return cartItem.findElement(By.cssSelector(".qty-btn:nth-child(3)"));
    }

    public WebElement getDecreaseButton(WebElement cartItem) {
        return cartItem.findElement(By.cssSelector(".qty-btn:nth-child(1)"));
    }

    public WebElement getRemoveButton() {
        return driver.findElement(removeButtonLocator);
    }

    public WebElement getTotalAmountElement() {
        return driver.findElement(totalAmountLocator);
    }

    public List<WebElement> getSizeSelects() {
        return driver.findElements(By.cssSelector("select[id^='size-select-']"));
    }

    public List<WebElement> getToppingsToggles() {
        return driver.findElements(By.cssSelector("[id^='toppings-toggle-']"));
    }
}