package com.vwo.tests;

import com.vwo.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class InvalidLoginTest extends BaseTest {

    @DataProvider(name = "invalidCreds")
    public Object[][] invalidCreds() {
        return new Object[][]{
            {config.getUsername(), "wrongPass"},
            {"", config.getPassword()},
            {config.getUsername(), ""},
            {"", ""},
            {"user@@domain", "pass"}
        };
    }

    @Test(dataProvider = "invalidCreds")
    public void invalidLoginScenarios(String user, String pass) {
        loginPage.doLogin(user, pass);
        String err = loginPage.getErrorText();
        Assert.assertTrue(err != null && !err.isEmpty(), "Expected error message for invalid login");
    }
}
