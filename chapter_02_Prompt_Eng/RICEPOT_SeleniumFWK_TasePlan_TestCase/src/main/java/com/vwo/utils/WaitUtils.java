package com.vwo.utils;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitUtils {
    public static WebElement waitForVisibility(WebDriver driver, WebElement element, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.visibilityOf(element));
    }

    public static WebElement waitForClickable(WebDriver driver, WebElement element, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.elementToBeClickable(element));
    }

    public static boolean waitForText(WebDriver driver, WebElement element, String text, int timeout) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeout)).until(ExpectedConditions.textToBePresentInElement(element, text));
    }
}
