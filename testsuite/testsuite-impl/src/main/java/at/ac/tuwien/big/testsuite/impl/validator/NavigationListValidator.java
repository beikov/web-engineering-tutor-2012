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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
public class NavigationListValidator implements Validator {

    private final HashMap<String, Map<String, List<String>>> fileNavigationEntries = new HashMap<>();
    private final Map<String, Collection<String>> validationFiles = new HashMap<>();

    public NavigationListValidator() {
        // navigation links present in register
        Map<String, List<String>> registerNavEntries = new HashMap<>();
        registerNavEntries.put(TestsuiteConstants.EX_ID_LAB1, Arrays.asList("Einloggen"));
        registerNavEntries.put(TestsuiteConstants.EX_ID_LAB3, Arrays.asList("Zurück zum Login"));
        
        fileNavigationEntries.put("register.html", registerNavEntries);
        fileNavigationEntries.put("register-new.html", registerNavEntries);
        fileNavigationEntries.put("register-success.html", registerNavEntries);
        fileNavigationEntries.put("register-fail-all.html", registerNavEntries);
        fileNavigationEntries.put("register-terms-and-conditions.html", registerNavEntries);

        // navigation links present in table
        Map<String, List<String>> tableNavEntries = new HashMap<>();
        tableNavEntries.put(TestsuiteConstants.EX_ID_LAB1, Arrays.asList("Neues Spiel", "Ausloggen"));
        tableNavEntries.put(TestsuiteConstants.EX_ID_LAB2, Arrays.asList("Neues Spiel", "Ausloggen"));
        tableNavEntries.put(TestsuiteConstants.EX_ID_LAB3, Arrays.asList("Neues Spiel", "Abmelden"));
        
        fileNavigationEntries.put("table.html", tableNavEntries);
        fileNavigationEntries.put("startup.html", tableNavEntries);
        fileNavigationEntries.put("first_step.html", tableNavEntries);
        fileNavigationEntries.put("finish.html", tableNavEntries);

        // navigation links present in login
        Map<String, List<String>> loginNavEntries = new HashMap<>();
        loginNavEntries.put(TestsuiteConstants.EX_ID_LAB3, Arrays.asList("Registrieren"));
        
        fileNavigationEntries.put("login-new.html", loginNavEntries);
        fileNavigationEntries.put("login-logged-out.html", loginNavEntries);
        fileNavigationEntries.put("login-failed.html", loginNavEntries);
    }

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

        // files for lab3
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
     *  - navigation implemented as list
     */
    @Override
    public ValidationResult validate(File fileToValidate, String exerciseId) throws Exception {
        List<ValidationResultEntry> validationResultEntries = new ArrayList<>();
        Map<String, List<String>> navigationEntries = fileNavigationEntries.get(fileToValidate.getName());

        if (navigationEntries != null && !navigationEntries.isEmpty()) {
            Document doc = DomUtils.createDocument(fileToValidate);
            List<String> exerciseEntries = navigationEntries.get(exerciseId);
            
            if(exerciseEntries != null){
                for (String navigationEntry : exerciseEntries) {
                    List<Element> navigationListCandidate = DomUtils.listByXpath(doc.getDocumentElement(), "//ul[descendant::*[contains(text(),'" + navigationEntry + "')]] | //ol[descendant::*[contains(text(),'" + navigationEntry + "')]]");
                    if (navigationListCandidate.isEmpty()) {
                        validationResultEntries.add(new DefaultValidationResultEntry("Non-list-Navigation", "Navigation link \"" + navigationEntry + "\" is not part of a list.", ValidationResultEntryType.ERROR));
                    }
                }
            }
        }

        return new DefaultValidationResult("Navigation List Validation", fileToValidate.getName(), new DefaultValidationResultType("WAI - Navigation"), validationResultEntries);
    }
}
