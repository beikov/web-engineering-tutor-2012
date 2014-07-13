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

import at.ac.tuwien.big.testsuite.api.model.UnzipReport;
import java.io.File;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Christian
 */
public class DefaultUnzipReport implements UnzipReport, Serializable {

    private final Collection<Exception> exceptions;
    private final File baseDirectory;

    public DefaultUnzipReport(Collection<Exception> exceptions, File baseDirectory) {
        this.exceptions = exceptions;
        this.baseDirectory = baseDirectory;
    }

    @Override
    public File getBaseDirectory() {
        return baseDirectory;
    }

    @Override
    public Collection<Exception> getExceptions() {
        return exceptions;
    }
}
