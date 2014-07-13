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
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
@ApplicationScoped
public class TableUsageValidator implements Validator {

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
        Document doc = DomUtils.createDocument(fileToValidate);
        List<Element> tableElements = DomUtils.listByXpath(doc.getDocumentElement(), "//table");

        if ("register.html".equals(fileToValidate.getName())) {
            if (tableElements.size() > 0) {
                validationResultEntries.add(new DefaultValidationResultEntry("Too many table elements", "No tables are allowed for register.html", ValidationResultEntryType.ERROR));
            }
        } else {
            if (tableElements.size() > 2) {
                validationResultEntries.add(new DefaultValidationResultEntry("Too many table elements", "More than 2 table were used for this document", ValidationResultEntryType.ERROR));
            }

            boolean foundSpielstandTable = false;
            boolean foundSpielerInfoTable = false;

            for (Element e : tableElements) {
                // Check if summary is used
                if (e.getAttribute("summary") == null || e.getAttribute("summary").trim().isEmpty()) {
                    validationResultEntries.add(new DefaultValidationResultEntry("Table element has no summary", "The table element at line " + e.getUserData(DomUtils.LINE_NUMBER_KEY_NAME) + " has no summary", ValidationResultEntryType.ERROR));
                }

                List<Element> ths = DomUtils.listByXpath(e, ".//th");

                if (ths.size() != 4 || ths.size() != 2) {
                    // probably illegal use of th
                }

                if (ths.size() > 0) {
                    String text = ths.get(0).getTextContent().trim().toLowerCase();

                    if (!foundSpielstandTable && "führender".equals(text)) {
                        foundSpielstandTable = true;

                        if (ths.size() != 4) {
                            validationResultEntries.add(new DefaultValidationResultEntry("Table element probably uses th-tags wrong", "Found " + ths.size() + " th elements instead of 4 for table 'Spielinformation'", ValidationResultEntryType.ERROR));
                        }
                    } else if (!foundSpielerInfoTable && "spieler 1".equals(text)) {
                        foundSpielerInfoTable = true;

                        if (ths.size() != 2) {
                            validationResultEntries.add(new DefaultValidationResultEntry("Table element probably uses th-tags wrong", "Found " + ths.size() + " th elements instead of 2 for table 'Spieler'", ValidationResultEntryType.ERROR));
                        }
                    }

                    if (!"führender".equals(text) && !"spieler 1".equals(text)) {
                        validationResultEntries.add(new DefaultValidationResultEntry("Table element probably uses th-tags wrong", "The th element at line " + ths.get(0).getUserData(DomUtils.LINE_NUMBER_KEY_NAME) + " is probably used in a wrong way", ValidationResultEntryType.ERROR));
                    }
                } else {
                    validationResultEntries.add(new DefaultValidationResultEntry("Table element probably uses th-tags wrong", "No th elements could be recognized for table at line " + e.getUserData(DomUtils.LINE_NUMBER_KEY_NAME), ValidationResultEntryType.ERROR));
                }
            }

            if (!foundSpielerInfoTable || !foundSpielstandTable) {
                validationResultEntries.add(new DefaultValidationResultEntry("Table element probably used wrong", "Either the 'Spielinformation' or the 'Spieler' table could not be recognized", ValidationResultEntryType.ERROR));
            }
        }

        return new DefaultValidationResult("Table Usage Validation", fileToValidate.getName(), new DefaultValidationResultType("XHTML"), validationResultEntries);
    }
}
