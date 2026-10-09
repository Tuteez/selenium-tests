package com.spreadsheet.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spreadsheet.pages.Grid;
import com.spreadsheet.pages.SheetTabs;
import java.util.List;
import org.junit.jupiter.api.Test;

class AddSheetTest extends BaseTest {

    @Test
    void addingASheetCreatesAndSelectsIt() {
        SheetTabs sheetTabs = new SheetTabs(driver);
        assertEquals(List.of("Sheet1"), sheetTabs.getSheetNames());

        sheetTabs.addSheet();

        assertEquals(List.of("Sheet1", "Sheet2"), sheetTabs.getSheetNames());
        assertEquals("Sheet2", sheetTabs.getActiveSheetName());
    }

    @Test
    void newSheetsAreNumberedInOrder() {
        SheetTabs sheetTabs = new SheetTabs(driver);

        sheetTabs.addSheet();
        sheetTabs.addSheet();
        sheetTabs.addSheet();

        assertEquals(List.of("Sheet1", "Sheet2", "Sheet3", "Sheet4"), sheetTabs.getSheetNames());
    }

    @Test
    void eachSheetKeepsItsOwnData() {
        SheetTabs sheetTabs = new SheetTabs(driver);
        Grid grid = new Grid(driver);

        grid.enterValue("A1", "first sheet");
        sheetTabs.addSheet();
        assertEquals("", grid.getCellText("A1"), "a new sheet starts empty");
        grid.enterValue("A1", "second sheet");

        sheetTabs.selectSheet("Sheet1");
        assertEquals("first sheet", grid.getCellText("A1"));

        sheetTabs.selectSheet("Sheet2");
        assertEquals("second sheet", grid.getCellText("A1"));
    }
}
