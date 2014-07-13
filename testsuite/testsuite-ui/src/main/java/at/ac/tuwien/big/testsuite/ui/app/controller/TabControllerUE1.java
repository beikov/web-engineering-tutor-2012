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
package at.ac.tuwien.big.testsuite.ui.app.controller;

import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import at.ac.tuwien.big.testsuite.api.model.ValidationReport;
import at.ac.tuwien.big.testsuite.api.task.LeafTask;
import at.ac.tuwien.big.testsuite.api.task.ParameterizableTask;
import java.io.File;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Florian Zoubek
 */
@ApplicationScoped
public class TabControllerUE1 extends AbstractTabController {

    private static final Logger LOG = Logger.getLogger(TabControllerUE1.class.getName());

    public void validateAll() {
        String filePath = zipFileTextField.getText();
        String directoryPath = targetDirTextField.getText();

        final File file = new File(filePath);
        final File outputDir = new File(directoryPath);

        if (file.exists() && outputDir.exists()) {
            errorMessageLabel.setVisible(false);
            successMessageLabel.setVisible(false);
            mainFormPane.setVisible(false);
            progressInformationPane.setVisible(true);
            progressLabel.setVisible(true);
            progressIndicator.setVisible(true);

            LeafTask<Void> parentTask = createParentTask();
            LeafTask<File> unzipTask = unzipService.unzip(file, parentTask)
                    .setOnScheduled(onUnzipStart)
                    .setOnFailed(onUzipFailed);
            ParameterizableTask<ValidationReport, File> validationTask = validatorService.validateAll(outputDir, TestsuiteConstants.EX_ID_LAB1, parentTask)
                    .setParameter(unzipTask)
                    .setOnScheduled(onValidationStart)
                    .setOnFailed(onValidationFailed)
                    .setOnTaskAdded(onGroupValidationTaskAdded(TestsuiteConstants.EX_ID_LAB1));

            parentTask.addTask(unzipTask)
                    .addTask(validationTask)
                    .start(executorService);
        } else {
            successMessageLabel.setVisible(false);
            errorMessageLabel.setVisible(true);
            errorMessageLabel.setText("Invalid file specified!");
        }
    }
}
