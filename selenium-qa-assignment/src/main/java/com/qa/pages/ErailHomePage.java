package com.qa.pages;

import com.qa.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Page Object for https://erail.in/ home page (Use Case 1).
 */
public class ErailHomePage extends BasePage {

    // ---------- Locators ----------
    private final By fromField = By.id("txtStationFrom");
    private final By autoSuggestList = By.cssSelector("div.autocomplete div");
    private final By sortOnDateCheckbox = By.id("chkSortOnDate");
    private final By dateField = By.id("txtDate");
    // Calendar: each month is rendered as a table; header cell shows e.g. "Sep-22"
    private final By calendarMonthTables = By.cssSelector("div#divCalendar table");

    public ErailHomePage(WebDriver driver) {
        super(driver);
    }

    // ---------- Actions ----------

    /** Step 2 + 3: Click on From field and clear existing data. */
    public void clickAndClearFromField() {
        WebElement from = waitForClickable(fromField);
        from.click();
        // erail is a JS-heavy field; CTRL+A + DELETE is more reliable than clear()
        from.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        from.sendKeys(Keys.DELETE);
    }

    /** Step 4: Type search text (e.g. "DEL") to open the suggestion dropdown. */
    public void typeInFromField(String text) {
        WebElement from = waitForVisible(fromField);
        for (char c : text.toCharArray()) {          // type char-by-char so JS suggestions fire
            from.sendKeys(String.valueOf(c));
        }
    }

    /** Step 7: Get all station names currently shown in the dropdown. */
    public List<String> getDropdownStationNames() {
        List<WebElement> suggestions = waitForAllVisible(autoSuggestList);
        List<String> names = new ArrayList<>();
        for (WebElement e : suggestions) {
            String txt = e.getText().trim();
            if (!txt.isEmpty()) {
                names.add(txt);
            }
        }
        return names;
    }

    /** Step 5: Select the station at the given position (1-based) and return its name. */
    public String selectStationAtPosition(int position) {
        List<WebElement> suggestions = waitForAllVisible(autoSuggestList);
        if (suggestions.size() < position) {
            throw new IllegalStateException(
                    "Dropdown has only " + suggestions.size() + " options, cannot pick position " + position);
        }
        WebElement target = suggestions.get(position - 1);
        String stationName = target.getText().trim();
        target.click();
        return stationName;
    }

    /** Make sure "Sort on Date" checkbox is checked. */
    public void enableSortOnDate() {
        WebElement checkbox = waitForClickable(sortOnDateCheckbox);
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
    }

    /**
     * Step 8: Select a date that is {@code daysFromToday} days from the current date
     * using the calendar widget. Fully dynamic - works for any offset.
     *
     * @return the selected date as dd-MMM-yy (e.g. 03-Sep-22) for logging/reporting.
     */
    public String selectDateDaysFromToday(int daysFromToday) {
        LocalDate targetDate = LocalDate.now().plusDays(daysFromToday);
        String monthHeader = targetDate.format(DateTimeFormatter.ofPattern("MMM-yy", Locale.ENGLISH)); // e.g. Sep-22
        String day = String.valueOf(targetDate.getDayOfMonth());

        click(dateField); // opens the calendar (3 months visible)

        List<WebElement> monthTables = waitForAllVisible(calendarMonthTables);
        for (WebElement monthTable : monthTables) {
            if (monthTable.getText().contains(monthHeader)) {
                List<WebElement> dayCells = monthTable.findElements(By.xpath(".//td"));
                for (WebElement cell : dayCells) {
                    if (cell.getText().trim().equals(day)) {
                        cell.click();
                        return targetDate.format(DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH));
                    }
                }
            }
        }
        throw new IllegalStateException("Could not select date " + monthHeader + " " + day + " from calendar");
    }

    /** Read back the value of the date field for verification. */
    public String getDateFieldValue() {
        return waitForVisible(dateField).getAttribute("value");
    }
}
