package com.qa.pages;

import com.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for OrangeHRM login page (Use Case 2).
 * URL: https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
 */
public class OrangeHRMLoginPage extends BasePage {

    // ---------- Locators ----------
    private final By usernameField = By.name("username");
    private final By passwordField = By.name("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By errorMessage = By.cssSelector("p.oxd-alert-content-text");
    private final By dashboardHeader = By.cssSelector("h6.oxd-topbar-header-breadcrumb-module");
    private final By requiredFieldMessage = By.cssSelector("span.oxd-input-field-error-message");

    public OrangeHRMLoginPage(WebDriver driver) {
        super(driver);
    }

    // ---------- Actions ----------

    public void login(String username, String password) {
        clear(usernameField);
        if (username != null && !username.isEmpty()) {
            type(usernameField, username);
        }
        clear(passwordField);
        if (password != null && !password.isEmpty()) {
            type(passwordField, password);
        }
        click(loginButton);
    }

    /** True if the Dashboard header is displayed after login. */
    public boolean isLoginSuccessful() {
        try {
            return waitForVisible(dashboardHeader).getText().contains("Dashboard");
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Returns "Invalid credentials" / "Required" style error text, or empty string. */
    public String getErrorMessage() {
        try {
            return waitForVisible(errorMessage).getText().trim();
        } catch (TimeoutException e) {
            try {
                return waitForVisible(requiredFieldMessage).getText().trim();
            } catch (TimeoutException ex) {
                return "";
            }
        }
    }

    /** Logout so the next data-driven iteration starts from the login page again. */
    public void logoutIfLoggedIn() {
        try {
            By userDropdown = By.cssSelector("p.oxd-userdropdown-name");
            By logoutLink = By.xpath("//a[text()='Logout']");
            click(userDropdown);
            click(logoutLink);
        } catch (Exception ignored) {
            // already on login page
        }
    }
}
