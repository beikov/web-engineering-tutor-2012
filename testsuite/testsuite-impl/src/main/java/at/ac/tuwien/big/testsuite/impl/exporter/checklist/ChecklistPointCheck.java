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
package at.ac.tuwien.big.testsuite.impl.exporter.checklist;

import at.ac.tuwien.big.testsuite.api.model.ValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntry;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultType;

/**
 * Base class for checkers used to calculate the amount of points for a specific
 * checklist entry.
 *
 * <p>For performance reasons the traversal is not done within the methods in
 * this class, instead it's notified of the objects passed during the traversal
 * by calling the passXX-methods. The implementation should calculate the points
 * based on the passXX calls, however, the correct number of points is only
 * available after the traversal is complete.</p>
 *
 * @author Florian Zoubek
 */
public interface ChecklistPointCheck {

    /**
     * called if the traversal passes a ValidationResultType
     *
     * @param resultType the passed ValidationResultType
     */
    public void passValidationResultType(ValidationResultType resultType);

    /**
     * called if the traversal passes a ValidationResult
     *
     * @param validationResult the passed ValidationResult
     */
    public void passValidationResult(ValidationResult validationResult);

    /**
     * called if the traversal passes a ValidationResultEntry
     *
     * @param validationResultEntry the passed ValidationResultEntry
     */
    public void passValidationResultEntry(ValidationResultEntry validationResultEntry);

    /**
     * returns the calculated points. The point value is correct only if the
     * traversal has been finished completly.
     *
     * @return the points at this state
     */
    public float getPoints();
    
    /**
     * returns a single-line description of this check
     * @return the description
     */
    public String getDescription();
}
