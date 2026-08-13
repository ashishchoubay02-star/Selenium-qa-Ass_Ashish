package com.qa.tests;

import com.aventstack.extentreports.Status;
import com.qa.pages.OrangeHRMLoginPage;
import com.qa.utils.ExcelUtils;
import com.qa.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Use Case 2 : Data-driven login test for OrangeHRM demo site.
 *
 * Test data comes from: src/test/resources/testdata/LoginData.xlsx
 * Columns: Username | Password | ExpectedResult (VALID / INVALID)
 */
public class OrangeHRMLoginTest extends BaseTest {

    private static final String LOGIN_URL =
            "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
    private static final String DATA_FILE =
            System.getProperty("user.dir") + "/src/test/resources/testdata/LoginData.xlsx";

    @DataProvider(name = "loginData")
    public Object[][] loginData() throws Exception {
        return ExcelUtils.readLoginData(DATA_FILE, "LoginData");
    }

    @Test(dataProvider = "loginData",
          description = "OrangeHRM: valid & invalid login combinations from excel")
    public void loginTest(String username, String password, String expectedResult) {

        driver.get(LOGIN_URL);
        ExtentReportManager.getTest().log(Status.INFO,
                "Opened login page | user='" + username + "' | expected=" + expectedResult);

        OrangeHRMLoginPage loginPage = new OrangeHRMLoginPage(driver);
        loginPage.login(username, password);

        if ("VALID".equalsIgnoreCase(expectedResult)) {
            boolean success = loginPage.isLoginSuccessful();
            ExtentReportManager.getTest().log(success ? Status.PASS : Status.FAIL,
                    "Login success expected. Dashboard shown = " + success);
            Assert.assertTrue(success, "Expected successful login for user: " + username);
            loginPage.logoutIfLoggedIn();
        } else {
            String error = loginPage.getErrorMessage();
            ExtentReportManager.getTest().log(Status.INFO, "Error message shown: '" + error + "'");
            boolean loginFailed = !loginPage.isLoginSuccessful() || !error.isEmpty();
            ExtentReportManager.getTest().log(loginFailed ? Status.PASS : Status.FAIL,
                    "Login failure expected. Failed = " + loginFailed);
            Assert.assertTrue(loginFailed, "Expected login to fail for user: " + username);
        }
    }
}
