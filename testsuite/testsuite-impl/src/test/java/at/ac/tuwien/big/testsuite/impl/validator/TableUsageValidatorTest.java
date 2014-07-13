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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public class TableUsageValidatorTest extends BaseTest {

    @Inject
    private TableUsageValidator validator;

    @Test
    public void testOk_validateShouldContainNoErrors() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/table.html"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(result.getEntries().isEmpty());
    }

    @Test
    public void testNoSummary_validateShouldContainTableError() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/tableNoSummary.html"), TestsuiteConstants.EX_ID_LAB1);
        assertFalse(result.getEntries().isEmpty());
        assertEquals(ValidationResultEntryType.ERROR, result.getEntries().iterator().next().getResultEntryType());
        assertTrue(result.getEntries().iterator().next().getTitle().toLowerCase().contains("summary"));
    }

    @Test
    public void testUseThWrong_validateShouldContainTableError() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/tableUseThWrong.html"), TestsuiteConstants.EX_ID_LAB1);
        assertEquals(2, result.getEntries().size());

        Iterator<ValidationResultEntry> entryIterator = result.getEntries().iterator();
        ValidationResultEntry element;

        element = entryIterator.next();
        assertEquals(ValidationResultEntryType.ERROR, element.getResultEntryType());
        assertTrue(element.getTitle().toLowerCase().contains("th-tag"));

        element = entryIterator.next();
        assertEquals(ValidationResultEntryType.ERROR, element.getResultEntryType());
        assertTrue(element.getDescription().toLowerCase().contains("spielinformation"));
    }

    @Test
    public void testUseTdInsteadTh_validateShouldContainTableError() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/tableUseTdInsteadTh.html"), TestsuiteConstants.EX_ID_LAB1);
        assertEquals(3, result.getEntries().size());

        Iterator<ValidationResultEntry> entryIterator = result.getEntries().iterator();
        ValidationResultEntry element;

        element = entryIterator.next();
        assertEquals(ValidationResultEntryType.ERROR, element.getResultEntryType());
        assertTrue(element.getDescription().toLowerCase().contains("no th"));

        element = entryIterator.next();
        assertEquals(ValidationResultEntryType.ERROR, element.getResultEntryType());
        assertTrue(element.getDescription().toLowerCase().contains("no th"));

        element = entryIterator.next();
        assertEquals(ValidationResultEntryType.ERROR, element.getResultEntryType());
        assertTrue(element.getDescription().toLowerCase().contains("spielinformation"));
        assertTrue(element.getDescription().toLowerCase().contains("spieler"));
    }

    @Test
    public void testUseSomeTdInsteadTh_validateShouldContainTableError() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/tableUseSomeTdInsteadTh.html"), TestsuiteConstants.EX_ID_LAB1);
        assertEquals(2, result.getEntries().size());

        Iterator<ValidationResultEntry> entryIterator = result.getEntries().iterator();
        ValidationResultEntry element;

        element = entryIterator.next();
        assertEquals(ValidationResultEntryType.ERROR, element.getResultEntryType());
        assertTrue(element.getDescription().toLowerCase().contains("1 th"));

        element = entryIterator.next();
        assertEquals(ValidationResultEntryType.ERROR, element.getResultEntryType());
        assertTrue(element.getDescription().toLowerCase().contains("1 th"));
    }

    @Test
    public void testMultipleTables_validateShouldContainTableError() throws Exception {
        ValidationResult result = validator.validate(getFile("xhtml/tableMultipleTables.html"), TestsuiteConstants.EX_ID_LAB1);
        assertEquals(2, result.getEntries().size());

        Iterator<ValidationResultEntry> entryIterator = result.getEntries().iterator();
        ValidationResultEntry element;

        element = entryIterator.next();
        assertEquals(ValidationResultEntryType.ERROR, element.getResultEntryType());
        assertTrue(element.getTitle().toLowerCase().contains("too many table elements"));

        element = entryIterator.next();
        assertEquals(ValidationResultEntryType.ERROR, element.getResultEntryType());
        assertTrue(element.getTitle().toLowerCase().contains("th-tag"));
    }
}