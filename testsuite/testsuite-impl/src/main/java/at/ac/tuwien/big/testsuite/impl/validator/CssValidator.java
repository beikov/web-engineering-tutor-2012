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
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.apache.http.client.HttpClient;
import org.w3c.css.css.StyleSheet;
import org.w3c.css.css.StyleSheetOrigin;
import org.w3c.css.css.StyleSheetParser;
import org.w3c.css.parser.CssError;
import org.w3c.css.parser.CssParseException;
import org.w3c.css.parser.CssSelectors;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.Warning;

/**
 *
 * @author Christian, Florian Zoubek
 */
@ApplicationScoped
public class CssValidator implements Validator {

    private static final String W3C_CSS_VALIDATOR_URL = "http://jigsaw.w3.org/css-validator/validator";
    private static final Map<String, Collection<String>> validationFiles = new HashMap<>();
    @Inject
    private HttpClient httpClient;

    @PostConstruct
    public void init() {
        // files for lab1
        Collection<String> filesToValidateLab1 = new ArrayList<>();
        filesToValidateLab1.add("styles/screen.css");
        validationFiles.put(TestsuiteConstants.EX_ID_LAB1, filesToValidateLab1);

        // files for lab2
        Collection<String> filesToValidateLab2 = new ArrayList<>();
        filesToValidateLab2.add("target/exportedhtml/screen.css");
        validationFiles.put(TestsuiteConstants.EX_ID_LAB2, filesToValidateLab2);

        // files for lab2
        Collection<String> filesToValidateLab3 = new ArrayList<>();
        filesToValidateLab3.add("target/exportedhtml/screen.css");
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
        List<ValidationResultEntry> validationResultEntries = new ArrayList<>();

        ApplContext applicationContext = new ApplContext("de");
        applicationContext.setCssVersion("css21");
        applicationContext.setProfile("css21");
        applicationContext.setMedium("all");
        applicationContext.setWarningLevel(1);
        StyleSheetParser parser = new StyleSheetParser();
        parser.parseURL(applicationContext, fileToValidate.toURI().toURL(), null, null, applicationContext.getMedium(), StyleSheetOrigin.AUTHOR);
        StyleSheet styleSheet = parser.getStyleSheet();
        styleSheet.findConflicts(applicationContext);

        for (CssError error : styleSheet.getErrors().getErrors()) {
            String context = "";

            if (error.getException() instanceof CssParseException) {
                StringBuilder sb = new StringBuilder();
                List<CssSelectors> selectors = (List<CssSelectors>) ((CssParseException) error.getException()).getContexts();
                if (selectors != null) {
                    for (int i = 0; i < selectors.size(); i++) {
                        if (i != 0) {
                            sb.append(", ");
                        }

                        sb.append(selectors.get(i).toString());
                    }
                }

                context = sb.toString();
            }

            String title = "CSS Validation Error (" + fileToValidate.getName() + ")";
            String description = "(line " + error.getLine() + ") " + context + ": " + error.getException().getLocalizedMessage();
            validationResultEntries.add(new DefaultValidationResultEntry(title, description, ValidationResultEntryType.ERROR));
        }
        for (Warning warning : styleSheet.getWarnings().getWarnings()) {
            String title = "CSS Validation Error (" + fileToValidate.getName() + ")";
            String description = "(line " + warning.getLine() + ") " + (warning.getContext() == null ? "" : warning.getContext().toString()) + ": " + warning.getWarningMessageEscaped();

            if (!description.contains("Die gleichen Farben für den Vordergrund und den Hintergrund")) {
                validationResultEntries.add(new DefaultValidationResultEntry(title, description, ValidationResultEntryType.WARNING));
            }
        }

        // The solution using the web service

        // Upload file
//        HttpPost request = new HttpPost(W3C_CSS_VALIDATOR_URL);
//
//        try {
//            MultipartEntity multipartEntity = new MultipartEntity();
//            multipartEntity.addPart("file", new FileBody(fileToValidate, "text/css"));
//            multipartEntity.addPart("profile", new StringBody("css3"));
//            multipartEntity.addPart("usermedium", new StringBody("all"));
//            multipartEntity.addPart("warning", new StringBody("1"));
//            multipartEntity.addPart("vextwarning", new StringBody("false"));
//            multipartEntity.addPart("lang", new StringBody("de"));
//
//            request.setEntity(multipartEntity);
//            Document doc = httpClient.execute(request, new DomResponseHandler(httpClient, request));
//
//            // extract validator errors
//
//            List<Element> cssErrorSections = DomUtils.listByXpath(doc.getDocumentElement(), "//*[@id='errors']/descendant::*[@class='error-section']");
//            for (Element section : cssErrorSections) {
//
//                String title = "CSS Validation Error (" + tidyXMLString(DomUtils.listByXpath(section, "//h4").get(0).getTextContent()) + ")";
//
//                List<Element> cssErrors = DomUtils.listByXpath(section, "//*[@class='error']");
//                for (Element cssError : cssErrors) {
//                    String line = "";
//                    String context = "";
//                    String error = "";
//
//                    for (Element e : DomUtils.asList(cssError.getChildNodes())) {
//                        if (e.getAttribute("class").equalsIgnoreCase("linenumber")) {
//                            line = tidyXMLString(e.getTextContent());
//                        } else if (e.getAttribute("class").equalsIgnoreCase("codeContext")) {
//                            context = tidyXMLString(e.getTextContent());
//                        } else if (e.getAttribute("class").equalsIgnoreCase("parse-error")) {
//                            error = tidyXMLString(e.getTextContent());
//                        }
//                    }
//
//                    String description = "(line " + line + ") " + context + ": " + error;
//
//                    validationResultEntries.add(new DefaultValidationResultEntry(title, description, ValidationResultEntryType.ERROR));
//                }
//            }
//
//            // extract validator warnings
//
//            List<Element> cssWarningSections = DomUtils.listByXpath(doc.getDocumentElement(), "//*[@id='warnings']/descendant::*[@class='warning-section']");
//            for (Element section : cssWarningSections) {
//
//                String title = "CSS Validation Warning (" + tidyXMLString(DomUtils.listByXpath(section, "//h4").get(0).getTextContent()) + ")";
//
//                List<Element> cssWarnings = DomUtils.listByXpath(section, "//*[@class='warning']");
//                for (Element cssWarning : cssWarnings) {
//                    String line = "";
//                    String context = "";
//                    String error = "";
//
//                    for (Element e : DomUtils.asList(cssWarning.getChildNodes())) {
//                        if (e.getAttribute("class").equalsIgnoreCase("linenumber")) {
//                            line = tidyXMLString(e.getTextContent());
//                        } else if (e.getAttribute("class").equalsIgnoreCase("codeContext")) {
//                            context = tidyXMLString(e.getTextContent());
//                        } else if (e.getAttribute("class").startsWith("level")) {
//                            error = tidyXMLString(e.getTextContent());
//                        }
//                    }
//
//                    String description = "(" + line + ") " + context + ": " + error;
//
//                    validationResultEntries.add(new DefaultValidationResultEntry(title, description, ValidationResultEntryType.WARNING));
//                }
//            }
//
//        } finally {
//            request.releaseConnection();
//        }

        return new DefaultValidationResult("CSS Validation", fileToValidate.getName(), new DefaultValidationResultType("CSS"), validationResultEntries);
    }

    private String tidyXMLString(String text) {
        return text.replace('\n', ' ').replace('\r', ' ').replaceAll("\\s+", " ").trim();
    }
}
