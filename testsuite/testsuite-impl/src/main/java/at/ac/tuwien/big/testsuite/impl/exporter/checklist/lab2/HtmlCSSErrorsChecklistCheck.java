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
import at.ac.tuwien.big.testsuite.api.model.ValidationResultType;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.ChecklistPointCheck;

/**
 * Checks for occurance of XHTML/CSS test errors
 *
 * @author Florian Zoubek
 */
public class HtmlCSSErrorsChecklistCheck implements ChecklistPointCheck {

    private float points = 1.0f;
    private boolean inspectResultType = false;
    private String currentFileName = "";

    @Override
    public void passValidationResultType(ValidationResultType resultType) {
        String type = resultType.getType();

        inspectResultType = type.startsWith("XHTML") || type.startsWith("Table")
                || type.startsWith("Navigation") || type.startsWith("Lang")
                || type.startsWith("Heading") || type.startsWith("Form")
                || type.startsWith("CSS");
    }

    @Override
    public void passValidationResult(ValidationResult validationResult) {
        if (inspectResultType) {
            if (!validationResult.getEntries().isEmpty()) {
                if (points > 0) {
                    points = 0;
                }
            }
        }

        if (validationResult.getFileName() != null) {
            currentFileName = validationResult.getFileName().trim().toLowerCase();
        }
    }

    @Override
    public void passValidationResultEntry(ValidationResultEntry validationResultEntry) {
        if (points > 0) {
            if (validationResultEntry.getTitle() != null && validationResultEntry.getTitle().toLowerCase().contains("file not found")
                    && currentFileName != null
                    && (currentFileName.endsWith("html") || currentFileName.endsWith("css"))) {
                points = 0;
            }
        }
    }

    @Override
    public float getPoints() {
        return points;
    }

    @Override
    public String getDescription() {
        return "No XHTML/CSS errors found";
    }
}
