package com.ricepot.base;

import com.ricepot.core.DriverFactory;
import com.ricepot.utils.ScreenshotUtil;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

import java.io.InputStream;
import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

public class BaseTest {

    protected WebDriver driver;
    protected Properties config = new Properties();

    @BeforeTest(alwaysRun = true)
    public void beforeTest() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                config.load(is);
            }
        } catch (Exception ignored) {
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        String browser = System.getProperty("browser", config.getProperty("browser", "chrome"));
        DriverFactory.initDriver(browser);
        driver = DriverFactory.getDriver();
        String url = System.getProperty("env.url", config.getProperty("env.url"));
        if (url != null) {
            driver.get(url);
            try {
                String src = driver.getPageSource();
                String fn = "target/pagesource_" + Instant.now().toEpochMilli() + ".html";
                Files.createDirectories(Paths.get("target"));
                Files.write(Paths.get(fn), src.getBytes());
            } catch (Exception ignored) {
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (!result.isSuccess()) {
            try {
                ScreenshotUtil.capture(driver, result.getName());
            } catch (Exception ignored) {
            }
        }
        DriverFactory.quitDriver();
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {
    }

}
