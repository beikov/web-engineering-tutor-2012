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
import at.ac.tuwien.big.testsuite.api.validator.Validator;
import at.ac.tuwien.big.testsuite.impl.BaseTest;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResult;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResultType;
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Christian Beikov
 */
public class ValidatorTest extends BaseTest {

    @Inject
    private Instance<Validator> validators;

    private ValidationResult validate(File file) throws Exception {
        List<ValidationResultEntry> validationResultEntries = new ArrayList<>();

        for (Validator validator : validators) {
            boolean validate = false;

            for (String fileToValidate : validator.filesToValidate(TestsuiteConstants.EX_ID_LAB1)) {
                if (fileToValidate.substring(fileToValidate.lastIndexOf('/') + 1).equals(file.getName())) {
                    validate = true;
                    break;
                }
            }

            if (validate) {
                validationResultEntries.addAll(validator.validate(file, TestsuiteConstants.EX_ID_LAB1).getEntries());
            }
        }

        return new DefaultValidationResult("All Validators", file.getName(), new DefaultValidationResultType("All"), validationResultEntries);
    }

    @Test
    public void testRegisterOk_shouldContainNoEntries() throws Exception {
        ValidationResult result = validate(getFile("xhtml/register.html"));
        assertTrue(result.getEntries().isEmpty());
    }

    @Test
    public void testTableOk_shouldContainNoEntries() throws Exception {
        ValidationResult result = validate(getFile("xhtml/table.html"));
        assertTrue(result.getEntries().isEmpty());
    }

    @Test
    public void testScreenOk_shouldContainNoEntries() throws Exception {
        ValidationResult result = validate(getFile("css/screen.css"));
        assertTrue(result.getEntries().isEmpty());
    }
}
