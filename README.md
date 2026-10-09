# Spreadsheet Selenium Tests

A small Selenium framework for the spreadsheet app. It is written for **people learning Selenium**, so it favours plain, readable Java over clever abstractions.

Built with Java 21, Maven, Selenium 4, JUnit 5 and WebDriverManager, using the Page Object Model.

## Prerequisites

- JDK 21 or newer (`java -version`)
- Maven 3.9 or newer (`mvn -version`)
- Chrome, Firefox or Edge installed. WebDriverManager downloads the matching driver for you.
- The spreadsheet app running. From the repository root:

```bash
npm install
npm run dev        # serves http://localhost:5173
```

## Running the tests

```bash
cd selenium-tests
mvn test
```

Common variations:

```bash
mvn test -Dheadless=false                             # watch the browser
mvn test -Dbrowser=firefox                            # chrome | firefox | edge
mvn test -Dbase.url=http://localhost:4173             # app running somewhere else
mvn test -Dtest=RenameSheetTest                       # one test class
mvn test -Dtest=RenameSheetTest#emptyNameIsRejected   # one test method
```

You can also run any test class straight from your IDE.

## Configuration

Settings live in [`src/test/resources/config.properties`](src/test/resources/config.properties):

| Key               | Default                 | Meaning                              |
|-------------------|-------------------------|--------------------------------------|
| `base.url`        | `http://localhost:5173` | Address of the spreadsheet app       |
| `browser`         | `chrome`                | `chrome`, `firefox` or `edge`        |
| `headless`        | `true`                  | `false` shows the browser window     |
| `timeout.seconds` | `10`                    | How long explicit waits wait at most |

A `-Dkey=value` option on the command line takes priority over the file. [`Config.java`](src/test/java/com/spreadsheet/config/Config.java) is the only class that reads these values.

## Project structure

```
src/test/java/com/spreadsheet/
├── config/
│   └── Config.java              reads config.properties (or -D overrides)
├── pages/                       Page Objects: one class per part of the screen
│   ├── BasePage.java            shared driver + WebDriverWait
│   ├── Grid.java                cells: select, type, read
│   ├── FormulaBar.java          name box + formula input
│   ├── SheetTabs.java           add / select / rename sheets
│   ├── Toolbar.java             CSV import
│   └── Notification.java        success / error banner
├── data/                        Test data builders
│   ├── SheetDataBuilder.java    cell values to type into the grid
│   └── CsvFileBuilder.java      CSV files to import
└── tests/
    ├── BaseTest.java            opens and closes the browser
    ├── AddSheetTest.java
    ├── RenameSheetTest.java
    ├── FormulaCalculationTest.java
    └── CsvImportTest.java
src/test/resources/
├── config.properties
└── simplelogger.properties      keeps library logging quiet
```

## How it fits together

### 1. `BaseTest`: the browser lifecycle

Every test class `extends BaseTest`. JUnit then runs:

- **`@BeforeEach`**: create a `WebDriver` for the configured browser, set the window size, open `base.url`, and wait for the grid to appear.
- **`@AfterEach`**: `driver.quit()`.

So every test starts with a **fresh browser** and empty `localStorage`. Tests never affect each other.

### 2. Page Objects: *what* the page can do

A test should read like a user story: "add a sheet, type 10 in A1". The page object hides the *how* (locators, clicks, waits):

```java
public class SheetTabs extends BasePage {
    private final By allTabs = By.cssSelector("[role='tab']");
    private final By addSheetButton = By.cssSelector("button[aria-label='Add sheet']");

    public void addSheet() {
        int tabsBefore = driver.findElements(allTabs).size();
        wait.until(ExpectedConditions.elementToBeClickable(addSheetButton)).click();
        wait.until(ExpectedConditions.numberOfElementsToBe(allTabs, tabsBefore + 1));
    }
}
```

If the app's HTML changes, you fix the locator in **one** place, not in every test.

### 3. Tests: arrange, act, assert

Tests create the page objects they need with `new`:

```java
class AddSheetTest extends BaseTest {

    @Test
    void addingASheetCreatesAndSelectsIt() {
        SheetTabs sheetTabs = new SheetTabs(driver);   // arrange

        sheetTabs.addSheet();                          // act

        assertEquals(List.of("Sheet1", "Sheet2"), sheetTabs.getSheetNames());  // assert
        assertEquals("Sheet2", sheetTabs.getActiveSheetName());
    }
}
```

Page objects contain **no assertions**. They return values, and the test decides what is correct.

### 4. Test data builders

Builders make test data readable and keep it next to the test:

```java
Map<String, String> data = new SheetDataBuilder()
        .column("A", 1, "10", "20", "30")       // A1..A3
        .cell("A4", "=SUM(A1:A3)")
        .build();
grid.enterValues(data);

Path csvFile = new CsvFileBuilder()
        .named("budget.csv")
        .row("Item", "Cost")
        .row("Rent", "1200")
        .writeTo(tempFolder);                    // a JUnit @TempDir folder
toolbar.importCsv(csvFile);
```

## Explicit waits

The app updates the page asynchronously, so a test must **wait for the result** of an action before checking it. This framework uses only **explicit waits**:

```java
wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
wait.until(ExpectedConditions.textToBe(nameBox, "B2"));
```

`wait` is a `WebDriverWait` created in `BasePage` with `timeout.seconds`. It checks the condition repeatedly and continues as soon as it is true.

Rules followed here:

- **No `Thread.sleep`.** It is either too slow or not long enough.
- **No implicit waits.** Mixing implicit and explicit waits causes unpredictable timeouts.
- **Wait inside the page object** after each action (for example `addSheet()` waits for the new tab), so tests stay clean.

## Writing a new test

1. Create a class in `tests/` that `extends BaseTest`.
2. In each `@Test`, create the page objects you need: `new Grid(driver)`.
3. If you need a new interaction, add a method to the right page object, including a wait for its result.
4. Run it: `mvn test -Dtest=YourTest -Dheadless=false`.

## Gotchas worth knowing

- **`clear()` does not work on React inputs.** React does not see the change. `BasePage.clearAndType()` deletes the old text with the keyboard instead.
- **File upload:** the CSV input is hidden behind the Import button. Selenium uploads by calling `sendKeys(absolutePath)` on the `<input type="file">` directly. Do not click the button, because that opens the operating system's file dialog.
- **Prefer stable locators:** `aria-label`, `data-testid` and `data-cell` attributes, not CSS classes or element positions.
