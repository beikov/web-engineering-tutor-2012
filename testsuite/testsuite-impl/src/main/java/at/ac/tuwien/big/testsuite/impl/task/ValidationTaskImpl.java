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
package at.ac.tuwien.big.testsuite.impl.task;

import at.ac.tuwien.big.testsuite.api.model.ValidationReport;
import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.ParameterizableTask;
import at.ac.tuwien.big.testsuite.api.validator.ValidatorFactory;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationReport;
import at.ac.tuwien.big.testsuite.impl.util.GroupUtils;
import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Christian
 */
public class ValidationTaskImpl extends ParameterizableTask<ValidationReport, File> {

    private final File targetDir;
    private final String exerciseId;
    private final Collection<ValidatorFactory> validatorFactories;

    public ValidationTaskImpl(File targetDir, String exerciseId, Collection<ValidatorFactory> validatorFactories) {
        this.targetDir = targetDir;
        this.exerciseId = exerciseId;
        this.validatorFactories = validatorFactories;
    }

    public ValidationTaskImpl(File targetDir, String exerciseId, Collection<ValidatorFactory> validatorFactories, HierarchicalTask<?, ?> parent) {
        super(parent);
        this.targetDir = targetDir;
        this.exerciseId = exerciseId;
        this.validatorFactories = validatorFactories;
    }

    @Override
    protected ValidationReport call(File baseDirectory) throws Exception {
        if (!targetDir.exists() && !targetDir.mkdir()) {
            throw new IllegalArgumentException("Target directory does not exist and could not be created");
        } else if (!targetDir.isDirectory()) {
            throw new IllegalArgumentException("Target directory is no directory");
        }

        final DefaultValidationReport validationReport = new DefaultValidationReport();
        final Map<String, File> groupDirectoryMap = GroupUtils.getGroups(baseDirectory);

        for (final Map.Entry<String, File> entry : groupDirectoryMap.entrySet()) {
            final String groupName = entry.getKey();
            final File groupBaseDir = entry.getValue();
            final File groupTargetBaseDir = new File(targetDir, groupName);

            addTask(new GroupValidationTaskImpl(groupName, groupBaseDir, groupTargetBaseDir, exerciseId, validatorFactories, this));
        }

        return validationReport;
    }

    public File getTargetDir() {
        return targetDir;
    }

    public String getExerciseId() {
        return exerciseId;
    }
}
