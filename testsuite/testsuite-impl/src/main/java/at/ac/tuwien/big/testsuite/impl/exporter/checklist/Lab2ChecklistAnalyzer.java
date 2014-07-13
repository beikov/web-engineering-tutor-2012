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

import at.ac.tuwien.big.testsuite.api.model.GroupValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntry;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultType;
import at.ac.tuwien.big.testsuite.impl.exporter.ReportWriter;
import at.ac.tuwien.big.testsuite.impl.exporter.ValidationResultAnalyzer;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.ChecklistPointCheck;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2.CarMovementChecklistCheck;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2.ComputerWinsChecklistCheck;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2.ConcurrentTestChecklistCheck;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2.GameInformationDuringGameChecklistCheck;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2.HtmlCSSErrorsChecklistCheck;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2.OilyMoveChecklistCheck;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2.PlayerWinsChecklistCheck;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2.RestartChecklistCheck;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2.StartupGameInformationChecklistCheck;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2.ValidDiceValueChecklistCheck;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2.WaiErrorsChecklistCheck;
import at.ac.tuwien.big.testsuite.impl.exporter.checklist.lab2.WelcomeFileChecklistCheck;
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

/**
 * This analyzer produces a point list according to the checklist for lab2
 *
 * @author Florian Zoubek
 */
@ApplicationScoped
public class Lab2ChecklistAnalyzer implements ValidationResultAnalyzer {

    private ChecklistPointCheck[] checks;

    @Override
    public boolean isResponsibleFor(String exerciseId) {
        return exerciseId.equals(TestsuiteConstants.EX_ID_LAB2);
    }

    private void initializePointChecks() {
        checks = new ChecklistPointCheck[]{
            new ConcurrentTestChecklistCheck(), // A1
            new HtmlCSSErrorsChecklistCheck(), // A2
            new WaiErrorsChecklistCheck(), // A3
            new StartupGameInformationChecklistCheck(), // A4
            new ValidDiceValueChecklistCheck(), // A5
            new CarMovementChecklistCheck(), // A6
            new GameInformationDuringGameChecklistCheck(), // A7
            new RestartChecklistCheck(), // A8
            new OilyMoveChecklistCheck(), // A9
            new PlayerWinsChecklistCheck(), // A10
            new ComputerWinsChecklistCheck(), // A11
            new WelcomeFileChecklistCheck()}; // A12
    }

    @Override
    public void analyze(GroupValidationResult validationResult, ReportWriter writer) {

        Map<ValidationResultType, Collection<ValidationResult>> resultMap = validationResult.getResults();
        float points[] = {1.0f, 1.0f, 1.0f, .5f, 1.0f, 1.0f, .5f, 1.0f, 1.0f, .5f, .5f, 1.0f};

        initializePointChecks();

        // start traversal

        for (ValidationResultType resultType : resultMap.keySet()) {

            for (ChecklistPointCheck check : checks) {
                check.passValidationResultType(resultType);
            }

            for (ValidationResult singleValidationResult : resultMap.get(resultType)) {

                for (ChecklistPointCheck check : checks) {
                    check.passValidationResult(singleValidationResult);
                }

                for (ValidationResultEntry validationEntry : singleValidationResult.getEntries()) {

                    for (ChecklistPointCheck check : checks) {
                        check.passValidationResultEntry(validationEntry);
                    }
                }
            }
        }

        float sum = 0;
        for (int i = 0; i < checks.length; i++) {
            sum += checks[i].getPoints();
        }
        try {
            writer.newLine();
            writer.writeLine(0, "Calculated points: " + sum + " point(s)");
            writer.writeSeparator('-');

            for (int i = 0; i < checks.length; i++) {
                writer.writeLine(0, "A" + (i + 1) + " " +checks[i].getDescription() + " : " + checks[i].getPoints()
                        + " point(s)");
            }
        } catch (IOException ex) {
            Logger.getLogger(Lab2ChecklistAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
