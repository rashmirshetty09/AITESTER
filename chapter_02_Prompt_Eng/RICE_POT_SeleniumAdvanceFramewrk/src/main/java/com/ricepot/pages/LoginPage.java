package com.ricepot.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.ricepot.utils.WaitUtils;

public class LoginPage {

    private WebDriver driver;

    @FindBy(xpath = "//input[@id='username']")
    WebElement username;

    @FindBy(xpath = "//input[@id='password']")
    WebElement password;

    @FindBy(xpath = "//input[@id='Login']")
    WebElement loginButton;

    @FindBy(xpath = "//input[@id='rememberUn']")
    WebElement rememberMe;

    @FindBy(xpath = "//div[@id='error'] | //div[contains(@class,'error') and string-length(normalize-space(.))>0]")
    WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void enterUsername(String user) {
        try {
            try {
                org.openqa.selenium.WebElement el = driver.findElement(org.openqa.selenium.By.id("username"));
                el.clear();
                el.sendKeys(user);
                return;
            } catch (Exception ignored) {
            }
            org.openqa.selenium.WebElement el = WaitUtils.waitForAnyVisible(driver, 15,
                    "//input[@id='username']",
                    "//input[@name='username']",
                    "//input[contains(@placeholder,'Username') or contains(@placeholder,'Email')]",
                    "//input[contains(@type,'email')]",
                    "//input[contains(@aria-label,'username') or contains(@aria-label,'Email']");
            el.clear();
            el.sendKeys(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter username", e);
        }
    }

    public void enterPassword(String pass) {
        try {
            try {
                org.openqa.selenium.WebElement el = driver.findElement(org.openqa.selenium.By.id("password"));
                el.clear();
                el.sendKeys(pass);
                return;
            } catch (Exception ignored) {
            }
            org.openqa.selenium.WebElement el = WaitUtils.waitForAnyVisible(driver, 15,
                    "//input[@id='password']",
                    "//input[@name='pw']",
                    "//input[@type='password']");
            el.clear();
            el.sendKeys(pass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter password", e);
        }
    }

    public void toggleRememberMe() {
        try {
            org.openqa.selenium.WebElement el = null;
            try {
                el = WaitUtils.waitForAnyVisible(driver, 8,
                        "//input[@id='rememberUn']",
                        "//input[@name='remember']",
                        "//label[contains(.,'Remember')]/input");
            } catch (Exception ignored) {
            }
            if (el != null) {
                WaitUtils.waitForClickable(driver, el, 5);
                el.click();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to toggle remember me", e);
        }
    }

    public void clickLogin() {
        try {
            org.openqa.selenium.WebElement el = WaitUtils.waitForAnyVisible(driver, 15,
                    "//input[@id='Login']",
                    "//button[@id='Login']",
                    "//button[contains(., 'Log In') or contains(., 'Log in') or contains(., 'Sign In')]",
                    "//input[@type='submit']");
            WaitUtils.waitForClickable(driver, el, 10);
            el.click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click login", e);
        }
    }

    public String getErrorMessage() {
        try {
            org.openqa.selenium.WebElement el = WaitUtils.waitForAnyVisible(driver, 8,
                    "//div[@id='error']",
                    "//div[contains(@class,'error')]",
                    "//div[contains(@class,'message') and string-length(normalize-space(.))>0]");
            return el.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isLoggedIn() {
        try {
            return driver.getCurrentUrl().toLowerCase().contains("salesforce");
        } catch (Exception e) {
            return false;
        }
    }

}
