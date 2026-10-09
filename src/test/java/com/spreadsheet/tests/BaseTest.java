package com.spreadsheet.tests;

import com.spreadsheet.config.Config;
import com.spreadsheet.pages.Grid;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Every test class extends this class.
 *
 * <p>Before each test a new browser is started and the application is opened. After each test the browser
 * is closed. A new browser means empty localStorage, so every test starts with a fresh spreadsheet.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    void openBrowserAndApplication() {
        driver = createDriver();
        driver.manage().window().setSize(new Dimension(1440, 900));
        driver.get(Config.getBaseUrl());
        new Grid(driver).waitUntilLoaded();
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    /** WebDriverManager downloads the right driver (e.g. chromedriver) for the installed browser. */
    private WebDriver createDriver() {
        String browser = Config.getBrowser().toLowerCase();
        boolean headless = Config.isHeadless();

        switch (browser) {
            case "chrome": {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                return new ChromeDriver(options);
            }
            case "firefox": {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                return new FirefoxDriver(options);
            }
            case "edge": {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (headless) {
                    options.addArguments("--headless=new");
                }
                return new EdgeDriver(options);
            }
            default:
                throw new IllegalArgumentException("Unknown browser '" + browser + "'. Use chrome, firefox or edge.");
        }
    }
}
