package com.ricepot.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {

    public static void waitForVisible(WebDriver driver, WebElement element, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public static void waitForClickable(WebDriver driver, WebElement element, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public static void waitForText(WebDriver driver, WebElement element, String text, int seconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }

    public static org.openqa.selenium.WebElement waitForAnyVisible(WebDriver driver, int seconds, String... xpaths) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        for (String xp : xpaths) {
            try {
                org.openqa.selenium.By by = org.openqa.selenium.By.xpath(xp);
                // First wait for presence in DOM
                wait.until(ExpectedConditions.presenceOfElementLocated(by));
                // Then wait briefly for visibility/clickable
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(Math.max(2, Math.min(5, seconds))));
                try {
                    return shortWait.until(ExpectedConditions.elementToBeClickable(by));
                } catch (Exception e) {
                    // as fallback return the present element
                    return driver.findElement(by);
                }
            } catch (Exception ignored) {
            }
        }
        throw new org.openqa.selenium.TimeoutException("None of the provided xpaths became visible");
    }

}
