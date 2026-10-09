package com.spreadsheet.pages;

import com.spreadsheet.config.Config;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Parent class for all page objects: gives every page the driver and an explicit wait. */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Config.getTimeout());
    }

    /**
     * Replaces the text in an input field.
     *
     * <p>We don't use {@code element.clear()} because this app is built with React, and React does not
     * notice text removed by clear(). Pressing Backspace like a real user works reliably.
     */
    protected void clearAndType(WebElement input, String text) {
        String currentText = input.getDomProperty("value");
        input.sendKeys(Keys.END);
        for (int i = 0; i < currentText.length(); i++) {
            input.sendKeys(Keys.BACK_SPACE);
        }
        input.sendKeys(text);
    }
}
