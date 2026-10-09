package com.spreadsheet.data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds the cell values for a test, in a readable way.
 *
 * <pre>{@code
 * Map<String, String> prices = new SheetDataBuilder()
 *         .cell("A1", "Item")
 *         .column("B", 2, "10", "20", "30")   // B2=10, B3=20, B4=30
 *         .build();
 * }</pre>
 */
public class SheetDataBuilder {

    // LinkedHashMap keeps the cells in the order they were added.
    private final Map<String, String> cells = new LinkedHashMap<>();

    public SheetDataBuilder cell(String cellRef, String value) {
        cells.put(cellRef, value);
        return this;
    }

    /** Fills a column downwards, e.g. column("B", 2, "10", "20") sets B2 and B3. */
    public SheetDataBuilder column(String column, int firstRow, String... values) {
        for (int i = 0; i < values.length; i++) {
            cell(column + (firstRow + i), values[i]);
        }
        return this;
    }

    public Map<String, String> build() {
        return new LinkedHashMap<>(cells);
    }
}
