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
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Florian Zoubek
 */
@ApplicationScoped
public class HeadingUsageValidator implements Validator {

    private final static Pattern HEADING_PATTERN = Pattern.compile("^h[123456]$");
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

    /*
     * Checks:
     *  - h1 text matches all or part of the title
     *  - correct nesting of headings
     */
    @Override
    public ValidationResult validate(File fileToValidate, String exerciseId) throws Exception {
        List<ValidationResultEntry> validationResultEntries = new ArrayList<>();
        Document doc = DomUtils.createDocument(fileToValidate);
        String title = "";

        List<Element> titleElements = DomUtils.listByXpath(doc.getDocumentElement(), "//title");

        if (titleElements.size() > 1) {
            validationResultEntries.add(new DefaultValidationResultEntry("Multiple title elements found", titleElements.size() + " title elements found", ValidationResultEntryType.ERROR));
        } else if (titleElements.size() < 1) {
            validationResultEntries.add(new DefaultValidationResultEntry("No title element found", "No title elements found", ValidationResultEntryType.ERROR));
        } else {
            title = tidyText(titleElements.get(0).getTextContent());
        }

        List<Element> bodyElements = DomUtils.listByXpath(doc.getDocumentElement(), "//body");

        if (bodyElements.size() > 1) {
            validationResultEntries.add(new DefaultValidationResultEntry("Multiple body elements found", titleElements.size() + " body elements found", ValidationResultEntryType.ERROR));
        } else if (bodyElements.size() < 1) {
            validationResultEntries.add(new DefaultValidationResultEntry("No body element found", "No body element found", ValidationResultEntryType.ERROR));
        } else {
            int currentLevel = 0;
            Stack<Element> dfsStack = new Stack<>();
            dfsStack.add(bodyElements.get(0));

            // Begin DFS
            while (!dfsStack.empty()) {
                Element element = dfsStack.pop();

                if (isHeading(element)) {
                    int headingLevel = getHeadingLevel(element);

                    if (headingLevel == 1) {
                        // h1 -> check title 
                        String headingText = tidyText(element.getTextContent());
                        if (!title.contains(headingText)) {
                            validationResultEntries.add(new DefaultValidationResultEntry("Title and h1 text mismatch", "Text of h1 element does not match with document title or parts of it\nh1-text: " + headingText + "\ntitle: " + title, ValidationResultEntryType.ERROR));
                        }
                    }

                    // check nesting                        
                    if (headingLevel > (currentLevel + 1)) {
                        validationResultEntries.add(new DefaultValidationResultEntry("Invalid nesting of headings", "Found heading with level " + headingLevel + " after heading with level " + currentLevel, ValidationResultEntryType.ERROR));
                    } else {
                        currentLevel = headingLevel;
                    }
                }

                // add child elements reversed to the stack to maintain order of elements
                NodeList children = element.getChildNodes();

                for (int i = children.getLength() - 1; i >= 0; i--) {
                    Node child = children.item(i);
                    if (child instanceof Element) {
                        dfsStack.add((Element) child);
                    }
                }
            }
        }

        return new DefaultValidationResult(
                "Heading Usage Validation", fileToValidate.getName(), new DefaultValidationResultType("WAI"), validationResultEntries);
    }

    private boolean isHeading(Element element) {
        return HEADING_PATTERN.matcher(element.getTagName().toLowerCase()).matches();
    }

    private int getHeadingLevel(Element element) {
        return Integer.parseInt(element.getTagName().toLowerCase().replaceFirst("h", ""));
    }

    private String tidyText(String text) {
        return text.replaceAll("\n", "").replaceAll("\r", "").trim();
    }
}
