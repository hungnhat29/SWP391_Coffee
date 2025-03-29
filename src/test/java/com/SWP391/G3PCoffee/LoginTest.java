package com.SWP391.G3PCoffee;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {

    private static WebDriver driver;

    @BeforeAll
    public static void setup() {
        // Tự động tải và cấu hình ChromeDriver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    @Order(1)
    public void testLoginSuccess() throws InterruptedException {
        driver.get("http://localhost:8080/auth/login");

        // Tìm và nhập username + password
        WebElement emailField = driver.findElement(By.id("email"));
        emailField.sendKeys("hungpoporo@gmail.com");

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("h12345");

        // Click nút login
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        // Đợi 1 chút để chuyển trang (hoặc dùng WebDriverWait)
        Thread.sleep(2000);

        // Kiểm tra URL sau login hoặc element hiển thị
        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/home"), "Login thành công và điều hướng tới dashboard");
    }

    @Test
    @Order(2)
    public void testLoginFailed() throws InterruptedException {
        driver.get("http://localhost:8080/auth/login");

        WebElement emailField = driver.findElement(By.id("email"));
        emailField.sendKeys("hungpoporo@gmail.com");

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("h12344");

        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        Thread.sleep(2000);

        // Kiểm tra hiển thị thông báo lỗi
        WebElement errorMsg = driver.findElement(By.id("error-message"));
        Assertions.assertTrue(errorMsg.isDisplayed(), "Hiển thị thông báo lỗi khi login fail");
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}
