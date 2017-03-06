package com.template.autoqa.core.web.pages;

import com.template.autoqa.core.web.elements.BaseWebElement;
import com.template.autoqa.core.web.selenium.BaseWebDriver;


public abstract class BaseWebPage {

    protected BaseWebDriver webDriver;

    protected BaseWebElement linkToOpen;
    protected BaseWebElement linkLogout;
    private BaseWebElement linkActivePage;

    public BaseWebPage(BaseWebDriver webDriver) {
        this.webDriver = webDriver;
        initializeLinkToOpen();
    }

    /**
     * Initializes a link to activate current page
     */
    protected abstract void initializeLinkToOpen();

    public boolean isCurrent() {
        return linkActivePage.getText().equals(getTitle());
    }

    public String getTitle() {
        return linkToOpen.getText();
    }

    /**
     * Opens the page
     */
    public void open() {
        linkToOpen.click();
    }

    /**
     * Logs out from the system by click of Logout link (if it exists)
     */
    public void logout() {
        if (linkLogout.exists(true)) {
            linkLogout.click();
        }
    }
}
