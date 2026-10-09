package com.spreadsheet.pages;

import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** The grid of cells for the active sheet. Cells are addressed like "A1" or "B3". */
public class Grid extends BasePage {

    private final By nameBox = By.cssSelector("[aria-label='Selected cell']");
    private final By cellEditor = By.cssSelector("input.cell-editor");

    public Grid(WebDriver driver) {
        super(driver);
    }

    private By cell(String cellRef) {
        return By.cssSelector("td[data-cell='" + cellRef + "']");
    }

    /** Waits until the grid has been drawn, i.e. the app has finished loading. */
    public void waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(cell("A1")));
    }

    /** Clicks a cell and waits until the name box shows it is selected. */
    public void selectCell(String cellRef) {
        wait.until(ExpectedConditions.elementToBeClickable(cell(cellRef))).click();
        wait.until(ExpectedConditions.textToBe(nameBox, cellRef));
    }

    /**
     * Types a value or formula into a cell, like a user would: click the cell, type, press Enter.
     * Typing into a selected cell replaces what was there before.
     */
    public void enterValue(String cellRef, String value) {
        selectCell(cellRef);
        new Actions(driver).sendKeys(value).sendKeys(Keys.ENTER).perform();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(cellEditor));
    }

    /** Enters many cells at once, e.g. data created with SheetDataBuilder. */
    public void enterValues(Map<String, String> cells) {
        for (Map.Entry<String, String> entry : cells.entrySet()) {
            enterValue(entry.getKey(), entry.getValue());
        }
    }

    /** The text shown in the cell. For a formula this is the calculated result. */
    public String getCellText(String cellRef) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cell(cellRef))).getText();
    }
}
