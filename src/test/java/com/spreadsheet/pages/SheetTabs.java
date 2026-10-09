package com.spreadsheet.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** The row of sheet tabs at the bottom of the page. */
public class SheetTabs extends BasePage {

    private final By allTabs = By.cssSelector("[role='tab']");
    private final By activeTab = By.cssSelector("[role='tab'][aria-selected='true']");
    private final By addSheetButton = By.cssSelector("button[aria-label='Add sheet']");
    private final By sheetNameInput = By.cssSelector("input[aria-label='Sheet name']");

    public SheetTabs(WebDriver driver) {
        super(driver);
    }

    private By tabNamed(String name) {
        return By.xpath("//button[@role='tab' and text()='" + name + "']");
    }

    private By renameButtonFor(String name) {
        return By.cssSelector("button[aria-label='Rename " + name + "']");
    }

    public List<String> getSheetNames() {
        return driver.findElements(allTabs).stream().map(WebElement::getText).toList();
    }

    public String getActiveSheetName() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(activeTab)).getText();
    }

    /** Clicks "+" and waits until the new tab has appeared. */
    public void addSheet() {
        int tabsBefore = driver.findElements(allTabs).size();
        wait.until(ExpectedConditions.elementToBeClickable(addSheetButton)).click();
        wait.until(ExpectedConditions.numberOfElementsToBe(allTabs, tabsBefore + 1));
    }

    public void selectSheet(String name) {
        wait.until(ExpectedConditions.elementToBeClickable(tabNamed(name))).click();
        wait.until(ExpectedConditions.attributeToBe(tabNamed(name), "aria-selected", "true"));
    }

    /** Renames a sheet using its ✎ button and waits until the tab shows the new name. */
    public void renameSheet(String currentName, String newName) {
        typeNewName(currentName, newName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(tabNamed(newName)));
    }

    /** Renames a sheet by double-clicking its tab and waits until the tab shows the new name. */
    public void renameSheetByDoubleClick(String currentName, String newName) {
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(tabNamed(currentName)));
        new Actions(driver).doubleClick(tab).perform();
        submitName(newName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(tabNamed(newName)));
    }

    /**
     * Clicks ✎, types a name and presses Enter, without waiting for the result.
     * Use this when the app is expected to reject the name.
     */
    public void typeNewName(String currentName, String newName) {
        wait.until(ExpectedConditions.elementToBeClickable(renameButtonFor(currentName))).click();
        submitName(newName);
    }

    public boolean isRenameFieldOpen() {
        return !driver.findElements(sheetNameInput).isEmpty();
    }

    /** Presses Escape in the rename field to cancel renaming. */
    public void cancelRename() {
        driver.findElement(sheetNameInput).sendKeys(Keys.ESCAPE);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(sheetNameInput));
    }

    private void submitName(String name) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(sheetNameInput));
        clearAndType(input, name);
        input.sendKeys(Keys.ENTER);
    }
}
