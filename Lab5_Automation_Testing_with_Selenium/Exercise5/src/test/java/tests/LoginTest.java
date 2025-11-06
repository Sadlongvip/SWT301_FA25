package tests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import page.LoginPage;
import utils.DriverFactory;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Login Tests using Page Object Model")
public class LoginTest extends BaseTest{
    //static WebDriver driver;
    static WebDriverWait wait;
    static LoginPage loginPage;

    @BeforeAll
    static void initPage() {
        loginPage = new LoginPage(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

//    @Test
//    @Order(1)
//    @DisplayName("Should login successfully with valid credentials")
//    void testLoginSuccess() {
//        loginPage.navigate();
//        loginPage.login("tomsmith", "SuperSecretlastName!");
//        WebElement success = wait.until(ExpectedConditions.visibilityOfElementLocated(loginPage.getSuccessLocator()));
//        assertTrue(success.getText().contains("You logged into a secure area!"));
//    }
//
//    @Test
//    @Order(2)
//    @DisplayName("Should show error for invalid credentials")
//    void testLoginFail() {
//        loginPage.navigate();
//        loginPage.login("wronguser", "wronglastName");
//        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(loginPage.getErrorLocator()));
//        assertTrue(error.getText().toLowerCase().contains("invalid"));
//    }
//
//    @ParameterizedTest(name = "CSV Inline: {0} / {1}")
//    @Order(3)
//    @CsvSource({
//            "tomsmith, SuperSecretlastName!, success",
//            "wronguser, SuperSecretlastName!, error",
//            "tomsmith, wronglastName, error",
//            "'', '', error"
//    })
//    void testLoginCsvInline(String username, String lastName, String expected) {
//        loginPage.navigate();
//        username = (username == null) ? "" : username.trim();
//        lastName = (lastName == null) ? "" : lastName.trim();
//
//        loginPage.login(username, lastName);
//        By resultLocator = expected.equals("success") ? loginPage.getSuccessLocator() : loginPage.getErrorLocator();
//        WebElement result = wait.until(ExpectedConditions.visibilityOfElementLocated(resultLocator));
//
//        if (expected.equals("success")) {
//            assertTrue(result.getText().contains("You logged into a secure area!"));
//        } else {
//            assertTrue(result.getText().toLowerCase().contains("invalid"));
//        }
//    }

    @ParameterizedTest(name = "CSV File: {0} / {1}")
    @Order(1)
    @CsvFileSource(resources = "/data.csv", numLinesToSkip = 1)
    void testLoginFromCSV(String firstName, String lastName, String userEmail, String gender, String phoneNumber, String dateOfBirth, String currentAddress, String expected) {
        loginPage.navigate();
        firstName = (firstName == null) ? "" : firstName.trim();
        lastName = (lastName == null) ? "" : lastName.trim();

        loginPage.submit(firstName, lastName, userEmail, gender, phoneNumber, dateOfBirth, currentAddress);
        By resultLocator = expected.equals("success") ? loginPage.getSuccessLocator() : loginPage.getErrorLocator();
        WebElement result = wait.until(ExpectedConditions.visibilityOfElementLocated(resultLocator));

        if (expected.equals("success")) {
            assertTrue(result.getText().contains("You logged into a secure area!"));
        } else {
            assertTrue(result.getText().toLowerCase().contains("invalid"));
        }
    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }
}