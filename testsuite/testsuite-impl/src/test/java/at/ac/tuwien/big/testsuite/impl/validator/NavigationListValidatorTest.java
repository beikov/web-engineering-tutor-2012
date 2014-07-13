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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Florian Zoubek
 */
public class NavigationListValidatorTest extends BaseTest {

    @Inject
    private NavigationListValidator validator;

    @Test
    public void testNavigationList_shouldContainNoEntries() throws Exception {

        ValidationResult result = validator.validate(getFile("xhtml/table.html"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(result.getEntries().isEmpty());

        result = validator.validate(getFile("xhtml/register.html"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(result.getEntries().isEmpty());
    }

    @Test
    public void testNavigationList_shouldContainErrorEntries() throws Exception {

        ValidationResult result = validator.validate(getFile("xhtml/tableNonListNavigation.html"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(!result.getEntries().isEmpty());
        assertEquals(result.getEntries().size(), 2);
        assertEquals(ValidationResultEntryType.ERROR, result.getEntries().iterator().next().getResultEntryType());

        result = validator.validate(getFile("xhtml/registerNonListNavigation.html"), TestsuiteConstants.EX_ID_LAB1);
        assertTrue(!result.getEntries().isEmpty());
        assertEquals(result.getEntries().size(), 1);
        assertEquals(ValidationResultEntryType.ERROR, result.getEntries().iterator().next().getResultEntryType());
    }
}
