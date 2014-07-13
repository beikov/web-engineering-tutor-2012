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

import at.ac.tuwien.big.testsuite.api.model.GroupValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResult;
import at.ac.tuwien.big.testsuite.api.task.EventHandler;
import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.LeafTask;
import at.ac.tuwien.big.testsuite.api.validator.ValidatorFactory;
import at.ac.tuwien.big.testsuite.impl.model.DefaultGroupValidationResult;
import java.io.File;
import java.util.Collection;

/**
 *
 * @author Christian
 */
public class GroupValidationTaskImpl extends LeafTask<GroupValidationResult> {

    private final String groupName;
    private final File groupBaseDir;
    private final File groupTargetBaseDir;
    private final String exerciseId;
    private final Collection<ValidatorFactory> validatorFactories;

    public GroupValidationTaskImpl(String groupName, File groupBaseDir, File groupTargetBaseDir, String exerciseId, Collection<ValidatorFactory> validatorFactories) {
        this(groupName, groupBaseDir, groupTargetBaseDir, exerciseId, validatorFactories, null);
    }

    public GroupValidationTaskImpl(String groupName, File groupBaseDir, File groupTargetBaseDir, String exerciseId, Collection<ValidatorFactory> validatorFactories, HierarchicalTask<?, ?> parent) {
        super(parent);
        this.groupName = groupName;
        this.groupBaseDir = groupBaseDir;
        this.groupTargetBaseDir = groupTargetBaseDir;
        this.exerciseId = exerciseId;
        this.validatorFactories = validatorFactories;
    }

    @Override
    protected GroupValidationResult call() throws Exception {
        if (!groupTargetBaseDir.exists() && !groupTargetBaseDir.mkdir()) {
            throw new IllegalArgumentException("Target group directory does not exist and could not be created");
        }

        final DefaultGroupValidationResult groupValidationResult = new DefaultGroupValidationResult(groupName, groupBaseDir, groupTargetBaseDir);

        for (ValidatorFactory validatorFactory : validatorFactories) {
            for (LeafTask<ValidationResult> validator : validatorFactory.create(groupBaseDir, exerciseId, this)) {
                addTask(validator.setOnComplete(new EventHandler<LeafTask<ValidationResult>>() {
                    @Override
                    public void handle(LeafTask<ValidationResult> event) {
                        groupValidationResult.addResult(event.getValue());
                    }
                }));
            }
        }

        return groupValidationResult;
    }

    public String getGroupName() {
        return groupName;
    }

    public File getGroupBaseDir() {
        return groupBaseDir;
    }

    public File getGroupTargetBaseDir() {
        return groupTargetBaseDir;
    }

    public String getExerciseId() {
        return exerciseId;
    }
}
