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
package at.ac.tuwien.big.testsuite.impl.validator;

import at.ac.tuwien.big.testsuite.api.model.ValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntry;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntryType;
import at.ac.tuwien.big.testsuite.api.service.WebApplicationService;
import at.ac.tuwien.big.testsuite.api.validator.Validator;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResult;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResultEntry;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResultType;
import at.ac.tuwien.big.testsuite.impl.util.DomResponseHandler;
import at.ac.tuwien.big.testsuite.impl.util.DomUtils;
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
@ApplicationScoped
public class WaiValidator implements Validator {

    private static final List<String> IGNORED_MESSAGES = Arrays.asList("48", "49");
    private static final Pattern CHECK_PATTERN = Pattern.compile("[a-zA-Z\\s]+([1-9][0-9]*):.*", Pattern.DOTALL);
    private static final Map<String, Collection<String>> validationFiles = new HashMap<>();
    @Inject
    private HttpClient httpClient;
    @Inject
    private WebApplicationService webApplicationService;
    private String contextPath;

    @PostConstruct
    public void init() {
        try {
            contextPath = webApplicationService.deployWar(WaiValidator.class.getResourceAsStream("/achecker-1.3.war"), "achecker");
            // Call it at least one time so we can be sure there is no error or so
            httpClient.execute(new HttpGet("http://localhost:8080/" + contextPath));
        } catch (Exception ex) {
            throw new RuntimeException("Could not deploy achecker", ex);
        }
        // files for lab1
        Collection<String> filesToValidateLab1 = new ArrayList<>();
        filesToValidateLab1.add("table.html");
        filesToValidateLab1.add("register.html");
        validationFiles.put(TestsuiteConstants.EX_ID_LAB1, filesToValidateLab1);

        // files for lab2
        Collection<String> filesToValidateLab2 = new ArrayList<>();
        filesToValidateLab2.add("target/exportedhtml/startup.html");
        filesToValidateLab2.add("target/exportedhtml/first_step.html");
        filesToValidateLab2.add("target/exportedhtml/finish.html");
        validationFiles.put(TestsuiteConstants.EX_ID_LAB2, filesToValidateLab2);

        // files for lab2
        Collection<String> filesToValidateLab3 = new ArrayList<>();
        filesToValidateLab3.add("target/exportedhtml/startup.html");
        filesToValidateLab3.add("target/exportedhtml/first_step.html");
        filesToValidateLab3.add("target/exportedhtml/finish.html");
        filesToValidateLab3.add("target/exportedhtml/register-new.html");
        filesToValidateLab3.add("target/exportedhtml/register-success.html");
        filesToValidateLab3.add("target/exportedhtml/register-fail-all.html");
        filesToValidateLab3.add("target/exportedhtml/register-terms-and-conditions.html");
        filesToValidateLab3.add("target/exportedhtml/login-new.html");
        filesToValidateLab3.add("target/exportedhtml/login-logged-out.html");
        filesToValidateLab3.add("target/exportedhtml/login-failed.html");
        validationFiles.put(TestsuiteConstants.EX_ID_LAB3, filesToValidateLab3);
    }

    @PreDestroy
    public void destroy() {
        if (contextPath != null) {
            webApplicationService.undeploy(contextPath);
        }
    }

    @Override
    public Collection<String> filesToValidate(String exerciseId) {
        Collection<String> filesToValidate = validationFiles.get(exerciseId);
        if (filesToValidate == null) {
            filesToValidate = new ArrayList<>();
        }
        return Collections.unmodifiableCollection(filesToValidate);
    }

    @Override
    public ValidationResult validate(File fileToValidate, String exerciseId) throws Exception {
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        HttpPost request = new HttpPost("http://localhost:8080/" + contextPath + "/checker/index.php");
        List<ValidationResultEntry> validationResultEntries = new ArrayList<>();

        try {
            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart("uri", new StringBody(""));
            multipartEntity.addPart("MAX_FILE_SIZE", new StringBody("52428800"));
            multipartEntity.addPart("uploadfile", new FileBody(fileToValidate, "text/html"));
            multipartEntity.addPart("validate_file", new StringBody("Check It"));
            multipartEntity.addPart("pastehtml", new StringBody(""));
            multipartEntity.addPart("radio_gid[]", new StringBody("8"));
            multipartEntity.addPart("checkbox_gid[]", new StringBody("8"));
            multipartEntity.addPart("rpt_format", new StringBody("1"));

            request.setEntity(multipartEntity);
            Document doc = httpClient.execute(request, new DomResponseHandler(httpClient, request), httpContext);
            Element errorsContainer = DomUtils.byId(doc.getDocumentElement(), "AC_errors");

            String title = "";
            StringBuilder descriptionSb = new StringBuilder();
            boolean descriptionStarted = false;

            for (Element e : DomUtils.asList(errorsContainer.getChildNodes())) {
                if ("h3".equals(e.getTagName())) {
                    if (descriptionStarted) {
                        validationResultEntries.add(new DefaultValidationResultEntry(title, descriptionSb.toString(), ValidationResultEntryType.ERROR));
                    }

                    title = e.getTextContent();
                    descriptionSb.setLength(0);
                    descriptionStarted = false;
                } else if ("div".equals(e.getTagName()) && e.getAttribute("class").contains("gd_one_check")) {
                    if (descriptionStarted) {
                        descriptionSb.append('\n');
                    }

                    if (extractDescription(e, descriptionSb)) {
                        descriptionStarted = true;
                    }
                }
            }

            if (descriptionStarted) {
                validationResultEntries.add(new DefaultValidationResultEntry(title, descriptionSb.toString(), ValidationResultEntryType.ERROR));
            }
        } finally {
            request.releaseConnection();
        }

        return new DefaultValidationResult("WAI Validation", fileToValidate.getName(), new DefaultValidationResultType("WAI"), validationResultEntries);
    }

    private boolean extractDescription(Element container, StringBuilder sb) {
        for (Element e : DomUtils.asList(container.getChildNodes())) {
            if ("span".equals(e.getTagName()) && e.getAttribute("class").contains("gd_msg")) {
                String text = e.getTextContent();
                Matcher matcher = CHECK_PATTERN.matcher(text);

                if (matcher.matches() && IGNORED_MESSAGES.contains(matcher.group(1))) {
                    return false;
                }

                sb.append(text.replaceAll("\\s*\\n\\s*", " ").trim());
            } else if ("div".equals(e.getTagName()) && e.getAttribute("class").contains("gd_question_section")) {
                sb.append('\n');
                sb.append(e.getTextContent().replaceAll("\\s*\\n\\s*", " ").trim());
            } else if ("table".equals(e.getTagName()) && e.getAttribute("class").contains("data")) {
                sb.append('\n');
                sb.append(DomUtils.textByXpath(e, ".//em").replaceAll("\\s*\\n\\s*", " ").trim());
                sb.append('\n');
                sb.append(DomUtils.textByXpath(e, ".//pre").replaceAll("\\s*\\n\\s*", " ").trim());
            }
        }

        return true;
    }
}
