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
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntryType;
import at.ac.tuwien.big.testsuite.impl.BaseTest;
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import javax.inject.Inject;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Florian Zoubek
 */
public class CssValidatorTest extends BaseTest {

    @Inject
    private CssValidator validator;

    @Test
    public void testScreenOk_shouldContainNoEntries() throws Exception {
        ValidationResult result = validator.validate(getFile("css/screen.css"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(result.getEntries().isEmpty());
    }

    @Test
    public void testScreenOK_shouldContainErrorEntries() throws Exception {
        ValidationResult result = validator.validate(getFile("css/screenVendorSpecificProperties.css"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(!result.getEntries().isEmpty());
        assertEquals(result.getEntries().iterator().next().getResultEntryType(), ValidationResultEntryType.ERROR);
    }

    @Test
    public void testScreenOK_shouldContainWarningEntries() throws Exception {
        ValidationResult result = validator.validate(getFile("css/screenWarnings.css"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(!result.getEntries().isEmpty());
        assertEquals(result.getEntries().iterator().next().getResultEntryType(), ValidationResultEntryType.WARNING);
    }
}