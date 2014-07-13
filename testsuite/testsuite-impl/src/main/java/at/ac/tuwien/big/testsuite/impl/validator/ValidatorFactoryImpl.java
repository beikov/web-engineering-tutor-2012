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
package at.ac.tuwien.big.testsuite.impl.validator;

import at.ac.tuwien.big.testsuite.api.model.ValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntry;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntryType;
import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.LeafTask;
import at.ac.tuwien.big.testsuite.api.validator.ValidatorCompleteListener;
import at.ac.tuwien.big.testsuite.api.validator.Validator;
import at.ac.tuwien.big.testsuite.api.validator.ValidatorFactory;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResult;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResultEntry;
import at.ac.tuwien.big.testsuite.impl.model.DefaultValidationResultType;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author Christian
 */
public class ValidatorFactoryImpl implements ValidatorFactory {

    private final Validator validator;

    public ValidatorFactoryImpl(Validator validator) {
        this.validator = validator;
    }

    @Override
    public Collection<Callable<ValidationResult>> create(File baseDirectory, final String exerciseId, final ValidatorCompleteListener listener) {
        Collection<Callable<ValidationResult>> validators = new ArrayList<>();

        for (String file : validator.filesToValidate(exerciseId)) {
            final File fileToValidate = new File(baseDirectory, file);

            validators.add(new Callable<ValidationResult>() {
                @Override
                public ValidationResult call() throws Exception {
                    ValidationResult result;

                    try {

                        if (fileToValidate.exists()) {
                            result = validator.validate(fileToValidate, exerciseId);
                        } else if (fileToValidate.getName().equals("screen.css") && new File(fileToValidate.getParentFile(), "style.css").exists()) {
                            // Added special handling since many students did the naming wrong
                            result = validator.validate(new File(fileToValidate.getParentFile(), "style.css"), exerciseId);
                        } else {
                            final List<ValidationResultEntry> validationResultEntries = new ArrayList<>();
                            validationResultEntries.add(new DefaultValidationResultEntry("File not found", "The file " + fileToValidate.getName() + " does not exist", ValidationResultEntryType.ERROR));
                            result = new DefaultValidationResult("General", fileToValidate.getName(), new DefaultValidationResultType(""), validationResultEntries);
                        }
                    } catch (Exception ex) {
                        result = new DefaultValidationResult(fileToValidate.getName(), ex);
                    }

                    listener.onComplete(result);

                    return result;
                }
            });
        }

        return Collections.unmodifiableCollection(validators);
    }

    @Override
    public Collection<LeafTask<ValidationResult>> create(File baseDirectory, final String exerciseId, final HierarchicalTask<?, ?> parent) {
        Collection<LeafTask<ValidationResult>> validators = new ArrayList<>();

        for (String file : validator.filesToValidate(exerciseId)) {
            final File fileToValidate = new File(baseDirectory, file);

            validators.add(new LeafTask<ValidationResult>(parent) {
                @Override
                public ValidationResult call() throws Exception {
                    ValidationResult result;

                    try {
                        if (fileToValidate.exists()) {
                            result = validator.validate(fileToValidate, exerciseId);
                        } else if (fileToValidate.getName().equals("screen.css") && new File(fileToValidate.getParentFile(), "style.css").exists()) {
                            // Added special handling since many students did the naming wrong
                            result = validator.validate(new File(fileToValidate.getParentFile(), "style.css"), exerciseId);
                        } else {
                            final List<ValidationResultEntry> validationResultEntries = new ArrayList<>();
                            validationResultEntries.add(new DefaultValidationResultEntry("File not found", "The file " + fileToValidate.getName() + " does not exist", ValidationResultEntryType.ERROR));
                            result = new DefaultValidationResult("General", fileToValidate.getName(), new DefaultValidationResultType(""), validationResultEntries);
                        }
                    } catch (Exception ex) {
                        result = new DefaultValidationResult(fileToValidate.getName(), ex);
                    }

                    return result;
                }
            });
        }

        return Collections.unmodifiableCollection(validators);
    }
}
