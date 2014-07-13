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

import at.ac.tuwien.big.testsuite.api.exporter.ExportCompleteListener;
import at.ac.tuwien.big.testsuite.api.exporter.ExporterFactory;
import at.ac.tuwien.big.testsuite.api.model.ExportReport;
import at.ac.tuwien.big.testsuite.api.model.GroupValidationResult;
import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.LeafTask;
import at.ac.tuwien.big.testsuite.api.task.ParameterizableTask;
import at.ac.tuwien.big.testsuite.impl.model.DefaultExportReport;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Christian
 */
public class ExportTaskImpl extends ParameterizableTask<ExportReport, GroupValidationResult> {

    private final Collection<ExporterFactory> exporterFactories;
    private final String exerciseId;

    public ExportTaskImpl(Collection<ExporterFactory> exportFactories, String exerciseId) {
        this.exporterFactories = exportFactories;
        this.exerciseId = exerciseId;
    }

    public ExportTaskImpl(Collection<ExporterFactory> exportFactories, String exerciseId, HierarchicalTask<?, ?> parent) {
        super(parent);
        this.exporterFactories = exportFactories;
        this.exerciseId = exerciseId;
    }

    @Override
    protected ExportReport call(GroupValidationResult groupResult) throws Exception {
        final Collection<Exception> exceptions = new CopyOnWriteArrayList<>();
        final ExportReport exportReport = new DefaultExportReport(exceptions);

        final File groupBaseDir = groupResult.getBaseDirectory();
        final File groupTargetBaseDir = groupResult.getTargetDirectory();

        if (!groupTargetBaseDir.exists() && !groupTargetBaseDir.mkdir()) {
            throw new IllegalArgumentException("Target directory does not exist and could not be created");
        } else if (!groupTargetBaseDir.isDirectory()) {
            throw new IllegalArgumentException("Target directory is no directory");
        }


        for (final ExporterFactory exporterFactory : exporterFactories) {
            Collection<Callable<Void>> exportTasks = exporterFactory.create(groupResult, groupBaseDir, groupTargetBaseDir, exerciseId, new ExportCompleteListener() {
                @Override
                public void onComplete(Collection<Exception> newExceptions) {
                    exceptions.addAll(newExceptions);
                }
            });
            for (final Callable<Void> exportTask : exportTasks) {
                addTask(new LeafTask<Void>(this) {
                    @Override
                    protected Void call() throws Exception {
                        return exportTask.call();
                    }
                });
            }
        }

        return exportReport;
    }
}
