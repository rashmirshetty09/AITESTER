package com.ricepot.tests;

import com.ricepot.base.BaseTest;
import com.ricepot.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ValidLoginTest extends BaseTest {

    @Test
    public void validLogin() {
        try {
            LoginPage login = new LoginPage(driver);
            String user = System.getProperty("username", config.getProperty("username", ""));
            String pass = System.getProperty("password", config.getProperty("password", ""));
            login.enterUsername(user);
            login.enterPassword(pass);
            login.clickLogin();
            boolean loggedIn = login.isLoggedIn();
            Assert.assertTrue(loggedIn, "Expected user to be logged in");
        } catch (Exception e) {
            throw e;
        }
    }

}
