//package com.SWP391.G3PCoffee;
//
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class ProductAdminTest {
//    private WebDriver driver;
//    private ProductAdminPage productAdminPage;
//
//    @BeforeEach
//    public void setUp() {
//        driver = new ChromeDriver();
//        driver.manage().window().maximize();
//        driver.get("http://localhost:8080/products");
//        productAdminPage = new ProductAdminPage(driver);
//    }
//
//    @Test
//    public void testAddProduct_ValidData() {
//        productAdminPage.clickAddProductButton();
//        productAdminPage.setProductName("Cappuccino");
//        productAdminPage.setProductDescription("A classic cappuccino with frothy milk");
//        productAdminPage.setProductBasePrice("4.50");
//        productAdminPage.selectProductCategory("1");
//        productAdminPage.setProductImageUrl("https://example.com/cappuccino.jpg");
//        productAdminPage.addSize("Medium", "0.50");
//        productAdminPage.addTopping("Whipped Cream", "0.30");
//        productAdminPage.clickSaveProductButton();
//        String expectedMessage = "Success";
//        assertEquals(expectedMessage, productAdminPage.getAlertMessage());
//    }
//
//    @Test
//    public void testAddProduct_InvalidPrice() {
//        productAdminPage.clickAddProductButton();
//        productAdminPage.setProductName("Latte");
//        productAdminPage.setProductDescription("A smooth latte");
//        productAdminPage.setProductBasePrice("-5.00"); // Invalid price
//        productAdminPage.selectProductCategory("1");
//        productAdminPage.setProductImageUrl("https://example.com/latte.jpg");
//        productAdminPage.addSize("Large", "1.00");
//        productAdminPage.addTopping("Caramel Syrup", "0.50");
//        productAdminPage.clickSaveProductButton();
//        String expectedMessage = "Error";
//        assertEquals(expectedMessage, productAdminPage.getAlertMessage());
//    }
//
//    @Test
//    public void testAddProduct_InvalidImageUrl() {
//        productAdminPage.clickAddProductButton();
//        productAdminPage.setProductName("Espresso");
//        productAdminPage.setProductDescription("A strong espresso shot");
//        productAdminPage.setProductBasePrice("3.00");
//        productAdminPage.selectProductCategory("1");
//        productAdminPage.setProductImageUrl("invalid-url"); // Invalid URL format
//        productAdminPage.addSize("Small", "0.00");
//        productAdminPage.addTopping("Sugar", "0.10");
//        productAdminPage.clickSaveProductButton();
//        String expectedMessage = "Error";
//        assertEquals(expectedMessage, productAdminPage.getAlertMessage());
//    }
//
//    @Test
//    public void testAddProduct_MissingRequiredFields() {
//        productAdminPage.clickAddProductButton();
//        productAdminPage.setProductName("");
//        productAdminPage.setProductDescription("");
//        productAdminPage.setProductBasePrice("");
//        productAdminPage.setProductImageUrl("");
//        productAdminPage.clickSaveProductButton();
//        String expectedMessage = "Error";
//        assertEquals(expectedMessage, productAdminPage.getAlertMessage());
//    }
//
//    @Test
//    public void testAddProduct_NoSizesOrToppings() {
//        productAdminPage.clickAddProductButton();
//        productAdminPage.setProductName("Mocha");
//        productAdminPage.setProductDescription("A chocolatey mocha");
//        productAdminPage.setProductBasePrice("5.00");
//        productAdminPage.selectProductCategory("1");
//        productAdminPage.setProductImageUrl("https://example.com/mocha.jpg");
//        productAdminPage.clickSaveProductButton();
//        String expectedMessage = "Error";
//        assertEquals(expectedMessage, productAdminPage.getAlertMessage());
//    }
//
//    @AfterEach
//    public void tearDown() {
//        if (driver != null) {
//            driver.quit();
//        }
//    }
//}