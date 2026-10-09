package com.spreadsheet.pages;

import java.nio.file.Path;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** The row of buttons above the grid (undo/redo, insert rows/columns, CSV import/export). */
public class Toolbar extends BasePage {

    private final By csvFileInput = By.cssSelector("input[data-testid='csv-file-input']");

    public Toolbar(WebDriver driver) {
        super(driver);
    }

    /**
     * Imports a CSV file.
     *
     * <p>Clicking "Import CSV" opens the operating system's file dialog, which Selenium cannot control.
     * Instead we type the file path straight into the hidden {@code <input type="file">} behind the button.
     * This is the standard way to upload files with Selenium.
     */
    public void importCsv(Path csvFile) {
        wait.until(ExpectedConditions.presenceOfElementLocated(csvFileInput))
                .sendKeys(csvFile.toAbsolutePath().toString());
    }
}
