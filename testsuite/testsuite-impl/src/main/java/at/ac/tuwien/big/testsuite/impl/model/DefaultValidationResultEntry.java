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

import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntryType;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntry;
import java.util.Objects;

/**
 *
 * @author Florian Zoubek, Christian Beikov
 */
public class DefaultValidationResultEntry implements ValidationResultEntry {

    private final String title;
    private final String description;
    private final ValidationResultEntryType result;

    public DefaultValidationResultEntry(String title, String description, ValidationResultEntryType result) {
        this.title = title;
        this.description = description;
        this.result = result;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ValidationResultEntryType getResultEntryType() {
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.title);
        hash = 97 * hash + Objects.hashCode(this.description);
        hash = 97 * hash + (this.result != null ? this.result.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ValidationResultEntry)) {
            return false;
        }
        final ValidationResultEntry other = (ValidationResultEntry) obj;
        if (!Objects.equals(this.title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(this.description, other.getDescription())) {
            return false;
        }
        if (this.result != other.getResultEntryType()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DefaultValidationResultEntry{" + "title=" + title + ", description=" + description + ", result=" + result + '}';
    }
}
