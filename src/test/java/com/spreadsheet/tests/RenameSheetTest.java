package com.spreadsheet.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spreadsheet.pages.Grid;
import com.spreadsheet.pages.Notification;
import com.spreadsheet.pages.SheetTabs;
import java.util.List;
import org.junit.jupiter.api.Test;

class RenameSheetTest extends BaseTest {

    @Test
    void renameSheetWithRenameButton() {
        SheetTabs sheetTabs = new SheetTabs(driver);

        sheetTabs.renameSheet("Sheet1", "Budget 2026");

        assertEquals(List.of("Budget 2026"), sheetTabs.getSheetNames());
        assertEquals("Budget 2026", sheetTabs.getActiveSheetName());
    }

    @Test
    void renameSheetByDoubleClickingTab() {
        SheetTabs sheetTabs = new SheetTabs(driver);

        sheetTabs.renameSheetByDoubleClick("Sheet1", "Expenses");

        assertEquals(List.of("Expenses"), sheetTabs.getSheetNames());
    }

    @Test
    void renamedSheetKeepsItsData() {
        SheetTabs sheetTabs = new SheetTabs(driver);
        Grid grid = new Grid(driver);
        grid.enterValue("B2", "42");

        sheetTabs.renameSheet("Sheet1", "Answers");

        assertEquals("42", grid.getCellText("B2"));
    }

    @Test
    void emptyNameIsRejected() {
        SheetTabs sheetTabs = new SheetTabs(driver);
        Notification notification = new Notification(driver);

        sheetTabs.typeNewName("Sheet1", "");

        assertTrue(notification.isError());
        assertEquals("Sheet name cannot be empty.", notification.getMessage());
        assertTrue(sheetTabs.isRenameFieldOpen(), "the user can still correct the name");

        sheetTabs.cancelRename();
        assertEquals(List.of("Sheet1"), sheetTabs.getSheetNames());
    }

    @Test
    void duplicateNameIsRejected() {
        SheetTabs sheetTabs = new SheetTabs(driver);
        Notification notification = new Notification(driver);
        sheetTabs.addSheet();

        // Names are compared ignoring upper/lower case, so "sheet1" clashes with "Sheet1".
        sheetTabs.typeNewName("Sheet2", "sheet1");

        assertTrue(notification.isError());
        assertEquals("A sheet named \"sheet1\" already exists.", notification.getMessage());

        sheetTabs.cancelRename();
        assertEquals(List.of("Sheet1", "Sheet2"), sheetTabs.getSheetNames());
    }

    @Test
    void nameWithForbiddenCharacterIsRejected() {
        SheetTabs sheetTabs = new SheetTabs(driver);
        Notification notification = new Notification(driver);

        sheetTabs.typeNewName("Sheet1", "Q1/Q2");

        assertTrue(notification.isError());
        assertTrue(notification.getMessage().startsWith("Sheet name cannot contain"));

        sheetTabs.cancelRename();
        assertEquals(List.of("Sheet1"), sheetTabs.getSheetNames());
    }
}
