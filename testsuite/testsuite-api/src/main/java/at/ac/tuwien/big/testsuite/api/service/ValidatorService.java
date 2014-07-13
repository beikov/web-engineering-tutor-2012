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
package at.ac.tuwien.big.testsuite.api.service;

import at.ac.tuwien.big.testsuite.api.model.GroupValidationResult;
import at.ac.tuwien.big.testsuite.api.model.MavenGroupReport;
import at.ac.tuwien.big.testsuite.api.model.SeleniumReport;
import at.ac.tuwien.big.testsuite.api.model.ValidationReport;
import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.LeafTask;
import at.ac.tuwien.big.testsuite.api.task.ParameterizableTask;
import java.io.File;

/**
 *
 * @author Christian
 */
public interface ValidatorService {

    public LeafTask<GroupValidationResult> validateSingle(String groupName, File groupBaseDir, File groupTargetBaseDir, String exerciseId, HierarchicalTask<?, ?> parent);

    public ParameterizableTask<ValidationReport, File> validateAll(final File targetDir, final String exerciseId, HierarchicalTask<?, ?> parent);

    public ParameterizableTask<SeleniumReport, MavenGroupReport> testSingle(String exerciseId, HierarchicalTask<?, ?> parent);
}
