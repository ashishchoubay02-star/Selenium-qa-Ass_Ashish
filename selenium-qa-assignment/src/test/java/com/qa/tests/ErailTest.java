package com.qa.tests;

import com.aventstack.extentreports.Status;
import com.qa.pages.ErailHomePage;
import com.qa.utils.ExcelUtils;
import com.qa.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Use Case 1 : https://erail.in/
 *
 * Step1: Open URL with web driver
 * Step2: Click on From field
 * Step3: Clear the data from "From" field
 * Step4: Insert "DEL" to open the dropdown
 * Step5: Select the station at 4th position & print it
 * Step6: Create an excel file with expected station names
 * Step7: Write dropdown data into excel & compare with expected
 * Step8: Select current date + 30 days in "Sort on Date" (dynamic)
 * Step9: Extent report (handled by ExtentTestListener)
 */
public class ErailTest extends BaseTest {

    private static final String EXPECTED_FILE =
            System.getProperty("user.dir") + "/src/test/resources/testdata/ExpectedStations.xlsx";
    private static final String COMPARISON_FILE =
            System.getProperty("user.dir") + "/test-output/StationComparison.xlsx";

    @Test(description = "Erail: From field dropdown, excel comparison and dynamic +30 days date selection")
    public void erailEndToEndFlow() throws Exception {

        // Step 1: Open URL
        driver.get("https://erail.in/");
        ExtentReportManager.getTest().log(Status.INFO, "Opened https://erail.in/");

        ErailHomePage home = new ErailHomePage(driver);

        // Step 2 + 3: Click on From field and clear it
        home.clickAndClearFromField();
        ExtentReportManager.getTest().log(Status.INFO, "Clicked & cleared 'From' field");

        // Step 4: Type DEL to open dropdown
        home.typeInFromField("DEL");
        ExtentReportManager.getTest().log(Status.INFO, "Typed 'DEL' in From field");

        // Step 7 (part 1): capture full dropdown list BEFORE selecting
        List<String> actualStations = home.getDropdownStationNames();
        ExtentReportManager.getTest().log(Status.INFO, "Dropdown stations: " + actualStations);

        // Step 5: Select 4th station and print it
        String fourthStation = home.selectStationAtPosition(4);
        System.out.println("Station at 4th position: " + fourthStation);
        ExtentReportManager.getTest().log(Status.PASS, "Selected 4th station: " + fourthStation);

        // Step 6: Create excel with expected station names
        List<String> expectedStations = Arrays.asList(
                "Denduluru", "Delang", "Delhi", "Delhi Azadpur", "Delhi Cantt");
        ExcelUtils.writeSingleColumn(EXPECTED_FILE, "Expected", "Expected Station", expectedStations);
        ExtentReportManager.getTest().log(Status.INFO, "Expected stations excel created: " + EXPECTED_FILE);

        // Step 7 (part 2): write actual list + comparison to excel
        List<String> expectedFromFile = ExcelUtils.readSingleColumn(EXPECTED_FILE, "Expected");
        ExcelUtils.writeComparison(COMPARISON_FILE, "Comparison", expectedFromFile, actualStations);
        ExtentReportManager.getTest().log(Status.PASS, "Comparison excel created: " + COMPARISON_FILE);

        // Soft verification: at least one expected station should be present in the dropdown
        boolean anyMatch = expectedFromFile.stream().anyMatch(
                exp -> actualStations.stream().anyMatch(a -> a.toLowerCase().contains(exp.toLowerCase())));
        Assert.assertTrue(anyMatch, "None of the expected stations were found in the dropdown");

        // Step 8: Dynamic date = today + 30 days (change offset any time for the interview)
        // "Sort on Date" only renders once a "To" station is also selected (assignment
        // screenshot shows To = "Mumbai Central" by default), so pick one first.
        home.ensureToStationSelected("BCT");
        ExtentReportManager.getTest().log(Status.INFO, "Ensured 'To' station selected (Mumbai Central)");

        int daysFromToday = Integer.parseInt(System.getProperty("dateOffset", "30"));
        home.enableSortOnDate();
        String selectedDate = home.selectDateDaysFromToday(daysFromToday);
        System.out.println("Selected date (today + " + daysFromToday + "): " + selectedDate);
        ExtentReportManager.getTest().log(Status.PASS,
                "Selected date dynamically (today + " + daysFromToday + " days): " + selectedDate);
    }
}
