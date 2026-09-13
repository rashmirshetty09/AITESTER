package com.vwo.base;

import com.vwo.pages.LoginPage;
import com.vwo.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public class BaseTest {
    protected WebDriver driver;
    protected LoginPage loginPage;
    protected ConfigReader config = ConfigReader.getInstance();

    @BeforeTest(alwaysRun = true)
    public void setUp() {
        String browser = config.getBrowser();
        int timeout = config.getTimeout();
        if (browser == null || browser.isEmpty()) browser = "chrome";
        if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            if ("true".equalsIgnoreCase(config.get("headless"))) options.addArguments("--headless=new");
            driver = new ChromeDriver(options);
        }
        driver.manage().window().maximize();
        String url = config.getBaseUrl();
        if (url != null && !url.isEmpty()) driver.get(url);
        loginPage = new LoginPage(driver, timeout);
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
