package com.redheap.selenium;

import com.redheap.selenium.output.ScreenshotFile;
import com.redheap.selenium.pages.PageObject;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.RemoteWebDriver;

public class TestCaseBase<P extends PageObject> {

    private static RemoteWebDriver driver;

    private String url;
    private Class<? extends P> cls;

    public TestCaseBase(String url, Class<? extends P> cls) {
        this.url = url;
        this.cls = cls;
    }

    @BeforeClass
    public static void setUpBrowser() throws Exception {
        System.out.println("Starting Firefox...");
        FirefoxProfile profile = new FirefoxProfile();
        profile.setEnableNativeEvents(true); // needed for Mac OSX (default is non-native which doesn't work with ADF)
        profile.setPreference("app.update.enabled", false); // don't bother updating Firefox (takes too much time)
        driver = new FirefoxDriver(profile);
    }

    @AfterClass
    public static void tearDownBrowser() throws Exception {
        System.out.println("Quit firefox...");
        driver.quit();
    }

    @Before
    public void setupSession() {
        // clear session cookie before each test so we start with a clean session
        System.out.println("Clearing session cookie for " + this.getClass() + "...");
        driver.manage().deleteCookieNamed("JSESSIONID");
        // navigate to homepage
        System.out.println("Navigating to " + url + "...");
        driver.get(url);
    }

    protected P getPage() {
        try {
            return cls.getConstructor(WebDriver.class).newInstance(driver);
        } catch (Exception e) {
            throw new WebDriverException(e.getCause() != null ? e.getCause() : e);
        }
    }

    protected RemoteWebDriver getDriver() {
        return driver;
    }

    protected void dumpError(String name) {
        try {
            File shotFile = new File(name + "-fail.png");
            System.out.println("dumping error screenshot " + shotFile.getCanonicalPath());
            getDriver().getScreenshotAs(new ScreenshotFile(shotFile));
            File srcFile = new File(name + "-fail.txt");
            System.out.println("dumping error page source " + srcFile.getCanonicalPath());
            FileUtils.write(srcFile, getDriver().getPageSource());
        } catch (IOException e) {
            // suppress
        }

    }

}
