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
package at.ac.tuwien.big.testsuite.impl.exporter;

import at.ac.tuwien.big.testsuite.api.model.GroupValidationResult;
import at.ac.tuwien.big.testsuite.api.exporter.GroupValidationResultExporter;
import at.ac.tuwien.big.testsuite.api.model.ValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntry;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author Christian, Florian Zoubek
 */
public class GroupValidationResultExporterImpl implements GroupValidationResultExporter {

    private static final Logger LOG = Logger.getLogger(GroupValidationResultExporterImpl.class.getName());
    
    @Inject Instance<ValidationResultAnalyzer> analyzers;

    @Override
    public void export(GroupValidationResult validationResult, File baseDirectory, File targetDirectory, String exerciseId) {
        File groupValidationFile = new File(targetDirectory, "report-group-" + validationResult.getGroup() + ".txt");

        try (BufferedWriter bwriter = new BufferedWriter(new FileWriter(groupValidationFile))) {
            
            ReportWriter writer = new BufferedReportWriter(bwriter);
            
            Map<ValidationResultType, Collection<ValidationResult>> resultMap = validationResult.getResults();

            writer.writeSeparator('=');
            writer.newLine();
            writer.writeLine(0,"  Web Engineering - validation report");
            writer.writeLine(0,"  Group: " + validationResult.getGroup());
            writer.writeLine(0,"  Created: " + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(GregorianCalendar.getInstance().getTime()));
            writer.newLine();
            writer.writeSeparator('=');

            for(ValidationResultAnalyzer analyzer : analyzers){
                if(analyzer.isResponsibleFor(exerciseId)){
                    analyzer.analyze(validationResult, writer);
                }
            }

            for (ValidationResultType resultType : resultMap.keySet()) {
                writer.newLine();

                writer.writeSeparator('_');
                writer.newLine();
                writer.writeLine(0, resultType.getType());
                writer.writeSeparator('_');

                Collection<ValidationResult> validationResults = resultMap.get(resultType);

                int numValidations = 0;

                for (ValidationResult singleValidationResult : validationResults) {
                    if (!singleValidationResult.getEntries().isEmpty()) {
                        writer.writeLine(0, singleValidationResult.getFileName());

                        for (ValidationResultEntry validationEntry : singleValidationResult.getEntries()) {
                            writer.writeLine(1, validationEntry.getResultEntryType().toString());
                            writer.writeLine(2, validationEntry.getTitle() == null ? "No title given" : validationEntry.getTitle());
                            writer.writeLine(3, validationEntry.getDescription() == null ? "No description given" : validationEntry.getDescription());
                            numValidations++;
                            writer.writeSeparator('-');
                        }
                    }
                }

                if (numValidations < 1) {
                    writer.newLine();
                    writer.writeLine(1, "No validation errors/warnings");
                    writer.writeSeparator('-');
                    continue;
                }

            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Failed to write report of Group \"" + validationResult.getGroup() + "\" to file", ex);
        }
    }
}
