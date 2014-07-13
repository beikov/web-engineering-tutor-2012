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

import at.ac.tuwien.big.testsuite.api.model.ValidationResultType;
import java.util.Objects;

/**
 *
 * @author Florian Zoubek, Christian Beikov
 */
public class DefaultValidationResultType implements ValidationResultType {

    private final String type;

    public DefaultValidationResultType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ValidationResultType)) {
            return false;
        }
        final ValidationResultType other = (ValidationResultType) obj;
        if (!Objects.equals(this.type, other.getType())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DefaultValidationResultType{" + "type=" + type + '}';
    }
}
