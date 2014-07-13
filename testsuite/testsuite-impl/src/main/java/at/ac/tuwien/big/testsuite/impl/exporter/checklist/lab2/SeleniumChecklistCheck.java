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

import at.ac.tuwien.big.testsuite.api.model.ValidationResultType;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.ChecklistPointCheck;

/**
 *
 * @author Florian Zoubek 
 */
public abstract class SeleniumChecklistCheck implements ChecklistPointCheck{
    
    protected boolean inSeleniumResultType = false;

    @Override
    public void passValidationResultType(ValidationResultType resultType) {
        inSeleniumResultType = resultType.getType().equalsIgnoreCase("Selenium");
    }
    
}