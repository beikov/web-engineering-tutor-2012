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

import at.ac.tuwien.big.testsuite.api.model.MavenReport;
import at.ac.tuwien.big.testsuite.api.service.MavenTestService;
import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.ParameterizableTask;
import at.ac.tuwien.big.testsuite.impl.task.MavenTaskImpl;
import java.io.File;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Florian Zoubek
 */
@ApplicationScoped
public class MavenTestServiceImpl implements MavenTestService {

    private static final Logger LOG = Logger.getLogger(MavenTestServiceImpl.class.getName());

    @Override
    public ParameterizableTask<MavenReport, File> buildAll(File targetDirectory, HierarchicalTask<?, ?> parent) {
        return new MavenTaskImpl(targetDirectory, parent);
    }
}
