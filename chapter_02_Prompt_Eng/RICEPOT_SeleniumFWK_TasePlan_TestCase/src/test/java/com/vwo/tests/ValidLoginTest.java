package com.vwo.tests;

import com.vwo.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ValidLoginTest extends BaseTest {

    @Test(priority = 1)
    public void uiElementsPresence() {
        Assert.assertTrue(loginPage.isRememberMeDisplayed());
    }

    @Test(priority = 2)
    public void validLogin() {
        String user = config.getUsername();
        String pass = config.getPassword();
        Assert.assertNotNull(user);
        Assert.assertNotNull(pass);
        loginPage.doLogin(user, pass);
        int timeout = config.getTimeout();
        boolean landed = false;
        try {
            landed = com.vwo.utils.WaitUtils.waitForVisibility(driver, driver.findElement(org.openqa.selenium.By.xpath("//header | //div[contains(.,'dashboard') or contains(.,'Dashboard')]")), timeout) != null;
        } catch (Exception e) {
            landed = false;
        }
        Assert.assertTrue(landed, "Landing page not detected after login");
    }
}
