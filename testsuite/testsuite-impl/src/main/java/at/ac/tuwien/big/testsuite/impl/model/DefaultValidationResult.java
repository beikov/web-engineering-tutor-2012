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

import at.ac.tuwien.big.testsuite.api.model.ValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntry;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultEntryType;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultType;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 *
 * @author Florian Zoubek, Christian Beikov
 */
public class DefaultValidationResult implements ValidationResult {

    private final String title;
    private final String fileName;
    private final ValidationResultType type;
    private final Collection<ValidationResultEntry> entries;

    public DefaultValidationResult(String title, String fileName, ValidationResultType type, Collection<ValidationResultEntry> entries) {
        this.title = title;
        this.fileName = fileName;
        this.type = type;
        this.entries = entries;
    }

    public DefaultValidationResult(String fileName, Throwable ex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        ex.printStackTrace(ps);

        this.title = "An exception occurred\n" + baos.toString();
        this.fileName = fileName;
        this.type = new DefaultValidationResultType("Exception");
        this.entries = new ArrayList<>();

        this.entries.add(new DefaultValidationResultEntry("An exception occurred", baos.toString(), ValidationResultEntryType.ERROR));
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public ValidationResultType getType() {
        return type;
    }

    @Override
    public Collection<ValidationResultEntry> getEntries() {
        return entries;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.title);
        hash = 37 * hash + Objects.hashCode(this.entries);
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
        final ValidationResult other = (ValidationResult) obj;
        if (!Objects.equals(this.title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(this.type, other.getType())) {
            return false;
        }
        if (!Objects.equals(this.entries, other.getEntries())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DefaultValidationResult{" + "title=" + title + ", type=" + type + ", entries=" + entries + '}';
    }
}
