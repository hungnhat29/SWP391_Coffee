//package com.SWP391.G3PCoffee;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//import java.time.Duration;
//
//public class ProductAdminPage {
//    private WebDriver driver;
//    private WebDriverWait wait;
//
//    // Locators for the Add Product modal
//    private By addProductButton = By.cssSelector("button.add-button[data-bs-target='#addProductModal']");
//    private By productNameField = By.id("addProductName");
//    private By productDescriptionField = By.id("addProductDescription");
//    private By productBasePriceField = By.id("addProductBasePrice");
//    private By productCategoryDropdown = By.id("addProductCategory");
//    private By productImageUrlField = By.id("addProductImageUrl");
//    private By addSizeButton = By.id("addSizeBtn");
//    private By addToppingButton = By.id("addToppingBtn");
//    private By sizeNameField = By.cssSelector("#addSizesContainer .size-entry:last-child input[name^='sizeName']");
//    private By sizePriceField = By.cssSelector("#addSizesContainer .size-entry:last-child input[name^='sizePrice']");
//    private By toppingNameField = By.cssSelector("#addToppingsContainer .topping-entry:last-child input[name^='toppingName']");
//    private By toppingPriceField = By.cssSelector("#addToppingsContainer .topping-entry:last-child input[name^='toppingPrice']");
//    private By saveProductButton = By.id("addProductBtn");
//    private By closeModalButton = By.cssSelector("#addProductModal .btn-secondary");
//
//    // Locator for success/error message (using SweetAlert2)
//    private By alertMessage = By.cssSelector(".swal2-title");
//
//    public ProductAdminPage(WebDriver driver) {
//        this.driver = driver;
//        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//    }
//
//    // Methods to interact with elements
//    public void clickAddProductButton() {
//        wait.until(ExpectedConditions.elementToBeClickable(addProductButton)).click();
//    }
//
//    public void setProductName(String name) {
//        wait.until(ExpectedConditions.visibilityOfElementLocated(productNameField)).sendKeys(name);
//    }
//
//    public void setProductDescription(String description) {
//        driver.findElement(productDescriptionField).sendKeys(description);
//    }
//
//    public void setProductBasePrice(String price) {
//        driver.findElement(productBasePriceField).sendKeys(price);
//    }
//
//    public void selectProductCategory(String categoryValue) {
//        WebElement dropdown = driver.findElement(productCategoryDropdown);
//        dropdown.click();
//        driver.findElement(By.cssSelector("#addProductCategory option[value='" + categoryValue + "']")).click();
//    }
//
//    public void setProductImageUrl(String url) {
//        driver.findElement(productImageUrlField).sendKeys(url);
//    }
//
//    public void addSize(String sizeName, String sizePrice) {
//        driver.findElement(addSizeButton).click();
//        wait.until(ExpectedConditions.visibilityOfElementLocated(sizeNameField)).sendKeys(sizeName);
//        driver.findElement(sizePriceField).sendKeys(sizePrice);
//    }
//
//    public void addTopping(String toppingName, String toppingPrice) {
//        driver.findElement(addToppingButton).click();
//        wait.until(ExpectedConditions.visibilityOfElementLocated(toppingNameField)).sendKeys(toppingName);
//        driver.findElement(toppingPriceField).sendKeys(toppingPrice);
//    }
//
//    public void clickSaveProductButton() {
//        driver.findElement(saveProductButton).click();
//    }
//
//    public void clickCloseModalButton() {
//        driver.findElement(closeModalButton).click();
//    }
//
//    public String getAlertMessage() {
//        return wait.until(ExpectedConditions.visibilityOfElementLocated(alertMessage)).getText();
//    }
//}