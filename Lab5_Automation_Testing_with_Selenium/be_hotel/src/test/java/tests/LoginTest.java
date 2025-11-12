package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import page.LoginPage;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeEach
    public void setUp() {
        loginPage = new LoginPage(driver);
        loginPage.navigateToLogin();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/login-data.csv", numLinesToSkip = 1)
    public void testLogin(String email, String password, String expectedResult) {
        loginPage.login(email, password);

        if ("success".equals(expectedResult)) {
            try {
                Thread.sleep(2000); // Wait for redirection
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertTrue(driver.getCurrentUrl().contains("/admin"), "Should be redirected to admin dashboard after successful login.");
        } else {
            assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed for failed login.");
            assertEquals("Tài khoản không tồn tại", loginPage.getErrorMessage(), "Error message should indicate invalid credentials.");
        }
    }
}
