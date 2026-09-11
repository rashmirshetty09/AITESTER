package com.ricepot.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScreenshotUtil {

    public static void capture(WebDriver driver, String name) {
        try {
            if (driver == null) return;
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            Path targetDir = Paths.get("target", "screenshots");
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(name + ".png");
            Files.copy(src.toPath(), target);
        } catch (Exception ignored) {
        }
    }

}
