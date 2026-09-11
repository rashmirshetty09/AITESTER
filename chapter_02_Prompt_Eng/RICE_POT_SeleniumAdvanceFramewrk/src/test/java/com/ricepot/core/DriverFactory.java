package com.ricepot.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static void initDriver(String browser) {
        if (browser == null) {
            browser = "chrome";
        }
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions co = new ChromeOptions();
                if (System.getProperty("headless", "false").equalsIgnoreCase("true")) {
                    co.addArguments("--headless=new");
                    co.addArguments("--disable-gpu");
                }
                driver.set(new ChromeDriver(co));
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions fo = new FirefoxOptions();
                if (System.getProperty("headless", "false").equalsIgnoreCase("true")) {
                    fo.addArguments("-headless");
                }
                driver.set(new FirefoxDriver(fo));
                break;
            default:
                WebDriverManager.chromedriver().setup();
                driver.set(new ChromeDriver());
        }
        driver.get().manage().window().maximize();
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        WebDriver drv = driver.get();
        if (drv != null) {
            try {
                drv.quit();
            } catch (Exception ignored) {
            }
            driver.remove();
        }
    }

}
