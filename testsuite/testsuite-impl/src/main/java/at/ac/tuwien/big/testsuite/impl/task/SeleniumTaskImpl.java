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
package at.ac.tuwien.big.testsuite.impl.task;

import at.ac.tuwien.big.testsuite.api.model.MavenGroupReport;
import at.ac.tuwien.big.testsuite.api.model.SeleniumReport;
import at.ac.tuwien.big.testsuite.api.model.ValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntry;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntryType;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultType;
import at.ac.tuwien.big.testsuite.api.service.WebApplicationService;
import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.ParameterizableTask;
import at.ac.tuwien.big.testsuite.impl.model.DefaultSeleniumReport;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResult;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResultEntry;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResultType;
import at.ac.tuwien.big.testsuite.impl.selenium.LoginAction;
import at.ac.tuwien.big.testsuite.impl.selenium.LoginTest;
import at.ac.tuwien.big.testsuite.impl.selenium.RegisterTest;
import at.ac.tuwien.big.testsuite.impl.selenium.TableTest;
import at.ac.tuwien.big.testsuite.impl.util.DomResponseHandler;
import at.ac.tuwien.big.testsuite.impl.util.DomUtils;
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openqa.selenium.WebDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Christian, Florian Zoubek
 */
public class SeleniumTaskImpl extends ParameterizableTask<SeleniumReport, MavenGroupReport> {

    private static final Logger LOG = Logger.getLogger(SeleniumTaskImpl.class.getName());
    private static final int CONCURRENT_SELENIUM_TASK_RUNS = 2;
    private static final BlockingQueue<Object> tokens = new LinkedBlockingQueue<>();
    private final WebApplicationService webApplicationService;
    private final HttpClient httpClient;
    private final String exerciseId;

    static{
        for (int i = 0; i < CONCURRENT_SELENIUM_TASK_RUNS; i++) {
            tokens.add(new Object());
        }
    }

    public SeleniumTaskImpl(WebApplicationService webApplicationService, HttpClient httpClient, String exerciseId) {
        this.webApplicationService = webApplicationService;
        this.httpClient = httpClient;
        this.exerciseId = exerciseId;
    }

    public SeleniumTaskImpl(WebApplicationService webApplicationService, HttpClient httpClient, String exerciseId, HierarchicalTask<?, ?> parent) {
        super(parent);
        this.webApplicationService = webApplicationService;
        this.httpClient = httpClient;
        this.exerciseId = exerciseId;
    }

    @Override
    protected SeleniumReport call(MavenGroupReport mavenReport) throws Exception {
        String contextPath = null;
        Map<ValidationResultType, Collection<ValidationResult>> map = new HashMap<>();
        Collection<ValidationResult> validationResults = new ArrayList<>();
        ValidationResultType validationResultType = new DefaultValidationResultType("Selenium");

        map.put(validationResultType, validationResults);

        Object token = tokens.take();

        try {
            if (mavenReport.getWarFile() == null) {
                throw new RuntimeException("Maven Build probably failed, no war file found. The following output was produced by the build:\n" + mavenReport.getOutput());
            }

            File war = mavenReport.getWarFile();
            String warName = war.getName();
            File warContent = new File(war.getParentFile(), warName.substring(0, warName.lastIndexOf('.')));
            contextPath = webApplicationService.deployWebapp(warContent);
            String serverBase = webApplicationService.getServerBase(contextPath);
            String welcomeFileUrl = webApplicationService.getWelcomeFileUrl(contextPath);
            String webappUrl = welcomeFileUrl != null ? welcomeFileUrl : webApplicationService.getUserServletUrl(contextPath);

            JUnitCore junit = new JUnitCore();
            Map<String, Result> results = new HashMap<>();

            try {
                webappUrl = getWebappUrlAndExportCss(webappUrl, mavenReport, contextPath, serverBase);

                final TableTest tableTest = new TableTest();
                tableTest.setGroupDir(mavenReport.getBaseDirectory());
                tableTest.setWebappUrl(webappUrl);

                if (TestsuiteConstants.EX_ID_LAB3.equals(exerciseId)) {
                    final RegisterTest registerTest = new RegisterTest();
                    registerTest.setGroupDir(mavenReport.getBaseDirectory());
                    registerTest.setWebappUrl(webappUrl);
                    results.put("Register Test", junit.run(new BlockJUnit4ClassRunner(RegisterTest.class) {
                        @Override
                        protected Object createTest() throws Exception {
                            return registerTest;
                        }
                    }));

                    final LoginTest loginTest = new LoginTest();
                    loginTest.setGroupDir(mavenReport.getBaseDirectory());
                    loginTest.setWebappUrl(webappUrl);
                    loginTest.setUsername(registerTest.getUsername());
                    loginTest.setPassword(registerTest.getPassword());
                    results.put("Login Test", junit.run(new BlockJUnit4ClassRunner(LoginTest.class) {
                        @Override
                        protected Object createTest() throws Exception {
                            return loginTest;
                        }
                    }));

                    tableTest.setDiceFormPrefix("form:");
                    tableTest.setNavigationFormPrefix("navForm:");
                    tableTest.setLogin(new LoginAction() {
                        @Override
                        public String doLogin(WebDriver driver) {
                            try {
                                loginTest.doLogin(driver, loginTest.getUsername(), loginTest.getPassword());
                            } catch (Exception ex) {
                                LOG.log(Level.SEVERE, "Could not do login", ex);
                                return null;
                            }

                            return loginTest.getUsername();
                        }
                    });
                }

                results.put("Table Test", junit.run(new BlockJUnit4ClassRunner(TableTest.class) {
                    @Override
                    protected Object createTest() throws Exception {
                        return tableTest;
                    }
                }));
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Exception occured during selenium tests for group " + mavenReport.getGroup(), e);
                Collection<ValidationResultEntry> entries = new ArrayList<>();

                entries.add(new DefaultValidationResultEntry("SeleniumException", e.toString(), ValidationResultEntryType.ERROR));
                validationResults.add(new DefaultValidationResult("Selenium Tests", "Exception", validationResultType, entries));
            }

            if (hasFailures(results) || welcomeFileUrl != null) {
                Collection<ValidationResultEntry> entries;

                for (Map.Entry<String, Result> entry : results.entrySet()) {
                    Result result = entry.getValue();
                    entries = new ArrayList<>();

                    for (Failure failure : result.getFailures()) {
                        String title = failure.getException().getMessage();
                        String description = failure.getDescription().toString();

                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); PrintStream ps = new PrintStream(baos)) {
                            failure.getException().printStackTrace(ps);
                            description += "\n" + baos.toString();
                        }

                        entries.add(new DefaultValidationResultEntry(title, description, ValidationResultEntryType.ERROR));
                    }

                    validationResults.add(new DefaultValidationResult("Selenium Tests", entry.getKey(), validationResultType, entries));
                }

                if (welcomeFileUrl == null) {
                    entries = new ArrayList<>();
                    entries.add(new DefaultValidationResultEntry("Welcome file is missing", "Welcome file could not be found in web.xml", ValidationResultEntryType.ERROR));
                    validationResults.add(new DefaultValidationResult("Selenium Tests", "web.xml", validationResultType, entries));
                }
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Deployment exception for group " + mavenReport.getGroup(), ex);
            Collection<ValidationResultEntry> entries = new ArrayList<>();

            entries.add(new DefaultValidationResultEntry("DeploymentException", ex.toString(), ValidationResultEntryType.ERROR));
            validationResults.add(new DefaultValidationResult("Selenium Tests", "Exception", validationResultType, entries));
        } finally {
            tokens.put(token);

            if (contextPath != null) {
                Collection<Throwable> runtimeExceptions = webApplicationService.undeploy(contextPath);

                if (runtimeExceptions != null && !runtimeExceptions.isEmpty()) {
                    Collection<ValidationResultEntry> entries = new ArrayList<>();

                    for (Throwable e : runtimeExceptions) {
                        LOG.log(Level.SEVERE, "Application exception occured during selenium tests for group " + mavenReport.getGroup(), e);
                        entries.add(new DefaultValidationResultEntry("ApplicationException", e.toString(), ValidationResultEntryType.ERROR));
                    }

                    validationResults.add(new DefaultValidationResult("Selenium Tests", "Exception", validationResultType, entries));
                }
            }
        }

        return new DefaultSeleniumReport(map);
    }

    private boolean hasFailures(Map<String, Result> results) {
        if (results == null || results.isEmpty()) {
            return false;
        }

        for (Map.Entry<String, Result> entry : results.entrySet()) {
            if (entry.getValue().getFailureCount() > 0) {
                return true;
            }
        }

        return false;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    private static void copyToFile(InputStream in, File f) throws Exception {
        try (OutputStream out = new FileOutputStream(f)) {
            copyInputStream(in, out);
        }
    }

    private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
    }

    private String getWebappUrlAndExportCss(String webappUrl, MavenGroupReport mavenReport, String contextPath, String serverBase) {
        try {
            int i = 0;
            String welcomePath = webappUrl;
            String[] commonWelcomePaths = {"table.jsp"};
            Document doc = null;
            String cssHref = null;
            Collection<Exception> exceptions = new ArrayList<>();

            while (doc == null && i < commonWelcomePaths.length) {
                try {
                    doc = getDocument(welcomePath, 10);

                    // Workaround for stupid students that think they must use a meta refresh instead of just adding the URL to their web.xml files :(
                    List<Element> refreshes = DomUtils.listByXpath(doc.getDocumentElement(), "//meta[@http-equiv='refresh']");

                    if (!refreshes.isEmpty()) {
                        String content = refreshes.get(0).getAttribute("content");
                        Matcher matcher = Pattern.compile(".*URL=\"(.+)\"", Pattern.CASE_INSENSITIVE).matcher(content);
                        matcher = matcher.matches() ? matcher : Pattern.compile(".*URL='(.+)'", Pattern.CASE_INSENSITIVE).matcher(content);
                        matcher = matcher.matches() ? matcher : Pattern.compile(".*URL=(.+)", Pattern.CASE_INSENSITIVE).matcher(content);

                        if (matcher.matches()) {
                            welcomePath = welcomePath.substring(0, welcomePath.lastIndexOf('/'));
                            welcomePath = welcomePath + (matcher.group(1).startsWith("/") ? matcher.group(1) : "/" + matcher.group(1));
                            doc = getDocument(welcomePath, 10);
                        }
                    }

                    cssHref = DomUtils.listByXpath(doc.getDocumentElement(), "//link[@type='text/css']").get(0).getAttribute("href");

                    if (cssHref.startsWith(contextPath)) {
                        cssHref = cssHref.substring(contextPath.length());
                    } else if (cssHref.startsWith("/" + contextPath)) {
                        cssHref = cssHref.substring(contextPath.length() + 1);
                    }

                    if (!cssHref.startsWith("/")) {
                        cssHref = "/" + cssHref;
                    }

                    webappUrl = welcomePath;
                } catch (Exception ex) {
                    exceptions.add(new RuntimeException("Calling the following URL on the webapp resulted in an error: " + welcomePath.substring(welcomePath.indexOf(contextPath) + contextPath.length()), ex));
                    welcomePath = welcomePath.substring(0, welcomePath.indexOf(contextPath) + contextPath.length()) + "/" + commonWelcomePaths[i++];
                }
            }

            if (cssHref == null) {
                Exception ex = new RuntimeException("Could not find path to welcome file");

                for (Exception excetion : exceptions) {
                    ex.addSuppressed(excetion);
                }

                throw ex;
            }

            File css = new File(mavenReport.getBaseDirectory(), "target/exportedhtml/screen.css");
            css.getParentFile().mkdirs();
            copyToFile(new URL(serverBase + cssHref).openStream(), css);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Could not export css file for group " + mavenReport.getGroup(), ex);
        }

        return webappUrl;
    }

    private Document getDocument(String url, int timeoutSeconds) throws Exception {
        ExecutorService singlePool = Executors.newSingleThreadExecutor();
        final HttpGet getRequest = new HttpGet(url);

        Future<Document> docFuture = singlePool.submit(new Callable<Document>() {
            @Override
            public Document call() throws Exception {
                return httpClient.execute(getRequest, new DomResponseHandler(httpClient, getRequest));
            }
        });

        try {
            return docFuture.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new RuntimeException("Could not retrieve the document within " + timeoutSeconds + " seconds", ex);
        } finally {
            getRequest.releaseConnection();
        }
    }
}
