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
package at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2;

import at.ac.tuwien.big.testsuite.api.model.ValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntry;
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;

/**
 * Checks for occurance of restart test errors
 *
 * @author Florian Zoubek
 */
public class RestartChecklistCheck extends SeleniumChecklistCheck {

    private float points = 1.0f;

    @Override
    public void passValidationResult(ValidationResult validationResult) {
        // No op
    }

    @Override
    public void passValidationResultEntry(ValidationResultEntry validationResultEntry) {
        if (inSeleniumResultType) {
            String title = validationResultEntry.getTitle();
            if (title != null) {
                if ((title.startsWith(TestsuiteConstants.KNOWN_ERROR_PREFIX + " Player position not 0 after restart") || title.startsWith(TestsuiteConstants.KNOWN_ERROR_PREFIX + " Computer position not 0 after restart")) || !title.startsWith(TestsuiteConstants.KNOWN_ERROR_PREFIX)) {
                    if (points > 0) {
                        points = 0;
                    }
                }
            }
        }
    }

    @Override
    public float getPoints() {
        return points;
    }

    @Override
    public String getDescription() {
        return "Restart is possible";
    }
}
