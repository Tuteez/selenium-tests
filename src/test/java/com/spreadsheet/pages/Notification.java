package com.spreadsheet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** The message banner the app shows after actions, e.g. "Imported ..." or an error. */
public class Notification extends BasePage {

    private final By banner = By.cssSelector(".notice");
    private final By bannerText = By.cssSelector(".notice span");

    public Notification(WebDriver driver) {
        super(driver);
    }

    /** Waits for the banner to appear and returns its text. */
    public String getMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(bannerText)).getText();
    }

    /** Error banners have role="alert"; information banners have role="status". */
    public boolean isError() {
        String role = wait.until(ExpectedConditions.visibilityOfElementLocated(banner)).getDomAttribute("role");
        return "alert".equals(role);
    }
}
