/*
 * Copyright 2013 BIG TU Wien.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.ac.tuwien.big.testsuite.impl.selenium;

import at.ac.tuwien.big.testsuite.impl.util.DomUtils;
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import static org.hamcrest.Matchers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
public abstract class BaseSeleniumTest {

    private static final int MAX_DRIVER_GET_RETRY = 20;
    private static final Logger LOG = Logger.getLogger(BaseSeleniumTest.class.getName());
    protected static final String DEFAULT_WEBAPP_URL = "http://localhost:8084/lab3";
    private static final String BASE_EXPORT_DIR = "target/exportedhtml/";
    private static ChromeDriverService chromeService;
    private static InternetExplorerDriverService ieService;
    private final List<WebDriver> webDrivers = new ArrayList<>();
    private LoginAction login;
    protected String user = "Super Mario";
    protected String webappUrl = DEFAULT_WEBAPP_URL;
    protected WebDriver driver;
    protected File groupDir;
    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    public LoginAction getLogin() {
        return login;
    }

    public void setLogin(LoginAction login) {
        this.login = login;
    }

    public void setGroupDir(File groupDir) {
        this.groupDir = groupDir;
    }

    public File getGroupDir() {
        return groupDir;
    }

    public String getWebappUrl() {
        return webappUrl;
    }

    public void setWebappUrl(String webappUrl) {
        this.webappUrl = webappUrl;
    }

    @BeforeClass
    public static void createAndStartService() throws Exception {
        File baseDir = new File(BaseSeleniumTest.class.getClassLoader().getResource("").toURI());
        String chromeDriverLocation = System.getProperty("webdriver.chrome.driver");
        String ieDriverLocation = System.getProperty("webdriver.ie.driver");

        if (chromeDriverLocation == null || chromeDriverLocation.trim().isEmpty()) {
            String path;

            if (OsUtils.isWindows()) {
                path = new File(baseDir, "chromedriverWin.exe").getAbsolutePath();
            } else if (OsUtils.isMac()) {
                path = new File(baseDir, "chromedriverMac").getAbsolutePath();
            } else if (OsUtils.is32Bit()) {
                path = new File(baseDir, "chromedriverLinux32").getAbsolutePath();
            } else if (OsUtils.is64Bit()) {
                path = new File(baseDir, "chromedriverLinux64").getAbsolutePath();
            } else {
                path = null;
            }

            if (path != null) {
                System.setProperty("webdriver.chrome.driver", path);
            }
        }

        if (ieDriverLocation == null || ieDriverLocation.trim().isEmpty()) {
            String path;

            if (OsUtils.isWindows() && OsUtils.is32Bit()) {
                path = new File(baseDir, "IEDriverServer32.exe").getAbsolutePath();
            } else if (OsUtils.isWindows() && OsUtils.is64Bit()) {
                path = new File(baseDir, "IEDriverServer64.exe").getAbsolutePath();
            } else {
                path = null;
            }

            if (path != null) {
                System.setProperty("webdriver.ie.driver", path);
            }
        }

        try {
            chromeService = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File(System.getProperty("webdriver.chrome.driver")))
                    .usingAnyFreePort()
                    .build();
            chromeService.start();
        } catch (Exception ex) {
            // If the service can not be started, use other drivers or so
            chromeService = null;
        }

        try {
            ieService = new InternetExplorerDriverService.Builder()
                    .usingDriverExecutable(new File(System.getProperty("webdriver.ie.driver")))
                    .usingAnyFreePort()
                    .build();
            ieService.start();
        } catch (Exception ex) {
            // If the service can not be started, use other drivers or so
            ieService = null;
        }
    }

    @AfterClass
    public static void createAndStopService() {
        if (chromeService != null) {
            chromeService.stop();
        }
        if (ieService != null) {
            ieService.stop();
        }
    }

    @Before
    public void init() throws Exception {
        driver = createDriver();
    }

    @After
    public void destroy() throws Exception {
        Iterator<WebDriver> iter = webDrivers.iterator();

        while (iter.hasNext()) {
            WebDriver webDriver = iter.next();
            webDriver.quit();
            iter.remove();
        }
    }

    protected final WebDriver createDriver() throws Exception {
        Exception exception = null;
        WebDriver webDriver = null;
        int tries = 0;

        while (tries < MAX_DRIVER_GET_RETRY) {
            try {
                if (chromeService != null) {
                    webDriver = new RemoteWebDriver(chromeService.getUrl(),
                            DesiredCapabilities.chrome());
                } else {
                    webDriver = new ChromeDriver();
                }
            } catch (Exception ex) {
                exception = ex;

                try {
                    webDriver = new FirefoxDriver();
                } catch (Exception ex1) {
                    exception.addSuppressed(ex1);

                    try {
                        webDriver = new SafariDriver();
                    } catch (Exception ex2) {
                        exception.addSuppressed(ex2);

                        try {
                            if (ieService != null) {
                                webDriver = new RemoteWebDriver(ieService.getUrl(),
                                        DesiredCapabilities.internetExplorer());
                            } else {
                                webDriver = new InternetExplorerDriver();
                            }
                        } catch (Exception ex3) {
                            exception.addSuppressed(ex3);
                        }
                    }
                }
            }

            if (webDriver != null) {
                break;
            }
        }

        if (webDriver == null) {
            throw new RuntimeException("Could not find a browser for testing", exception);
        }

        webDrivers.add(webDriver);
        webDriver.get(webappUrl);

        if (login != null) {
            user = login.doLogin(webDriver);

            if (user == null) {
                user = "";
                errorCollector.checkThat("Could not login", true, is(false));
            }
        }

        return webDriver;
    }

    protected void exportCurrentHTML(WebDriver driver, String filename) {
        File exportFile = new File(groupDir, BASE_EXPORT_DIR + filename);
        File parent = exportFile.getParentFile();

        if (!parent.exists()) {
            parent.mkdirs();
        }

        PrintWriter out = null;

        try {
            exportFile.createNewFile();

            String pageSource = driver.getPageSource();
            Document doc = DomUtils.createDocument(new ByteArrayInputStream(pageSource.getBytes("UTF-8")));

            String encoding = "UTF-8";
            boolean encodingFound = false;
            // Try to find the correct encoding
            List<Element> metaTags = DomUtils.listByXpath(doc.getDocumentElement(), "//meta[@http-equiv=\"content-type\"]");
            for (Element meta : metaTags) {
                String attContent = meta.getAttribute("content").trim();
                if (!attContent.isEmpty()) {
                    String[] parts = attContent.split(";");
                    for (String part : parts) {
                        if (part.trim().startsWith("charset=")) {
                            encoding = part.split("=")[1];
                            encodingFound = true;
                            break;
                        }
                    }
                    if (encodingFound) {
                        break;
                    }
                }
            }

            if (LOG.isLoggable(Level.FINE)) {
                LOG.log(Level.FINE, "Detected encoding: {0}", encoding);
            }

            out = new PrintWriter(exportFile, encoding);
            out.print(pageSource);
            out.flush();
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    protected String textById(String id) {
        return textById(driver, id);
    }

    protected WebElement getParentElement(By by) {
        return getParentElement(driver, by);
    }

    protected boolean waitForJQuery(By by) {
        return waitForJQuery(by, 20000, 2000);
    }

    protected boolean waitForJQuery(By by, int timeOutInMiliSeconds, int pollingInMiliSeconds) {
        return waitForJQuery(driver, by, timeOutInMiliSeconds, pollingInMiliSeconds);
    }

    protected boolean waitForJQuery() {
        return waitForJQuery(20000, 2000);
    }

    protected boolean waitForJQuery(int timeOutInMiliSeconds, int pollingInMiliSeconds) {
        return waitForJQuery(driver, timeOutInMiliSeconds, pollingInMiliSeconds);
    }

    public boolean exists(By by) {
        return exists(driver, by);
    }

    protected void clickNonAjax(By by) {
        clickNonAjax(driver, by);
    }

    protected void writeText(String id, String text) {
        writeText(driver, id, text);
    }

    protected boolean isElementDisplayed(By by) {
        return isElementDisplayed(driver, by);
    }

    protected void clickAjax(String id) {
        clickAjax(driver, id);
    }

    protected void clickAjax(By by) {
        clickAjax(driver, by);
    }

    protected static String textById(WebDriver driver, String id) {
        try {
            return driver.findElement(By.id(id)).getText().trim();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    protected static WebElement getParentElement(WebDriver driver, By by) {
        try {
            WebElement element = driver.findElement(by);
            return element.findElement(By.xpath(".."));
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    protected static boolean waitForJQuery(WebDriver driver, By by) {
        return waitForJQuery(driver, by, 20000, 2000);
    }

    protected static boolean waitForJQuery(WebDriver driver, final By by, int timeOutInMiliSeconds, int pollingInMiliSeconds) {
        try {
            new FluentWait<>(driver).withTimeout(timeOutInMiliSeconds, TimeUnit.MILLISECONDS)
                    .pollingEvery(pollingInMiliSeconds, TimeUnit.MILLISECONDS)
                    .until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return ((Boolean) ((JavascriptExecutor) driver).executeScript("return (typeof jQuery === 'undefined') || (jQuery.active == 0 && $(\":animated\").length == 0)")) && exists(driver, by);
                }
            });

            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    protected static boolean waitForJQuery(WebDriver driver) {
        return waitForJQuery(driver, 20000, 2000);
    }

    protected static boolean waitForJQuery(WebDriver driver, int timeOutInMiliSeconds, int pollingInMiliSeconds) {
        try {
            new FluentWait<>(driver).withTimeout(timeOutInMiliSeconds, TimeUnit.MILLISECONDS)
                    .pollingEvery(pollingInMiliSeconds, TimeUnit.MILLISECONDS)
                    .until(new ExpectedCondition<Boolean>() {
                @Override
                public Boolean apply(WebDriver driver) {
                    return ((Boolean) ((JavascriptExecutor) driver).executeScript("return (typeof jQuery === 'undefined') || (jQuery.active == 0 && $(\":animated\").length == 0)"));
                }
            });

            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    protected static void disableAnimations(WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript("if (typeof jQuery !== 'undefined') { jQuery.fx.off = true }");
    }

    protected static boolean exists(WebDriver driver, By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected static void clickNonAjax(WebDriver driver, By by) {
        driver.findElement(by).click();
        wait(200);
    }

    protected static void writeText(WebDriver driver, String id, String text) {
        final WebElement elem = driver.findElement(By.name(id));
        elem.clear();
        elem.sendKeys(text);
    }

    protected static boolean isElementDisplayed(WebDriver driver, By by) {
        return driver.findElement(by).isDisplayed();
    }

    protected static void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not wait !!!", e);
        }
    }

    protected static void clickAjax(WebDriver driver, String id) {
        clickAjax(driver, By.id(id));
    }

    protected static void clickAjax(WebDriver driver, By by) {
        driver.findElement(by).click();
        waitForJQuery(driver);
    }

    protected <T> void checkThat(String reason, T value, Matcher<T> matcher) {
        errorCollector.checkThat(TestsuiteConstants.KNOWN_ERROR_PREFIX + " " + reason, value, matcher);
    }
}
