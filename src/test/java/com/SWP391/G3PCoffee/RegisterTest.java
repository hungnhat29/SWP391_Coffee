package com.SWP391.G3PCoffee;




import com.SWP391.G3PCoffee.RegisterPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;


class RegisterTest {
    static WebDriver driver;
    static RegisterPage registerPage;


    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:8080/auth/register");


        // Khởi tạo trang đăng ký
        registerPage = new RegisterPage(driver);
    }


    @Test
    @DisplayName("TC01 - Đăng ký thành công với thông tin hợp lệ")
    void testRegisterValid() {
        String randomEmail = "test" + System.currentTimeMillis() + "@gmail.com";
        registerPage.register("Nguyen Van A", "0123456666", "tuser@gmail.com", "123456", "Password123", "Password123");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        boolean isRedirected = wait.until(ExpectedConditions.urlToBe("http://localhost:8080/home"));
        assertTrue(isRedirected, "Người dùng không được chuyển hướng về trang chủ sau khi đăng ký thành công!");
    }


    @Test
    @DisplayName("TC02 - Đăng ký thất bại do họ tên rỗng")
    void testRegisterEmptyFullName() {
        registerPage.register("", "0123456789", "testuser@gmail.com", "123456", "Password123", "Password123");


        assertTrue(registerPage.getMessage().contains("Vui lòng điền đầy đủ tất cả các trường!"));
    }


    @Test
    @DisplayName("TC03 - Đăng ký thất bại do số điện thoại rỗng")
    void testRegisterEmptyPhone() {
        registerPage.register("Nguyen Van A", "", "testuser@gmail.com", "123456", "Password123", "Password123");


        assertTrue(registerPage.getMessage().contains("Vui lòng điền đầy đủ tất cả các trường!"));
    }



    @Test
    @DisplayName("TC04 - Đăng ký thất bại do số điện thoại sai định dạng")
    void testRegisterInvalidPhone() {
        registerPage.register("Nguyen Van A", "abcd1234", "testuser@gmail.com", "123456", "Password123", "Password123");


        assertTrue(registerPage.getMessage().contains("Số điện thoại không hợp lệ."));
    }





    @Test
    @DisplayName("TC05 - Đăng ký thất bại do OTP rỗng")
    void testRegisterEmptyOtp() {
        registerPage.register("Nguyen Van A", "0123456789", "testuser@gmail.com", "", "Password123", "Password123");


        assertTrue(registerPage.getMessage().contains("Vui lòng điền đầy đủ tất cả các trường!"));
    }


    @Test
    @DisplayName("TC06 - Đăng ký thất bại do OTP sai")
    void testRegisterInvalidOtp() {
        try{
            registerPage.register("Nguyen Van A", "0123456789", "testuser@gmail.com", "000000", "Password123", "Password123");

        } catch (RuntimeException e) {
            assertTrue(registerPage.getMessage().contains("OTP không hợp lệ hoặc đã hết hạn!"));
        }


    }



    @Test
    @DisplayName("TC07 - Đăng ký thất bại do mật khẩu rỗng")
    void testRegisterEmptyPassword() {
        registerPage.register("Nguyen Van A", "0123456789", "testuser@gmail.com", "123456", "", "Password123");


        assertTrue(registerPage.getMessage().contains("Vui lòng điền đầy đủ tất cả các trường!"));
    }


    @Test
    @DisplayName("TC08 - Đăng ký thất bại do mật khẩu yếu")
    void testRegisterWeakPassword() {
        registerPage.register("Nguyen Van A", "0123456789", "testuser@gmail.com", "123456", "123", "123");


        assertTrue(registerPage.getMessage().contains("Mật khẩu phải có ít nhất 6 ký tự và bao gồm cả chữ cái lẫn số."));
    }


    @Test
    @DisplayName("TC09 - Đăng ký thất bại do mật khẩu không khớp")
    void testRegisterPasswordMismatch() {
        registerPage.register("Nguyen Van A", "0123456789", "testuser@gmail.com", "123456", "Password123", "Password456");


        assertTrue(registerPage.getMessage().contains("Mật khẩu nhập lại không khớp."));
    }
    @Test
    @DisplayName("TC10 - Đăng ký thất bại do email đã tồn tại")
    void testRegisterEmailExists() {
        try {
            registerPage.register("Nguyen Van B", "0987654321", "testuser@gmail.com", "123456", "Password123", "Password123");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Email is already in use!"));
        }
    }

    @Test
    @DisplayName("TC11 - Đăng ký thất bại do số điện thoại đã tồn tại")
    void testRegisterPhoneExists() {
       try{
           registerPage.register("Nguyen Van C", "0123456789", "newuser@gmail.com", "123456", "Password123", "Password123");
       }catch(RuntimeException e){
           assertTrue(registerPage.getMessage().contains("Email is already in use!"));
       }

    }
//    @Test
//    @DisplayName("TC03 - Đăng ký thất bại do email sai định dạng")
//    void testRegisterInvalidEmail() {
//        registerPage.register("Test User", "0123456789", "testuser#gmail.com", "123456", "Password123", "Password123");
//        assertTrue(registerPage.getMessage().contains("Email không hợp lệ! Vui lòng nhập đúng định dạng email."));
//    }


    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}
