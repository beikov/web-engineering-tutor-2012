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
 * @author Christian
 */
public class XhtmlValidatorTest extends BaseTest {

    @Inject
    private XhtmlValidator validator;

    @Test
    public void testOk_validateShouldContainNoEntries() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/table.html"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(result.getEntries().isEmpty());
    }

    @Test
    public void testARIA_validateShouldContainNoEntries() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/registerARIA.html"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(result.getEntries().size() == 1); // 1 false positive expected
    }

    @Test
    public void testMissingTitle_validateShouldContainError() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/tableMissingTitle.html"), TestsuiteConstants.EX_ID_LAB1);
        assertFalse(result.getEntries().isEmpty());
        assertEquals(ValidationResultEntryType.ERROR, result.getEntries().iterator().next().getResultEntryType());
    }

    @Test
    public void testXhtml10_validateShouldContainDoctypeError() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/tableXhtml10.html"), TestsuiteConstants.EX_ID_LAB1);
        assertFalse(result.getEntries().isEmpty());
        assertEquals(ValidationResultEntryType.ERROR, result.getEntries().iterator().next().getResultEntryType());
        assertTrue(result.getEntries().iterator().next().getTitle().toLowerCase().contains("doctype"));
    }
}