package com.SWP391.G3PCoffee;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class RegisterPage {
    private WebDriver driver;


    // Khai báo các phần tử trên trang đăng ký
    private By fullNameField = By.id("fullName");
    private By phoneField = By.id("phone");
    private By emailField = By.id("email");
    private By otpField = By.id("otpCode");
    private By passwordField = By.id("password");
    private By confirmPasswordField = By.id("confirmPassword");
    private By submitButton = By.cssSelector("button[type='submit']");
    private By successMessage = By.xpath("//div[@id='swal2-html-container']");

    // Constructor
    public RegisterPage(WebDriver driver) {
        this.driver = driver;
    }


    // Nhập họ tên
    public void setFullName(String fullName) {
        driver.findElement(fullNameField).sendKeys(fullName);
    }


    // Nhập số điện thoại
    public void setPhone(String phone) {
        driver.findElement(phoneField).sendKeys(phone);
    }


    // Nhập email
    public void setEmail(String email) {
        driver.findElement(emailField).sendKeys(email);
    }


    // Nhập mã OTP
    public void setOtp(String otp) {
        driver.findElement(otpField).sendKeys(otp);
    }


    // Nhập mật khẩu
    public void setPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }


    // Nhập lại mật khẩu
    public void setConfirmPassword(String confirmPassword) {
        driver.findElement(confirmPasswordField).sendKeys(confirmPassword);
    }


    // Nhấn nút đăng ký
    public void clickSubmit() {
        driver.findElement(submitButton).click();
    }




    // Hàm thực hiện đăng ký
    public void register(String fullName, String phone, String email, String otp, String password, String confirmPassword) {
        setFullName(fullName);
        setPhone(phone);
        setEmail(email);
        setOtp(otp);
        setPassword(password);
        setConfirmPassword(confirmPassword);
        clickSubmit();
    }


    // Lấy thông báo thành công
    public String getMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        WebElement messageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
        return messageElement.getText();
    }
}
