package com.spreadsheet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** The bar above the grid: the name box (e.g. "B5") and the formula input (e.g. "=SUM(B2:B4)"). */
public class FormulaBar extends BasePage {

    private final By formulaInput = By.cssSelector("input[aria-label='Formula bar']");

    public FormulaBar(WebDriver driver) {
        super(driver);
    }

    /** What is really stored in the selected cell: the formula itself, not its result. */
    public String getContent() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(formulaInput)).getDomProperty("value");
    }
}
