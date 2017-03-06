package com.template.autoqa.core.web.elements;

import com.velti.template.core.exceptions.TestInterruptException;
import com.velti.template.core.web.selenium.BaseWebDriver;
import com.velti.template.utils.GeneralUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class BaseWebElement implements WebElement {

    private final int TIMEOUT_WAIT_FOR_VISIBILITY = 5;
    private final int TIMEOUT_WAIT_FOR_ENABLE = 5;
    private final int TIMEOUT_WAIT_FOR_TEXT = 5;
    private final int TIMEOUT_WAIT_FOR_PRESENCE = 20;

    private final Logger logger = LoggerFactory.getLogger(BaseWebElement.class);

    private BaseWebDriver webDriver;
    private WebElement element;
    private By locator;

    private int elementIndex = 0;
    private BaseWebElement parentElement;

    /**
     * Default constructor
     */
    public BaseWebElement() {
        // do nothing
    }

    /**
     * Creates an instance using provided parameters
     *
     * @param webDriver BaseWebDriver object
     * @param locator   locator
     * @param parent    parent object
     * @param index     index
     */
    public BaseWebElement(BaseWebDriver webDriver, By locator, BaseWebElement parent, int index) {
        this.locator = locator;
        this.webDriver = webDriver;
        elementIndex = index;
        parentElement = parent;
    }

    /**
     * Creates an instance using provided parameters
     *
     * @param webDriver BaseWebDriver object
     * @param locator   locator
     * @param index     index
     */
    public BaseWebElement(BaseWebDriver webDriver, By locator, int index) {
        this.locator = locator;
        this.webDriver = webDriver;
        elementIndex = index;
    }

    /**
     * Creates an instance using provided parameters
     *
     * @param webDriver BaseWebDriver object
     * @param locator   locator
     */
    public BaseWebElement(BaseWebDriver webDriver, By locator) {
        this.locator = locator;
        this.webDriver = webDriver;
    }

    /**
     * Returns the web element.
     * An exception will be thrown if there is no appropriate element
     *
     * @return found element
     */
    private WebElement getElement() {
        waitForPresence();

        if (element == null) {
            throw new TestInterruptException(
                    String.format("There is no web element with '%s' locator and '%s' index", locator, elementIndex));
        }
        waitForVisibility();
        return element;
    }

    /**
     * Returns the web element.
     * <br/></><b>IMPORTANT:</b> as you may notice from the method name this method is quite specific.
     * It may return null if there is no appropriate element. Also you should call previously any other methods
     * on this object to search the appropriate web element. In most cases you don't need to use this method!
     *
     * @return found element
     */
    public WebElement getUnsafeWebElement() {
        return element;
    }

    /**
     * Performs the search of the element by specified locator and additional parameters.
     *
     * @return found element or null
     */
    private WebElement findSelf() {
        // wait for ajax request finishes
        webDriver.sync();

        List<WebElement> foundElements = (parentElement != null)
                ? parentElement.getElement().findElements(locator)
                : webDriver.findElements(locator);
        return (foundElements.size() > elementIndex) ? foundElements.get(elementIndex) : null;
    }

    private boolean waitForPresence() {
        return waitFor(true);
    }

    private boolean waitForAbsence() {
        return waitFor(false);
    }

    private boolean waitFor(boolean isPresence) {
        element = findSelf();
        // for some reason the web elements may appear on the page with some delay after complete page loading
        // (for now it's caused by downloading some javascript code). So if we haven't found the web element, we repeat
        // this attempt for some time
        long finishTime = System.currentTimeMillis() + TIMEOUT_WAIT_FOR_PRESENCE * 1000;
        while (System.currentTimeMillis() < finishTime) {
            if (isPresence == !(element == null)) {
                return true;
            }
            GeneralUtils.sleep(200);
            element = findSelf();
        }
        return false;
    }

    /**
     * Returns true if there is a web element that matches the specified locator and additional parameters
     *
     * @return true if the web element exists
     */
    public boolean exists() {
        element = findSelf();
        return element != null;
    }

    /**
     * Returns true if there is a web element that matches the specified locator and additional parameters
     *
     * @param isPresenceExpected if true, the method will wait for some time to appear the correspondent web element
     * @return true if the web element exists
     */
    public boolean exists(boolean isPresenceExpected) {
        if (isPresenceExpected) {
            waitForPresence();
        } else {
            waitForAbsence();
        }
        return exists();
    }

    /**
     * Sets the checked state of web element according to specified
     */
    public void check(boolean checked) {
        WebElement element = getElement();
        if (element.isSelected() != checked) {
            element.click();
        }
    }

    private void waitForVisibility() {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, TIMEOUT_WAIT_FOR_VISIBILITY);
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            // what should we do in this case? just ignore and let to throw an exception in further code..?
            logger.warn(String.format("'%s' element isn't visible", toString()));
        }
    }

    /**
     * Waits when the element gets enabled state
     */
    public void waitForEnable() {
        WebElement element = getWebElement();
        long finishTime = System.currentTimeMillis() + TIMEOUT_WAIT_FOR_ENABLE * 1000;
        while (System.currentTimeMillis() < finishTime) {
            if (element.isEnabled()) {
                return;
            }
            GeneralUtils.sleep(200);
        }
    }

    /**
     * Waits when the element gets specified text
     */
    public void waitForText(String text) {
        WebElement element = getWebElement();
        long finishTime = System.currentTimeMillis() + TIMEOUT_WAIT_FOR_TEXT * 1000;
        while (System.currentTimeMillis() < finishTime) {
            if (element.getText().equals(text)) {
                return;
            }
            GeneralUtils.sleep(200);
        }
    }

    /**
     * Returns standard WebElement object
     *
     * @return WebElement object
     */
    public WebElement getWebElement() {
        return getElement();
    }

    public void click() {
        getElement().click();
    }

    public void submit() {
        getElement().submit();
    }

    /**
     * Clears the field and types the text to it
     */
    public void type(CharSequence... keysToSend) {
        getElement().clear();
        getElement().sendKeys(keysToSend);
    }

    public void sendKeys(CharSequence... keysToSend) {
        getElement().sendKeys(keysToSend);
    }

    public void clear() {
        getElement().clear();
    }

    public String getTagName() {
        return getElement().getTagName();
    }

    public String getAttribute(String name) {
        return getElement().getAttribute(name);
    }

    public boolean isSelected() {
        return getElement().isSelected();
    }

    public boolean isEnabled() {
        return getElement().isEnabled();
    }

    public String getText() {
        return getElement().getText();
    }

    public List<WebElement> findElements(By by) {
        return getElement().findElements(by);
    }

    public WebElement findElement(By by) {
        return getElement().findElement(by);
    }

    public boolean isDisplayed() {
        return getElement().isDisplayed();
    }

    public Point getLocation() {
        return getElement().getLocation();
    }

    public Dimension getSize() {
        return getElement().getSize();
    }

    public String getCssValue(String propertyName) {
        return getElement().getCssValue(propertyName);
    }

    @Override
    public String toString() {
        return String.format("locator: '%s',%s index: %s",
                locator.toString(),
                parentElement == null ? "" : ", parent: '" + parentElement.toString() + "'",
                elementIndex);
    }
}