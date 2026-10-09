package com.spreadsheet.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spreadsheet.data.CsvFileBuilder;
import com.spreadsheet.pages.FormulaBar;
import com.spreadsheet.pages.Grid;
import com.spreadsheet.pages.Notification;
import com.spreadsheet.pages.SheetTabs;
import com.spreadsheet.pages.Toolbar;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CsvImportTest extends BaseTest {

    // JUnit creates an empty temporary folder for each test and deletes it afterwards.
    @TempDir
    Path tempFolder;

    @Test
    void importCreatesNewSheetNamedAfterTheFile() {
        Path csvFile = new CsvFileBuilder()
                .named("products.csv")
                .row("Product", "Quantity")
                .row("Apples", "3")
                .row("Pears", "4")
                .writeTo(tempFolder);
        Toolbar toolbar = new Toolbar(driver);
        Notification notification = new Notification(driver);
        SheetTabs sheetTabs = new SheetTabs(driver);
        Grid grid = new Grid(driver);

        toolbar.importCsv(csvFile);

        assertFalse(notification.isError());
        assertEquals("Imported \"products.csv\" (3 rows).", notification.getMessage());
        assertEquals(List.of("Sheet1", "products"), sheetTabs.getSheetNames());
        assertEquals("products", sheetTabs.getActiveSheetName());
        assertEquals("Product", grid.getCellText("A1"));
        assertEquals("Pears", grid.getCellText("A3"));
        assertEquals("4", grid.getCellText("B3"));
    }

    @Test
    void importHandlesQuotedValues() {
        Path csvFile = new CsvFileBuilder()
                .named("notes.csv")
                .row("Fruit", "Note")
                .row("Apples", "red, crisp")
                .row("Pears", "say \"hi\"")
                .writeTo(tempFolder);
        Toolbar toolbar = new Toolbar(driver);
        Notification notification = new Notification(driver);
        Grid grid = new Grid(driver);

        toolbar.importCsv(csvFile);
        notification.getMessage(); // wait until the import has finished

        assertEquals("red, crisp", grid.getCellText("B2"));
        assertEquals("say \"hi\"", grid.getCellText("B3"));
    }

    @Test
    void importedFormulasAreCalculated() {
        Path csvFile = new CsvFileBuilder()
                .named("totals.csv")
                .row("Item", "Amount")
                .row("Apples", "3")
                .row("Pears", "4")
                .row("Total", "=SUM(B2:B3)")
                .writeTo(tempFolder);
        Toolbar toolbar = new Toolbar(driver);
        Notification notification = new Notification(driver);
        Grid grid = new Grid(driver);
        FormulaBar formulaBar = new FormulaBar(driver);

        toolbar.importCsv(csvFile);
        notification.getMessage(); // wait until the import has finished

        assertEquals("7", grid.getCellText("B4"));
        grid.selectCell("B4");
        assertEquals("=SUM(B2:B3)", formulaBar.getContent());
    }

    @Test
    void importAcceptsUnixLineEndings() {
        Path csvFile = new CsvFileBuilder()
                .named("scores.csv")
                .lineSeparator("\n")
                .row("Player", "Score")
                .row("Ann", "12")
                .row("Bob", "30")
                .writeTo(tempFolder);
        Toolbar toolbar = new Toolbar(driver);
        Notification notification = new Notification(driver);
        Grid grid = new Grid(driver);

        toolbar.importCsv(csvFile);

        assertEquals("Imported \"scores.csv\" (3 rows).", notification.getMessage());
        assertEquals("Bob", grid.getCellText("A3"));
        assertEquals("30", grid.getCellText("B3"));
    }

    @Test
    void importWithExistingSheetNameGetsUniqueName() {
        Path csvFile = new CsvFileBuilder()
                .named("Sheet1.csv")
                .row("a", "b")
                .writeTo(tempFolder);
        Toolbar toolbar = new Toolbar(driver);
        Notification notification = new Notification(driver);
        SheetTabs sheetTabs = new SheetTabs(driver);

        toolbar.importCsv(csvFile);
        notification.getMessage(); // wait until the import has finished

        assertEquals(List.of("Sheet1", "Sheet1 (2)"), sheetTabs.getSheetNames());
    }

    @Test
    void emptyFileShowsAnError() {
        Path csvFile = new CsvFileBuilder()
                .named("empty.csv")
                .writeTo(tempFolder);
        Toolbar toolbar = new Toolbar(driver);
        Notification notification = new Notification(driver);
        SheetTabs sheetTabs = new SheetTabs(driver);

        toolbar.importCsv(csvFile);

        assertTrue(notification.isError());
        assertEquals("\"empty.csv\" is empty.", notification.getMessage());
        assertEquals(List.of("Sheet1"), sheetTabs.getSheetNames());
    }
}
