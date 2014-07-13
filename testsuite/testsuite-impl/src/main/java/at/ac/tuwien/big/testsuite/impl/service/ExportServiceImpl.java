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
package at.ac.tuwien.big.testsuite.impl.service;

import at.ac.tuwien.big.testsuite.api.service.ExportService;
import at.ac.tuwien.big.testsuite.api.exporter.ExporterFactory;
import at.ac.tuwien.big.testsuite.api.model.GroupValidationResult;
import at.ac.tuwien.big.testsuite.api.exporter.GroupValidationResultExporter;
import at.ac.tuwien.big.testsuite.api.model.ExportReport;
import at.ac.tuwien.big.testsuite.impl.exporter.ExporterFactoryImpl;
import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.ParameterizableTask;
import at.ac.tuwien.big.testsuite.impl.task.ExportTaskImpl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author Christian, Florian Zoubek
 */
@ApplicationScoped
public class ExportServiceImpl implements Serializable, ExportService {

    private final Collection<ExporterFactory> exporterFactories = new ArrayList<>();
    @Inject
    private Instance<GroupValidationResultExporter> exporters;

    @PostConstruct
    public void init() {
        for (GroupValidationResultExporter exporter : exporters) {
            exporterFactories.add(new ExporterFactoryImpl(exporter));
        }
    }

    @Override
    public ParameterizableTask<ExportReport, GroupValidationResult> export(final String exerciseId, HierarchicalTask<?, ?> parent) {
        return new ExportTaskImpl(exporterFactories, exerciseId, parent);
    }
}
