package com.velti.template.tests.web;

import com.velti.template.core.web.pages.BaseWebPage;
import com.velti.template.core.web.selenium.BaseWebDriver;
import com.velti.template.utils.Config;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertTrue;


public class BaseWebTest {
    private final Logger logger = LoggerFactory.getLogger(BaseWebTest.class);
    private final Config config = Config.getInstance();

    protected final String SERVER_URL = config.get("web_link");
    protected final String BROWSER_TYPE = config.get("web_driver");
    protected final BaseWebDriver driver;


    public BaseWebTest() {
        driver = BaseWebDriver.getInstance();
    }

    private void initializePages() {
    }

    @BeforeClass
    public void beforeClass() {
        driver.startBrowser(BROWSER_TYPE);
        driver.get(SERVER_URL);
        initializePages();
    }

    @BeforeMethod
    public void beforeMethod() {
    }

    @AfterMethod(alwaysRun = true)
    public void takeScreenshot(ITestResult result, ITestContext context) throws IOException {
        String file = "screenshots/" + result.getName() + ".png";

        // output gets lost without this
        Reporter.setCurrentTestResult(result);
        if (result.isSuccess())
            return;
        File f = ((TakesScreenshot) driver.getWebDriver()).getScreenshotAs(OutputType.FILE);
        File outputDir = new File((context.getOutputDirectory()));
        File saved = new File(outputDir.getParent(), file);
        FileUtils.copyFile(f, saved);
        // post html code to ReportNG with a reference to screenshot
        Reporter.log("<a href=\"../" + file + "\"> <img src=\"../" + file + "\" width='320px'></a>");
        Reporter.setCurrentTestResult(null);
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }

    /**
     * Asserts that provided page is opened in the browser
     *
     * @param currentPage
     */
    protected static void assertCurrentPage(BaseWebPage currentPage) {
        assertTrue(currentPage.isCurrent(), String.format("The wrong page is opened, expected page is '%s'", currentPage));
    }
}
