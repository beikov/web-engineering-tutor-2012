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
import java.io.BufferedWriter;

/**
 * Base class for analyzers that produce an output based on a given
 * <code>GroupValidationResult</code>. An expample would be an analyzer that
 * calculates points for a report
 *
 * @author Florian Zoubek
 */
public interface ValidationResultAnalyzer {

    /**
     * determines if this Analyzer is able to analyze the report for a specific
     * exercise
     *
     * @param exerciseId the identifier of the exercise to analyze
     * @return true if the analyzer is able to analyze the exercise, false
     * otherwise
     */
    public boolean isResponsibleFor(String exerciseId);

    /**
     * analyzes the given group validation result and outputs the result to the
     * given writer
     *
     * @param validationResult the group validation result to analyze
     * @param writer the writer used to write the result
     */
    public void analyze(GroupValidationResult validationResult, ReportWriter writer);
}
