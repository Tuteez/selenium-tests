package com.spreadsheet.data;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds a CSV file for import tests.
 *
 * <pre>{@code
 * Path file = new CsvFileBuilder()
 *         .named("fruit.csv")
 *         .row("Name", "Amount")
 *         .row("Apples", "3")
 *         .writeTo(folder);
 * }</pre>
 */
public class CsvFileBuilder {

    private String fileName = "data.csv";
    private String lineSeparator = "\r\n";
    private final List<String> lines = new ArrayList<>();

    public CsvFileBuilder named(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public CsvFileBuilder row(String... values) {
        List<String> fields = new ArrayList<>();
        for (String value : values) {
            fields.add(toCsvField(value));
        }
        lines.add(String.join(",", fields));
        return this;
    }

    /** Use "\n" for Unix line endings. The default is "\r\n", the CSV standard. */
    public CsvFileBuilder lineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
        return this;
    }

    /** The CSV text, without writing a file. */
    public String build() {
        return String.join(lineSeparator, lines);
    }

    /** Writes the CSV file into the given folder and returns its path. */
    public Path writeTo(Path folder) {
        try {
            return Files.writeString(folder.resolve(fileName), build());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write " + fileName, e);
        }
    }

    /** Values containing a comma, quote or line break must be wrapped in quotes; inner quotes are doubled. */
    private static String toCsvField(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
