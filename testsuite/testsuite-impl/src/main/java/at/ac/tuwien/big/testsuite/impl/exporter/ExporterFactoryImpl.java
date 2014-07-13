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
package at.ac.tuwien.big.testsuite.impl.exporter;

import at.ac.tuwien.big.testsuite.api.exporter.ExportCompleteListener;
import at.ac.tuwien.big.testsuite.api.exporter.ExporterFactory;
import at.ac.tuwien.big.testsuite.api.exporter.GroupValidationResultExporter;
import at.ac.tuwien.big.testsuite.api.model.GroupValidationResult;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

/**
 *
 * @author Christian
 */
public class ExporterFactoryImpl implements ExporterFactory {

    private final GroupValidationResultExporter exporter;

    public ExporterFactoryImpl(GroupValidationResultExporter exporter) {
        this.exporter = exporter;
    }

    @Override
    public Collection<Callable<Void>> create(final GroupValidationResult groupValidationResult, final File baseDirectory, final File targetDirectory, final String exerciseId, final ExportCompleteListener listener) {
        Collection<Callable<Void>> exporters = new ArrayList<>();

        exporters.add(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    exporter.export(groupValidationResult, baseDirectory, targetDirectory, exerciseId);
                    listener.onComplete(Collections.<Exception>emptyList());
                } catch (Exception ex) {
                    listener.onComplete(Arrays.asList(ex));
                }

                return null;
            }
        });

        return Collections.unmodifiableCollection(exporters);
    }
}
