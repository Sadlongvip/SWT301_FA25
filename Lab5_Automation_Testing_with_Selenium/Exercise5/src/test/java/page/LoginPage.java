package page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // Locators
    private By firstNameField = By.id("firstName");
    private By lastNameField = By.id("lastName");
    private By emailField = By.id("userEmail");
    private By genderField = By.id("gender");
    private By phoneNumberField = By.id("userNumber");
    private By dateOfBirthField = By.id("dateOfBirthInput");
    private By currentAddressField = By.id("currentAddress");

    // Locators cho từng lựa chọn
    private By maleRadio   = By.xpath("//label[@for='gender-radio-1']");
    private By femaleRadio = By.xpath("//label[@for='gender-radio-2']");
    private By otherRadio = By.xpath("//label[@for='gender-radio-3']");

    private By submitButton = By.cssSelector("button[type='submit']");
    private By successMsg = By.cssSelector(".flash.success");
    private By errorMsg = By.cssSelector(".flash.error");

    // Actions
    public void navigate() {
        navigateTo("https://demoqa.com/automation-practice-form");
    }

    public void submit(String firstName, String lastName, String userEmail, String gender,
                       String phoneNumber, String dateOfBirth, String currentAddress) {
        // nhập thông tin cơ bản
        type(firstNameField, firstName);
        type(lastNameField, lastName);
        type(emailField, userEmail);
        type(phoneNumberField, phoneNumber);
        type(dateOfBirthField, dateOfBirth);
        type(firstNameField, "");
        type(currentAddressField, currentAddress);

        // chọn giới tính
        switch (gender.toLowerCase()) {
            case "male":
                click(maleRadio);
                break;
            case "female":
                click(femaleRadio);
                break;
            case "other":
                click(otherRadio);
                break;
            default:
                throw new IllegalArgumentException("Gender không hợp lệ: " + gender);
        }

        // click nút submit
        click(submitButton);
    }


    public By getSuccessLocator() {
        return successMsg;
    }

    public By getErrorLocator() {
        return errorMsg;
    }

    public String getMessageText(By locator) {
        return getText(locator);
    }
}
