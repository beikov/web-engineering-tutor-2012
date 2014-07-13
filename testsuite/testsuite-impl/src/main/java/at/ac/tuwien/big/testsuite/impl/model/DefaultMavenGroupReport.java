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

import at.ac.tuwien.big.testsuite.api.model.MavenGroupReport;
import java.io.File;
import java.io.Serializable;

/**
 *
 * @author Christian
 */
public class DefaultMavenGroupReport implements MavenGroupReport, Serializable {

    private final String group;
    private final String output;
    private final File baseDirectory;
    private final File warFile;

    public DefaultMavenGroupReport(String group, String output, File baseDirectory, File warFile) {
        this.group = group;
        this.output = output;
        this.baseDirectory = baseDirectory;
        this.warFile = warFile;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getOutput() {
        return output;
    }

    @Override
    public File getBaseDirectory() {
        return baseDirectory;
    }

    @Override
    public File getWarFile() {
        return warFile;
    }
}
