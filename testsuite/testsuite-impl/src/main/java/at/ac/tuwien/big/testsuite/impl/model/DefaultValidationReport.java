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
import at.ac.tuwien.big.testsuite.api.model.ValidationReport;
import at.ac.tuwien.big.testsuite.api.model.ValidationResult;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Florian Zoubek, Christian Beikov
 */
public class DefaultValidationReport implements ValidationReport {

    private final Map<String, DefaultGroupValidationResult> groupResults = new ConcurrentHashMap<>();

    @Override
    public Map<String, GroupValidationResult> getGroupResults() {
        return (Map<String, GroupValidationResult>) (Map<String, ? extends GroupValidationResult>) new HashMap<>(groupResults);
    }

    public DefaultGroupValidationResult forGroup(GroupValidationResult group) {
        final String groupName = group.getGroup();
        DefaultGroupValidationResult groupValidationResult = groupResults.get(groupName);

        if (groupValidationResult == null) {
            groupValidationResult = new DefaultGroupValidationResult(groupName, group.getBaseDirectory(), group.getTargetDirectory());
            groupResults.put(groupName, groupValidationResult);
        }

        return groupValidationResult;
    }

    public GroupValidationResult addGroupValidationResult(GroupValidationResult result) {
        DefaultGroupValidationResult groupValidationResult = forGroup(result);
        for (Collection<ValidationResult> validationResults : result.getResults().values()) {
            groupValidationResult.addResults(validationResults);
        }
        return groupValidationResult;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final ValidationReport other = (ValidationReport) obj;
        if (!Objects.equals(this.groupResults, other.getGroupResults())) {
            return false;
        }
        return true;
    }
}
