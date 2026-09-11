package com.ricepot.tests;

import com.ricepot.base.BaseTest;
import com.ricepot.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class InvalidLoginTest extends BaseTest {

    @Test
    public void invalidCredentials() {
        try {
            LoginPage login = new LoginPage(driver);
            login.enterUsername("invalid@example.com");
            login.enterPassword("wrongpass");
            login.clickLogin();
            String err = login.getErrorMessage();
            Assert.assertTrue(err.length() > 0, "Expected error message for invalid credentials");
        } catch (Exception e) {
            throw e;
        }
    }

    @Test
    public void emptyFields() {
        try {
            LoginPage login = new LoginPage(driver);
            login.enterUsername("");
            login.enterPassword("");
            login.clickLogin();
            String err = login.getErrorMessage();
            Assert.assertTrue(err.length() > 0, "Expected error message for empty fields");
        } catch (Exception e) {
            throw e;
        }
    }

}
