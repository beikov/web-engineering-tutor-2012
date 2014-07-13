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

/**
 *
 * @author Christian
 */
@ApplicationScoped
public class LanguageAttributeValidator implements Validator {

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
        List<ValidationResultEntry> validationResultEntries = new ArrayList<>();
        Document doc = DomUtils.createDocument(fileToValidate);

        String xmlLang = doc.getDocumentElement().getAttribute("xml:lang");
        String lang = doc.getDocumentElement().getAttribute("lang");

        if (isEmpty(lang) && isEmpty(xmlLang)) {
            validationResultEntries.add(new DefaultValidationResultEntry("No lang or xml:lang attribute", "The root node(html) has neither a lang- nor a xml:lang-Attribute", ValidationResultEntryType.ERROR));
        }

        return new DefaultValidationResult("Lang Attribute Validation", fileToValidate.getName(), new DefaultValidationResultType("XHTML"), validationResultEntries);
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
