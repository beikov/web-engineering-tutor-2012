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

import at.ac.tuwien.big.testsuite.api.model.ExportReport;
import at.ac.tuwien.big.testsuite.api.model.GroupValidationResult;
import at.ac.tuwien.big.testsuite.api.model.MavenGroupReport;
import at.ac.tuwien.big.testsuite.api.model.ProgressStatus;
import at.ac.tuwien.big.testsuite.api.model.SeleniumReport;
import at.ac.tuwien.big.testsuite.api.model.ValidationResult;
import at.ac.tuwien.big.testsuite.api.model.ValidationResultType;
import at.ac.tuwien.big.testsuite.api.service.ApplicationSettingsService;
import at.ac.tuwien.big.testsuite.api.service.ExportService;
import at.ac.tuwien.big.testsuite.api.service.UnzipService;
import at.ac.tuwien.big.testsuite.api.service.ValidatorService;
import at.ac.tuwien.big.testsuite.api.task.EventHandler;
import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.LeafTask;
import at.ac.tuwien.big.testsuite.api.task.ParameterizableTask;
import at.ac.tuwien.big.testsuite.impl.task.ExportTaskImpl;
import at.ac.tuwien.big.testsuite.impl.task.MavenGroupTaskImpl;
import at.ac.tuwien.big.testsuite.ui.app.event.FxEventHandlers;
import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javax.inject.Inject;
import org.tbee.javafx.scene.layout.fxml.MigPane;

/**
 *
 * @author Christian
 */
public abstract class AbstractTabController implements CdiController<AbstractTabController>, Initializable, Serializable {

    private static final Logger LOG = Logger.getLogger(AbstractTabController.class.getName());
    @FXML
    protected Label errorMessageLabel;
    @FXML
    protected MigPane mainFormPane;
    @FXML
    protected ProgressBar progressIndicator;
    @FXML
    protected MigPane progressInformationPane;
    @FXML
    protected Label progressLabel;
    @FXML
    protected Label successMessageLabel;
    @FXML
    protected TextField targetDirTextField;
    @FXML
    protected TextField zipFileTextField;
    @Inject
    protected UnzipService unzipService;
    @Inject
    protected ValidatorService validatorService;
    @Inject
    protected ExportService exportService;
    @Inject
    protected ApplicationSettingsService applicationSettings;
    @Inject
    protected ExecutorService executorService;
    protected File lastTargetDir = null;
    protected File lastZipFile = null;
    protected final AtomicReference<ProgressStatus> progressUpdate = new AtomicReference<>();

    @Override
    public AbstractTabController getInstance() {
        return this;
    }
    protected final EventHandler<HierarchicalTask<?, ?>> onUnzipStart = FxEventHandlers.runLater(new EventHandler<HierarchicalTask<?, ?>>() {
        @Override
        public void handle(HierarchicalTask<?, ?> event) {
            progressLabel.setText("Unzipping files...");
        }
    });
    protected final EventHandler<HierarchicalTask<?, ?>> onUzipFailed = FxEventHandlers.runLater(new EventHandler<HierarchicalTask<?, ?>>() {
        @Override
        public void handle(final HierarchicalTask<?, ?> event) {
            LOG.log(Level.SEVERE, "An error occured during unzipping:\n{0}", event.getException() == null ? "" : event.getException().toString());
            errorMessageLabel.setText("An error occured during unzipping");
            errorMessageLabel.setVisible(true);
            mainFormPane.setVisible(true);
            progressInformationPane.setVisible(false);
        }
    });
    protected final EventHandler<HierarchicalTask<?, ?>> onValidationStart = FxEventHandlers.runLater(new EventHandler<HierarchicalTask<?, ?>>() {
        @Override
        public void handle(HierarchicalTask<?, ?> event) {
            progressLabel.setText("Running tests...");
        }
    });
    protected final EventHandler<HierarchicalTask<?, ?>> onValidationFailed = FxEventHandlers.runLater(new EventHandler<HierarchicalTask<?, ?>>() {
        @Override
        public void handle(final HierarchicalTask<?, ?> event) {
            LOG.log(Level.SEVERE, "An error occured during validation:\n{0}", event.getException() == null ? "" : event.getException().toString());
            
            for(Throwable t : event.getChildException().values()) {
                LOG.log(Level.SEVERE, "Child Exception", t);
            }
            
            errorMessageLabel.setText("An error occured during validation");
            errorMessageLabel.setVisible(true);
            mainFormPane.setVisible(true);
            progressInformationPane.setVisible(false);
        }
    });

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSettings();
        if (lastZipFile != null) {
            zipFileTextField.setText(lastZipFile.getAbsolutePath());
        }
        if (lastTargetDir != null) {
            targetDirTextField.setText(lastTargetDir.getAbsolutePath());
        }
    }

    protected LeafTask<Void> createParentTask() {
        return new LeafTask<Void>() {
            @Override
            protected Void call() throws Exception {
                return null;
            }
        }.setOnProgress(new EventHandler<ProgressStatus>() {
            @Override
            public void handle(ProgressStatus event) {
                if (Platform.isFxApplicationThread()) {
                    _updateProgress(event.getDone(), event.getTotal());
                } else if (progressUpdate.getAndSet(event) == null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            final ProgressStatus update = progressUpdate.getAndSet(null);
                            _updateProgress(update.getDone(), update.getTotal());
                        }
                    });
                }
            }

            private void _updateProgress(double workDone, double max) {
                progressIndicator.setProgress(workDone == 0 ? 0 : workDone / max);
            }
        }).setOnSucceeded(FxEventHandlers.runLater(new EventHandler<LeafTask<Void>>() {
            @Override
            public void handle(LeafTask<Void> event) {
                successMessageLabel.setText("Finished exporting test results.");
                successMessageLabel.setVisible(true);
                mainFormPane.setVisible(true);
                progressInformationPane.setVisible(false);
            }
        }));
    }

    protected EventHandler<HierarchicalTask<?, ?>> onGroupValidationTaskAdded(final String labId) {
        return new EventHandler<HierarchicalTask<?, ?>>() {
            @Override
            public void handle(HierarchicalTask<?, ?> groupTask) {
                if (!(groupTask instanceof ExportTaskImpl)) {
                    groupTask.getParent().addTask(
                            /* Schedule Export Task for each Group Validation Task*/
                            exportService.export(labId, groupTask.getParent())
                            .setParameter((Future<GroupValidationResult>) groupTask));
                }
            }
        };
    }

    protected EventHandler<ParameterizableTask<SeleniumReport, MavenGroupReport>> onSeleniumTaskScheduled(final String labId) {
        return new EventHandler<ParameterizableTask<SeleniumReport, MavenGroupReport>>() {
            @Override
            public void handle(final ParameterizableTask<SeleniumReport, MavenGroupReport> event) {
                MavenGroupTaskImpl task = (MavenGroupTaskImpl) event.getParameter();
                LeafTask<GroupValidationResult> validationTask = validatorService.validateSingle(task.getGroupName(), task.getGroupBaseDir(), task.getGroupTargetBaseDir(), labId, task.getParent())
                        .setOnScheduled(new EventHandler<LeafTask<GroupValidationResult>>() {
                    @Override
                    public void handle(LeafTask<GroupValidationResult> ignore) {
                        try {
                            /* 
                             * Wait for the Selenium Task to be completed before we continue with validation.
                             * We could also make a ParameterizableTask but we don't want to affect normal validation.
                             */
                            event.get();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
                ParameterizableTask<ExportReport, GroupValidationResult> exportTask = exportService.export(labId, task.getParent())
                        .setParameter(new SeleniumAwareGroupValidationResultFuture(validationTask, event));
                task.getParent()
                        .addTask(validationTask)
                        .addTask(exportTask);
            }
        };
    }

    protected EventHandler<HierarchicalTask<?, ?>> onMavenGroupTaskAdded(final String labId) {
        return onMavenGroupTaskAdded(labId, onSeleniumTaskScheduled(labId));
    }

    protected EventHandler<HierarchicalTask<?, ?>> onMavenGroupTaskAdded(final String labId, final EventHandler<ParameterizableTask<SeleniumReport, MavenGroupReport>> onSeleniumTaskScheduled) {
        return new EventHandler<HierarchicalTask<?, ?>>() {
            @Override
            public void handle(HierarchicalTask<?, ?> buildTask) {
                if (buildTask instanceof MavenGroupTaskImpl) {
                    MavenGroupTaskImpl task = (MavenGroupTaskImpl) buildTask;
                    task.getParent().addTask(
                            /* Schedule Selenium Task for each MavenGroupTask*/
                            validatorService.testSingle(labId, buildTask.getParent())
                            .setParameter(task)
                            .setOnScheduled(onSeleniumTaskScheduled));
                }
            }
        };
    }

    protected void loadSettings() {
        String lastZipFilePath = applicationSettings.get("lastzipfile");
        if (lastZipFilePath != null) {
            lastZipFile = new File(lastZipFilePath);
        }
        String lastOutputDirPath = applicationSettings.get("lastoutputdir");
        if (lastOutputDirPath != null) {
            lastTargetDir = new File(lastOutputDirPath);
        }
    }

    public void selectTargetDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (lastTargetDir != null) {
            directoryChooser.setInitialDirectory(lastTargetDir.getParentFile());
        }
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null) {
            lastTargetDir = selectedDirectory;
            applicationSettings.set("lastoutputdir", lastTargetDir.getAbsolutePath());
            targetDirTextField.setText(selectedDirectory.getAbsolutePath());
            errorMessageLabel.setVisible(false);
            successMessageLabel.setVisible(false);
        }
    }

    public void selectZipFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Zip files (*.zip)", "*.zip");
        fileChooser.getExtensionFilters().add(extFilter);
        if (lastZipFile != null) {
            fileChooser.setInitialDirectory(lastZipFile.getParentFile());
        }
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            lastZipFile = selectedFile;
            applicationSettings.set("lastzipfile", lastZipFile.getAbsolutePath());
            zipFileTextField.setText(selectedFile.getAbsolutePath());
            errorMessageLabel.setVisible(false);
            successMessageLabel.setVisible(false);
        }
    }

    private static class SeleniumAwareGroupValidationResultFuture implements Future<GroupValidationResult> {

        private final Future<GroupValidationResult> delegate;
        private final Future<SeleniumReport> seleniumReport;

        public SeleniumAwareGroupValidationResultFuture(Future<GroupValidationResult> delegate, Future<SeleniumReport> seleniumReport) {
            this.delegate = delegate;
            this.seleniumReport = seleniumReport;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return delegate.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return delegate.isCancelled();
        }

        @Override
        public boolean isDone() {
            return delegate.isDone();
        }

        @Override
        public GroupValidationResult get() throws InterruptedException, ExecutionException {
            return decorate(delegate.get());
        }

        @Override
        public GroupValidationResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return decorate(delegate.get(timeout, unit));
        }

        private GroupValidationResult decorate(final GroupValidationResult result) throws InterruptedException, ExecutionException {
            final SeleniumReport report = seleniumReport.get();
            return new GroupValidationResult() {
                @Override
                public File getBaseDirectory() {
                    return result.getBaseDirectory();
                }

                @Override
                public File getTargetDirectory() {
                    return result.getTargetDirectory();
                }

                @Override
                public String getGroup() {
                    return result.getGroup();
                }

                @Override
                public Map<ValidationResultType, Collection<ValidationResult>> getResults() {
                    if (report.getResults().isEmpty()) {
                        return result.getResults();
                    } else {
                        Map<ValidationResultType, Collection<ValidationResult>> map = new HashMap<>(result.getResults());
                        map.putAll(report.getResults());
                        return map;
                    }
                }
            };
        }
    }
}
