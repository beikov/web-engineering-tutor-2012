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

import at.ac.tuwien.big.testsuite.api.model.ExportReport;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Christian
 */
public class DefaultExportReport implements ExportReport, Serializable {

    private final Collection<Exception> exceptions;

    public DefaultExportReport(Collection<Exception> exceptions) {
        this.exceptions = exceptions;
    }

    @Override
    public Collection<Exception> getExceptions() {
        return exceptions;
    }
}
