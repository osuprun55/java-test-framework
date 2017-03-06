package com.template.autoqa.core.web.selenium;


import com.velti.template.core.exceptions.TestInterruptException;
import com.velti.template.core.web.elements.BaseWebElement;
import com.velti.template.utils.GeneralUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public class BaseWebDriver implements WebDriver {
    private final Logger logger = LoggerFactory.getLogger(BaseWebDriver.class);

    private static final long TIMEOUT_MAX_REQUEST_TIME = 2 * 60 * 1000;
    private static final String HTMLUNIT = "htmlunit";
    private static final String FIREFOX = "firefox";
    private static final String IEXPLORER = "iexplorer";
    private static final String CHROME = "chrome";
    private static final String MAXIMIZE_BROWSER_WINDOW = "if (window.screen) { window.moveTo(0, 0); window.resizeTo(window.screen.availWidth, window.screen.availHeight); }";

    private static final BaseWebDriver instance = new BaseWebDriver();
    protected WebDriver webDriver;

    public static BaseWebDriver getInstance() {
        return instance;
    }

    private BaseWebDriver() {
        // do nothing
    }

    /**
     * Returns an instance of WebDriver
     *
     * @return an instance of WebDriver
     */
    public WebDriver getWebDriver() {
        return webDriver;
    }

    /**
     * Returns an instance of BaseWebElement built from provided parameters
     *
     * @return an instance of BaseWebElement
     */
    public BaseWebElement getWebElement(By by) {
        return new BaseWebElement(this, by);
    }

    /**
     * Returns an instance of BaseWebElement built from provided parameters
     *
     * @return an instance of BaseWebElement
     */
    public BaseWebElement getWebElement(By by, BaseWebElement parent, int index) {
        return new BaseWebElement(this, by, parent, index);
    }

    /**
     * Returns an instance of BaseWebElement built from provided parameters
     *
     * @return an instance of BaseWebElement
     */
    public BaseWebElement getWebElement(By by, int index) {
        return new BaseWebElement(this, by, index);
    }

    /**
     * Starts the browser
     */
    public void startBrowser(String browserType) {
        logger.debug("'{}' browser is opening...", browserType);
        if (HTMLUNIT.equals(browserType)) {
            webDriver = new HtmlUnitDriver();
            ((HtmlUnitDriver) webDriver).setJavascriptEnabled(true);
        } else if (FIREFOX.equals(browserType)) {
            webDriver = new FirefoxDriver();
            executeJavascript(MAXIMIZE_BROWSER_WINDOW);
        } else if (IEXPLORER.equals(browserType)) {
            DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
            ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            ieCapabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
            webDriver = new InternetExplorerDriver(ieCapabilities);
        } else if (CHROME.equals(browserType)) {
            webDriver = new ChromeDriver();
        } else {
            throw new TestInterruptException(
                    String.format("You must define webdriver type, '%s' is a wrong webdriver type", browserType));
        }
        logger.debug("Browser has opened");
    }

    /**
     * Switch between different browser windows if multiply opened
     *
     * @param handle
     */
    public void switchToWindow(String handle) {
        // TODO: implement it
    }

    /**
     * Switching between frames
     *
     * @param handle
     */
    public void switchToFrame(String handle) {
        // TODO: implement it
    }

    /**
     * Refreshes current page
     */
    public void refresh() {
        // TODO: implement it
    }

    /**
     * Performs navigation to the previous page
     */
    public void back() {
        webDriver.navigate().back();
    }

    /**
     * Executes the javascript and returns the result of this execution
     *
     * @return result of javascript execution
     */
    public Object executeJavascript(String script) {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        return js.executeScript(script);
    }

    /**
     * Waits when the ajax interaction finishes if it is.
     */
    public void sync() {
        // let's give a chance for WebUI to start ajax-request in case it's a bit slow
        GeneralUtils.sleep(100);

        logger.trace("sync: start waiting");
        long finishTime = System.currentTimeMillis() + TIMEOUT_MAX_REQUEST_TIME;
        while (System.currentTimeMillis() <= finishTime) {
            Object status = executeJavascript("try { return isAjax() } catch(err) { return null }");
            boolean isLoading = status != null && status.toString().equals("true");
            if (!isLoading) {
                logger.trace("sync: finished");
                return;
            }
            logger.trace("sync: sleeping (status ='{}')", status);
            GeneralUtils.sleep(200);
        }
    }

    public void get(String url) {
        webDriver.get(url);
    }

    public String getCurrentUrl() {
        return webDriver.getCurrentUrl();
    }

    public String getTitle() {
        return webDriver.getTitle();
    }

    public List<WebElement> findElements(By by) {
        return webDriver.findElements(by);
    }

    public WebElement findElement(By by) {
        return webDriver.findElement(by);
    }

    public String getPageSource() {
        return webDriver.getPageSource();
    }

    public void close() {
        webDriver.close();
    }

    public void quit() {
        webDriver.quit();
    }

    public Set<String> getWindowHandles() {
        return webDriver.getWindowHandles();
    }

    public String getWindowHandle() {
        return webDriver.getWindowHandle();
    }

    public TargetLocator switchTo() {
        return webDriver.switchTo();
    }

    public Navigation navigate() {
        return webDriver.navigate();
    }

    public Options manage() {
        return webDriver.manage();
    }
}
