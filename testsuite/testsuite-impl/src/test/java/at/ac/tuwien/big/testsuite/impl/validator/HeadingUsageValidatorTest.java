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
import at.ac.tuwien.big.testsuite.impl.BaseTest;
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import java.util.Iterator;
import javax.inject.Inject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Florian Zoubek
 */
public class HeadingUsageValidatorTest extends BaseTest {

    @Inject
    private HeadingUsageValidator validator;

    @Test
    public void testHeadingUsage_table_shouldContainNoEntries() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/table.html"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(result.getEntries().isEmpty());
    }

    @Test
    public void testHeadingUsage_register_shouldContainNoEntries() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/register.html"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(result.getEntries().isEmpty());
    }

    @Test
    public void testHeadingUsage_register_shouldContainMismatchError() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/registerH1TitleMismatch.html"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(!result.getEntries().isEmpty());
        assertEquals(result.getEntries().size(), 1);
        assertTrue(result.getEntries().iterator().next().getResultEntryType() == ValidationResultEntryType.ERROR);
        assertTrue(result.getEntries().iterator().next().getTitle().equalsIgnoreCase("Title and h1 text mismatch"));
    }

    @Test
    public void testHeadingUsage_register_shouldContainNestingError() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/registerInvalidHeadingNesting.html"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(!result.getEntries().isEmpty());
        assertEquals(result.getEntries().size(), 2);
        Iterator<ValidationResultEntry> entryIterator = result.getEntries().iterator();
        ValidationResultEntry element;

        element = entryIterator.next();
        assertTrue(element.getResultEntryType() == ValidationResultEntryType.ERROR);
        assertTrue(element.getTitle().equalsIgnoreCase("Invalid nesting of headings"));

        element = entryIterator.next();
        assertTrue(element.getResultEntryType() == ValidationResultEntryType.ERROR);
        assertTrue(element.getTitle().equalsIgnoreCase("Invalid nesting of headings"));
    }
}
