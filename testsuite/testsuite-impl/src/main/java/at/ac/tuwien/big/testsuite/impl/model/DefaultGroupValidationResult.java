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
package at.ac.tuwien.big.testsuite.impl.model;

import at.ac.tuwien.big.testsuite.api.model.GroupValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultType;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author Florian Zoubek, Christian Beikov
 */
public class DefaultGroupValidationResult implements GroupValidationResult {

    private final String group;
    private final File baseDirectory;
    private final File targetDirectory;
    private final ConcurrentMap<ValidationResultType, Collection<ValidationResult>> results = new ConcurrentHashMap<>();

    public DefaultGroupValidationResult(String group, File baseDirectory, File targetDirectory) {
        this.group = group;
        this.baseDirectory = baseDirectory;
        this.targetDirectory = targetDirectory;
    }

    public void addResults(Collection<ValidationResult> newValidationResults) {
        for (ValidationResult validationResult : newValidationResults) {
            addResult(validationResult);
        }
    }

    public void addResult(ValidationResult validationResult) {
        getOrCreateValidationResults(validationResult.getType()).add(validationResult);
    }

    private Collection<ValidationResult> getOrCreateValidationResults(ValidationResultType type) {
        Collection<ValidationResult> validationResults = results.get(type);

        if (validationResults == null) {
            validationResults = Collections.newSetFromMap(new ConcurrentHashMap<ValidationResult, Boolean>());

            final Collection<ValidationResult> temp = results.putIfAbsent(type, validationResults);

            if (temp != null) {
                validationResults = temp;
            }
        }

        return validationResults;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public File getBaseDirectory() {
        return baseDirectory;
    }

    @Override
    public File getTargetDirectory() {
        return targetDirectory;
    }

    @Override
    public Map<ValidationResultType, Collection<ValidationResult>> getResults() {
        return Collections.unmodifiableMap(new HashMap<>(results));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.group);
        hash = 67 * hash + Objects.hashCode(this.results);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GroupValidationResult other = (GroupValidationResult) obj;
        if (!Objects.equals(this.group, other.getGroup())) {
            return false;
        }
        if (!Objects.equals(this.results, other.getResults())) {
            return false;
        }
        return true;
    }
}
