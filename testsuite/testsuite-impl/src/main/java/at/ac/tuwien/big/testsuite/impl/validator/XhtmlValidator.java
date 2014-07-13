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
import at.ac.tuwien.big.testsuite.api.validator.Validator;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResult;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResultEntry;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResultType;
import at.ac.tuwien.big.testsuite.impl.util.DomResponseHandler;
import at.ac.tuwien.big.testsuite.impl.util.DomUtils;
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Christian
 */
@ApplicationScoped
public class XhtmlValidator implements Validator {

    private static final Pattern LINE_AND_COLUMN_NUMBER_PATTERN = Pattern.compile(".*Line\\s+([0-9]+).+Column\\s+([0-9]+).+", Pattern.DOTALL);
    private static final Pattern HIDDEN_FORM_INPUT_PATTERN = Pattern.compile(".*(<\\s*input\\s+type=\\\"hidden\\\"\\s+value=\\\"1\\\"\\s+name=\\\"([a-zA-Z0-9]+)_SUBMIT\\\"\\s+\\/>).*");
    private static final Pattern HIDDEN_VIEW_STATE_INPUT_PATTERN = Pattern.compile(".*(<\\s*input\\s+type=\\\"hidden\\\"\\s+value=\\\"([a-zA-Z0-9\\/+=]+)\\\"\\s+name=\\\"javax\\.faces\\.ViewState\\\"\\s+(id=\\\"javax\\.faces\\.ViewState\\\"\\s+)?\\/>).*");
    private static final String W3C_XHTML_VALIDATOR_URL = "http://validator.w3.org/check";
    private static final List<String> IGNORED_MESSAGES = Arrays.asList("W13", "W28");
    @Inject
    private HttpClient httpClient;
    private static final Map<String, Collection<String>> validationFiles = new HashMap<>();

    @PostConstruct
    public void init() {
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
        HttpPost request = new HttpPost(W3C_XHTML_VALIDATOR_URL);
        List<ValidationResultEntry> validationResultEntries = new ArrayList<>();

        try {
            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart("uploaded_file", new FileBody(fileToValidate, "text/html"));
            multipartEntity.addPart("charset", new StringBody("(detect automatically)"));
            multipartEntity.addPart("doctype", new StringBody("Inline"));
            multipartEntity.addPart("group", new StringBody("0"));

            request.setEntity(multipartEntity);
            Document doc = httpClient.execute(request, new DomResponseHandler(httpClient, request));

            String doctype = DomUtils.textByXpath(doc.getDocumentElement(), "//form[@id='form']/table//tr[4]/td[1]");

            if (!"XHTML 1.1".equals(doctype.trim()) && !doctype.contains("XHTML+ARIA 1.0")) {
                validationResultEntries.add(new DefaultValidationResultEntry("Doctype Validation", "The given document is not XHTML 1.1 compatible, instead the guessed doctype is '" + doctype + "'", ValidationResultEntryType.ERROR));
            }

            Document fileToValidateDocument = null;

            Element warningsContainer = DomUtils.byId(doc.getDocumentElement(), "warnings");

            if (warningsContainer != null) {
                for (Element warningChildElement : DomUtils.asList(warningsContainer.getChildNodes())) {
                    if (IGNORED_MESSAGES.contains(warningChildElement.getAttribute("id"))) {
                        continue;
                    }

                    ValidationResultEntryType type = getEntryType(warningChildElement.getAttribute("class"));
                    String title = getTitle(DomUtils.firstByClass(warningChildElement.getElementsByTagName("span"), "msg"));
                    StringBuilder descriptionSb = new StringBuilder();

                    for (Element descriptionElement : DomUtils.listByXpath(warningChildElement, ".//p[position()>1]")) {
                        descriptionSb.append(descriptionElement.getTextContent());
                    }

                    validationResultEntries.add(new DefaultValidationResultEntry(title, descriptionSb.toString(), type));
                }
            }

            Element errorsContainer = DomUtils.byId(doc.getDocumentElement(), "error_loop");

            if (errorsContainer != null) {
                for (Element errorChildElement : DomUtils.asList(errorsContainer.getChildNodes())) {
                    ValidationResultEntryType type = getEntryType(errorChildElement.getAttribute("class"));
                    StringBuilder titleSb = new StringBuilder();
                    NodeList errorEms = errorChildElement.getElementsByTagName("em");

                    if (errorEms.getLength() > 0) {
                        titleSb.append(getTitle((Element) errorEms.item(0)));
                        titleSb.append(": ");
                    }

                    titleSb.append(getTitle(DomUtils.firstByClass(errorChildElement.getElementsByTagName("span"), "msg")));
                    StringBuilder descriptionSb = new StringBuilder();

                    for (Element descriptionElement : DomUtils.listByXpath(errorChildElement, ".//div/p")) {
                        descriptionSb.append(descriptionElement.getTextContent());
                    }

                    String title = titleSb.toString();

                    if (TestsuiteConstants.EX_ID_LAB3.equals(exerciseId)) {
                        // This is more a hack than anything else but we have to ignore the errors that were produced by JSF specific artifacts.
                        // We basically extract the line and column number from the reported errors and look for the 2 elements that match these
                        // numbers and check if they really are the input elements produced by forms that cant be wrapped by block containers.
                        // More specifically we check for inputs with type hidden, one is for the ViewState of JSF and the other is for recognition
                        // of the form that was submitted.
                        Matcher matcher = LINE_AND_COLUMN_NUMBER_PATTERN.matcher(title);

                        if (title.contains("document type does not allow element \"input\" here") && matcher.matches()) {
                            if (fileToValidateDocument == null) {
                                fileToValidateDocument = DomUtils.createDocument(fileToValidate);
                            }

                            boolean excludeEntry = false;
                            int expectedLineNumber = Integer.parseInt(matcher.group(1));
                            int expectedColumnNumber = Integer.parseInt(matcher.group(2));

                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToValidate)))) {
                                String line;

                                while ((line = reader.readLine()) != null) {
                                    if (--expectedLineNumber == 0) {
                                        Matcher lineMatcher = HIDDEN_FORM_INPUT_PATTERN.matcher(line);

                                        if (lineMatcher.matches()) {
                                            MatchResult matchResult = lineMatcher.toMatchResult();
                                            if (matchResult.start(1) <= expectedColumnNumber && matchResult.end(1) >= expectedColumnNumber) {
                                                excludeEntry = true;
                                                break;
                                            }
                                        }

                                        lineMatcher = HIDDEN_VIEW_STATE_INPUT_PATTERN.matcher(line);

                                        if (lineMatcher.matches()) {
                                            MatchResult matchResult = lineMatcher.toMatchResult();
                                            if (matchResult.start(1) <= expectedColumnNumber && matchResult.end(1) >= expectedColumnNumber) {
                                                excludeEntry = true;
                                                break;
                                            }
                                        }

                                        System.out.println("Could not match potential wrong error.");

                                        break;
                                    }
                                }
                            }

                            if (excludeEntry) {
                                continue;
                            }
                        }
                    }

                    validationResultEntries.add(new DefaultValidationResultEntry(title, descriptionSb.toString(), type));
                }
            }
        } finally {
            request.releaseConnection();
        }

        return new DefaultValidationResult("XHTML Validation", fileToValidate.getName(), new DefaultValidationResultType("XHTML"), validationResultEntries);
    }

    private static ValidationResultEntryType getEntryType(String cssClass) {
        switch (cssClass) {
            case "msg_info":
                return ValidationResultEntryType.OK;
            case "msg_warn":
                return ValidationResultEntryType.WARNING;
            default:
                return ValidationResultEntryType.ERROR;
        }
    }

    private static String getTitle(Element msgElement) {
        return msgElement == null ? null : msgElement.getTextContent();
    }
}
