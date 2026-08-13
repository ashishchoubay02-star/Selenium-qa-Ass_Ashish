# QA Selenium Assignment — Functional Testing

Selenium automation framework built with the **Page Object Model** using **Java, TestNG, Maven, Apache POI and Extent Reports**.

## Use Case 1 — erail.in
1. Open https://erail.in/ with WebDriver
2. Click the **From** field and clear it
3. Type **"DEL"** to open the auto-suggest dropdown
4. Select the station at the **4th position** and print it
5. Create an Excel file with expected station names (`ExpectedStations.xlsx`)
6. Capture the dropdown list, write it to Excel and compare with the expected names (`test-output/StationComparison.xlsx`)
7. Select **current date + 30 days** in "Sort on Date" from the calendar — fully **dynamic** (offset configurable with `-DdateOffset=30`)
   - *Implementation note:* the live site only renders the "Sort on Date" checkbox/calendar once a **To** station is also selected (see the assignment's own screenshot, where To = "Mumbai Central" by default). This isn't a separate numbered step in the brief, so the code selects a To station (`ensureToStationSelected`) right before this step, purely so the site's panel appears — no additional assertion or requirement was added.
8. Generate an **Extent report**

## Use Case 2 — OrangeHRM Login (Data-Driven)
1. Test valid & invalid logins on https://opensource-demo.orangehrmlive.com/web/index.php/auth/login
2. Test data read from `src/test/resources/testdata/LoginData.xlsx` via a TestNG **@DataProvider**
3. Extent report generated for all iterations

## Project Structure
```
selenium-qa-assignment
├── pom.xml
├── testng.xml
├── src
│   ├── main/java/com/qa
│   │   ├── base/BasePage.java              # common waits & actions
│   │   ├── pages/ErailHomePage.java        # UC1 page object
│   │   ├── pages/OrangeHRMLoginPage.java   # UC2 page object
│   │   └── utils/ExcelUtils.java           # Apache POI helper
│   │   └── utils/ExtentReportManager.java  # Extent singleton
│   └── test/java/com/qa
│       ├── listeners/ExtentTestListener.java
│       └── tests/BaseTest.java
│       └── tests/ErailTest.java
│       └── tests/OrangeHRMLoginTest.java
└── src/test/resources/testdata
    ├── ExpectedStations.xlsx
    └── LoginData.xlsx
```

## Prerequisites
- Java 11+
- Maven 3.6+
- Chrome browser (driver is auto-managed by WebDriverManager)

## How to Run
```bash
mvn clean test
```

Run with a different date offset (e.g. +45 days) for the erail calendar:
```bash
mvn clean test -DdateOffset=45
```

## Reports
- Extent HTML report: `test-output/ExtentReport_<timestamp>.html`
- Station comparison Excel: `test-output/StationComparison.xlsx`

## Notes
- erail.in is a JS-heavy site; if its DOM changes again, update the locators in `ErailHomePage.java`. As of this version the site uses `chkSelectDateOnly` for the Sort-on-Date checkbox (originally `chkSortOnDate` when this assignment was written), a date **button** inside `#tdDateFromTo` (originally a `txtDate` text input), and `div#divCalender` for the calendar (originally `divCalendar`).
- OrangeHRM demo credentials: `Admin / admin123` (already present in `LoginData.xlsx`).
