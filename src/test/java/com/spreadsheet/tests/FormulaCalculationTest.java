package com.spreadsheet.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spreadsheet.data.SheetDataBuilder;
import com.spreadsheet.pages.FormulaBar;
import com.spreadsheet.pages.Grid;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FormulaCalculationTest extends BaseTest {

    /** A small price list: B2=10, B3=20, B4=30. */
    private Map<String, String> priceList() {
        return new SheetDataBuilder()
                .cell("A1", "Item")
                .cell("B1", "Price")
                .column("A", 2, "Apples", "Pears", "Plums")
                .column("B", 2, "10", "20", "30")
                .build();
    }

    @Test
    void sumAddsUpARange() {
        Grid grid = new Grid(driver);
        grid.enterValues(priceList());

        grid.enterValue("B5", "=SUM(B2:B4)");

        assertEquals("60", grid.getCellText("B5"));
    }

    @Test
    void avgCalculatesTheAverageOfARange() {
        Grid grid = new Grid(driver);
        grid.enterValues(priceList());

        grid.enterValue("B5", "=AVG(B2:B4)");

        assertEquals("20", grid.getCellText("B5"));
    }

    @Test
    void cellShowsResultWhileFormulaBarShowsFormula() {
        Grid grid = new Grid(driver);
        FormulaBar formulaBar = new FormulaBar(driver);
        grid.enterValues(priceList());

        grid.enterValue("B5", "=SUM(B2:B4)");
        grid.selectCell("B5");

        assertEquals("60", grid.getCellText("B5"));
        assertEquals("=SUM(B2:B4)", formulaBar.getContent());
    }

    @Test
    void formulasUpdateWhenAValueChanges() {
        Grid grid = new Grid(driver);
        grid.enterValues(priceList());
        grid.enterValue("B5", "=SUM(B2:B4)");
        grid.enterValue("B6", "=AVG(B2:B4)");

        grid.enterValue("B3", "50");

        assertEquals("90", grid.getCellText("B5"));
        assertEquals("30", grid.getCellText("B6"));
    }

    @Test
    void formulasCanBeCombined() {
        Grid grid = new Grid(driver);
        grid.enterValues(priceList());

        grid.enterValue("B5", "=SUM(B2:B4)/AVG(B2:B4)");

        assertEquals("3", grid.getCellText("B5"));
    }
}
