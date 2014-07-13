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
import at.ac.tuwien.big.testsuite.impl.util.DomUtils;
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Florian Zoubek
 */
@ApplicationScoped
public class CssUsageValidator implements Validator {

    private final static Pattern REL_ABS_FONT_PATTERN = Pattern.compile("font:(.)*px");
    private final static Pattern REL_ABS_FONT_SIZE_PATTERN = Pattern.compile("font-size:(.)*px");
    private final static Pattern ABS_FONT_PATTERN = Pattern.compile("font:(.)*(cm|mm|pc|pt)");
    private final static Pattern ABS_FONT_SIZE_PATTERN = Pattern.compile("font-size:(.)*(cm|mm|pc|pt)");
    private static final Map<String, Collection<String>> validationFiles = new HashMap<>();

    @PostConstruct
    public void init() {
        // files for lab1
        Collection<String> filesToValidateLab1 = new ArrayList<>();
        filesToValidateLab1.add("table.html");
        filesToValidateLab1.add("register.html");
        filesToValidateLab1.add("styles/screen.css");
        validationFiles.put(TestsuiteConstants.EX_ID_LAB1, filesToValidateLab1);

        // files for lab2
        Collection<String> filesToValidateLab2 = new ArrayList<>();
        filesToValidateLab2.add("target/exportedhtml/startup.html");
        filesToValidateLab2.add("target/exportedhtml/first_step.html");
        filesToValidateLab2.add("target/exportedhtml/finish.html");
        filesToValidateLab2.add("target/exportedhtml/screen.css");
        validationFiles.put(TestsuiteConstants.EX_ID_LAB2, filesToValidateLab2);

        // files for lab3
        Collection<String> filesToValidateLab3 = new ArrayList<>();
        filesToValidateLab3.add("target/exportedhtml/startup.html");
        filesToValidateLab3.add("target/exportedhtml/first_step.html");
        filesToValidateLab3.add("target/exportedhtml/finish.html");
        filesToValidateLab3.add("target/exportedhtml/screen.css");
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
        List<ValidationResultEntry> validationResultEntries = new ArrayList<>();

        if (fileToValidate.getName().toLowerCase().endsWith(".html")) {
            // check existence of inline css 
            Document doc = DomUtils.createDocument(fileToValidate);
            List<Element> inlineElements = DomUtils.listByXpath(doc.getDocumentElement(), "//*[@style]");

            for (Element element : inlineElements) {
                String styleValue = element.getAttribute("style").trim();
                if (!styleValue.equalsIgnoreCase("") && !styleValue.matches("display:\\s*block.*")) {
                    validationResultEntries.add(new DefaultValidationResultEntry("Inline css found", "Inline css found in element \"" + element.getTagName() + "\" at line " + element.getUserData(DomUtils.LINE_NUMBER_KEY_NAME), ValidationResultEntryType.ERROR));
                }
            }
        } else {
            // check existence of absolute font sizes
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToValidate)));
            int currentLineNumber = 1;
            String line = fileReader.readLine();
            while (line != null) {
                // Errors for absolute font size 
                Matcher absFontMatcher = ABS_FONT_PATTERN.matcher(line);
                while (absFontMatcher.find()) {
                    validationResultEntries.add(new DefaultValidationResultEntry("Absolute font size found", "Absolute font size found at line " + currentLineNumber + "", ValidationResultEntryType.ERROR));
                }

                Matcher absFontSizeMatcher = ABS_FONT_SIZE_PATTERN.matcher(line);
                while (absFontSizeMatcher.find()) {
                    validationResultEntries.add(new DefaultValidationResultEntry("Absolute font size found", "Absolute font size found at line " + currentLineNumber + "", ValidationResultEntryType.ERROR));
                }

                // Warnings for relative-absolute font-size
                Matcher relAbsFontMatcher = REL_ABS_FONT_PATTERN.matcher(line);
                while (relAbsFontMatcher.find()) {
                    validationResultEntries.add(new DefaultValidationResultEntry("Relative-absolute font size found", "Relative-absolute font size found at line " + currentLineNumber + "", ValidationResultEntryType.WARNING));
                }

                Matcher relAbsFontSizeMatcher = REL_ABS_FONT_SIZE_PATTERN.matcher(line);
                while (relAbsFontSizeMatcher.find()) {
                    validationResultEntries.add(new DefaultValidationResultEntry("Relative-absolute font size found", "Relative-absolute font size found at line " + currentLineNumber + "", ValidationResultEntryType.WARNING));
                }

                line = fileReader.readLine();
                currentLineNumber++;
            }
        }

        return new DefaultValidationResult("CSS Usage Validation", fileToValidate.getName(), new DefaultValidationResultType("CSS"), validationResultEntries);
    }
}
