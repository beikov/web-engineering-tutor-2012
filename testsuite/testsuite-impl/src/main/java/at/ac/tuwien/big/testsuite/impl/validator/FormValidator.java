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
 * @author Florian Zoubek
 */
@ApplicationScoped
public class FormValidator implements Validator {

    private static final String REF_INPUT_ELEMENT_XPATH_TEMPLATE = "//input[@id=\"%s\"] | //select[@id=\"%s\"] | //textarea[@id=\"%s\"]";
    private static final Map<String, Collection<String>> validationFiles = new HashMap<>();

    @PostConstruct
    public void init() {
        // files for lab1
        Collection<String> filesToValidateLab1 = new ArrayList<>();
        filesToValidateLab1.add("register.html");
        validationFiles.put(TestsuiteConstants.EX_ID_LAB1, filesToValidateLab1);
        
        // files for lab3
        Collection<String> filesToValidateLab3 = new ArrayList<>();
        filesToValidateLab3.add("target/exportedhtml/register-new.html");
        filesToValidateLab3.add("target/exportedhtml/register-success.html");
        filesToValidateLab3.add("target/exportedhtml/register-fail-all.html");
        filesToValidateLab3.add("target/exportedhtml/register-terms-and-conditions.html");
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
     *  - correct label/input mapping
     *  - fieldset exists
     */
    @Override
    public ValidationResult validate(File fileToValidate, String exerciseId) throws Exception {
        List<ValidationResultEntry> validationResultEntries = new ArrayList<>();
        Document doc = DomUtils.createDocument(fileToValidate);

        // fieldset test
        List<Element> formsWithoutFieldsets = DomUtils.listByXpath(doc.getDocumentElement(), "//form[not(fieldset)]");

        if (!formsWithoutFieldsets.isEmpty()) {
            validationResultEntries.add(new DefaultValidationResultEntry("Missing fieldset", formsWithoutFieldsets.size() + " forms without fieldset found", ValidationResultEntryType.ERROR));
        }

        // check for labels without for attribute
        List<Element> labelsWithoutForAttribute = DomUtils.listByXpath(doc.getDocumentElement(), "//label[not(@for)]");

        if (!labelsWithoutForAttribute.isEmpty()) {
            validationResultEntries.add(new DefaultValidationResultEntry("Missing or invalid label/input mapping", labelsWithoutForAttribute.size() + " label(s) without \"for\" attribute found", ValidationResultEntryType.ERROR));
        }

        // check references of labels with for attribute
        List<Element> labels = DomUtils.listByXpath(doc.getDocumentElement(), "//label[@for]");

        for (Element label : labels) {
            String reference = label.getAttribute("for").trim();
            List<Element> referencedInputElements = DomUtils.listByXpath(doc.getDocumentElement(), String.format(REF_INPUT_ELEMENT_XPATH_TEMPLATE, reference, reference, reference));
            if (referencedInputElements.size() != 1) {
                validationResultEntries.add(new DefaultValidationResultEntry("Missing or invalid label/input mapping", "No matching input element for label with reference to id \"" + reference + "\" found", ValidationResultEntryType.ERROR));
            }
        }

        return new DefaultValidationResult("Form Validation", fileToValidate.getName(), new DefaultValidationResultType("WAI - Form"), validationResultEntries);
    }
}
