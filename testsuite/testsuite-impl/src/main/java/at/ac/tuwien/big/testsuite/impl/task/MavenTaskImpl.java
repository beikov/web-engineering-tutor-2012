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

import at.ac.tuwien.big.testsuite.api.model.MavenReport;
import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.ParameterizableTask;
import at.ac.tuwien.big.testsuite.impl.model.DefaultMavenReport;
import at.ac.tuwien.big.testsuite.impl.util.GroupUtils;
import java.io.File;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Christian
 */
public class MavenTaskImpl extends ParameterizableTask<MavenReport, File> {

    private final File targetDirectory;

    public MavenTaskImpl(File targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public MavenTaskImpl(File targetDirectory, HierarchicalTask<?, ?> parent) {
        super(parent);
        this.targetDirectory = targetDirectory;
    }

    @Override
    protected MavenReport call(File baseDirectory) throws Exception {
        final Map<String, File> groupDirectoryMap = GroupUtils.getMavenStyleGroups(baseDirectory);

        // We have not considered collecting results yet, so this is just to make it work
        MavenReport mavenReport = new DefaultMavenReport(Collections.<Exception>emptyList());

        for (final Map.Entry<String, File> entry : groupDirectoryMap.entrySet()) {
            final String groupName = entry.getKey();
            final File groupBaseDir = entry.getValue();
            final File groupTargetBaseDir = new File(targetDirectory, groupName);

            addTask(new MavenGroupTaskImpl(groupName, groupBaseDir, groupTargetBaseDir, this));
        }

        return mavenReport;
    }

    public File getTargetDir() {
        return targetDirectory;
    }
}
