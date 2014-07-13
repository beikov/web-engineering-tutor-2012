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
package at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab3;

import at.ac.tuwien.big.testsuite.api.model.ValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntry;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2.SeleniumChecklistCheck;
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;

/**
 * Checks if any game errors occured (except restart errors)
 * 
 * @author Florian Zoubek
 */
public class GameRunsChecklistCheck extends SeleniumChecklistCheck{

    private float points = 1.0f;
    
    @Override
    public void passValidationResult(ValidationResult validationResult) {
        //No op
    }

    @Override
    public void passValidationResultEntry(ValidationResultEntry validationResultEntry) {
        if (inSeleniumResultType) {
            String title = validationResultEntry.getTitle();
            if (title != null) {
                if ((title.startsWith(TestsuiteConstants.KNOWN_ERROR_PREFIX + " Incorrect Player position") 
                        || validationResultEntry.getTitle().startsWith("Incorrect Computer position")
                        || title.startsWith(TestsuiteConstants.KNOWN_ERROR_PREFIX + " Computer couldn't win")
                        || title.startsWith(TestsuiteConstants.KNOWN_ERROR_PREFIX + " Player couldn't win")
                        || title.startsWith(TestsuiteConstants.KNOWN_ERROR_PREFIX + " Round count") 
                        || title.startsWith(TestsuiteConstants.KNOWN_ERROR_PREFIX + " Incorrect Leader")
                        || title.startsWith(TestsuiteConstants.KNOWN_ERROR_PREFIX + " Oily field never reached")
                        || title.startsWith(TestsuiteConstants.KNOWN_ERROR_PREFIX+" Normal test")
                        || title.startsWith(TestsuiteConstants.KNOWN_ERROR_PREFIX+" Dice value"))
                        || !title.startsWith(TestsuiteConstants.KNOWN_ERROR_PREFIX)) {
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
        return "Gameplay works as expected";
    }
    
}
