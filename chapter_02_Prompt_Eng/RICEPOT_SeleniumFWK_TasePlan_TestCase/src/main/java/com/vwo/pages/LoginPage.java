package com.vwo.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.vwo.utils.WaitUtils;

public class LoginPage {
    private WebDriver driver;
    private int timeout;

    @FindBy(xpath = "//input[contains(@placeholder,'Email') or contains(@type,'email')]")
    WebElement username;

    @FindBy(xpath = "//input[contains(@placeholder,'Password') or contains(@type,'password')]")
    WebElement password;

    @FindBy(xpath = "//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'log in') or contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'login') or @type='submit']")
    WebElement loginButton;

    @FindBy(xpath = "//input[@type='checkbox' and (contains(.,'remember') or contains(@aria-label,'Remember') or contains(@id,'remember'))]")
    WebElement rememberMe;

    @FindBy(xpath = "//div[contains(@role,'alert') or contains(@class,'error') or contains(.,'invalid')]")
    WebElement errorMessage;

    public LoginPage(WebDriver driver, int timeout) {
        this.driver = driver;
        this.timeout = timeout;
        PageFactory.initElements(driver, this);
    }

    public void enterUsername(String user) {
        try {
            WaitUtils.waitForVisibility(driver, username, timeout).clear();
            username.sendKeys(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void enterPassword(String pass) {
        try {
            WaitUtils.waitForVisibility(driver, password, timeout).clear();
            password.sendKeys(pass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clickLogin() {
        try {
            WaitUtils.waitForClickable(driver, loginButton, timeout).click();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void doLogin(String user, String pass) {
        enterUsername(user);
        enterPassword(pass);
        clickLogin();
    }

    public String getErrorText() {
        try {
            return WaitUtils.waitForVisibility(driver, errorMessage, timeout).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isRememberMeDisplayed() {
        try {
            return WaitUtils.waitForVisibility(driver, rememberMe, timeout).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
