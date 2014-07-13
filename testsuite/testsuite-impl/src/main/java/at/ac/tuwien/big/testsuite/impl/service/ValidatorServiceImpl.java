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

import at.ac.tuwien.big.testsuite.impl.validator.ValidatorFactoryImpl;
import at.ac.tuwien.big.testsuite.api.model.GroupValidationResult;
import at.ac.tuwien.big.testsuite.api.model.MavenGroupReport;
import at.ac.tuwien.big.testsuite.api.model.SeleniumReport;
import at.ac.tuwien.big.testsuite.api.model.ValidationReport;
import at.ac.tuwien.big.testsuite.api.validator.Validator;
import at.ac.tuwien.big.testsuite.api.validator.ValidatorFactory;
import at.ac.tuwien.big.testsuite.api.service.ValidatorService;
import at.ac.tuwien.big.testsuite.api.service.WebApplicationService;
import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.LeafTask;
import at.ac.tuwien.big.testsuite.api.task.ParameterizableTask;
import at.ac.tuwien.big.testsuite.impl.task.GroupValidationTaskImpl;
import at.ac.tuwien.big.testsuite.impl.task.SeleniumTaskImpl;
import at.ac.tuwien.big.testsuite.impl.task.ValidationTaskImpl;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.apache.http.client.HttpClient;

/**
 *
 * @author Christian, Florian Zoubek
 */
@ApplicationScoped
public class ValidatorServiceImpl implements ValidatorService, Serializable {

    private final Collection<ValidatorFactory> validatorFactories = new ArrayList<>();
    @Inject
    private Instance<Validator> validators;
    @Inject
    private WebApplicationService webApplicationService;
    @Inject
    private HttpClient httpClient;

    @PostConstruct
    public void init() {
        for (Validator validator : validators) {
            validatorFactories.add(new ValidatorFactoryImpl(validator));
        }
    }

    @Override
    public LeafTask<GroupValidationResult> validateSingle(String groupName, File groupBaseDir, File groupTargetBaseDir, String exerciseId, HierarchicalTask<?, ?> parent) {
        return new GroupValidationTaskImpl(groupName, groupBaseDir, groupTargetBaseDir, exerciseId, validatorFactories, parent);
    }

    @Override
    public ParameterizableTask<ValidationReport, File> validateAll(final File targetDir, final String exerciseId, HierarchicalTask<?, ?> parent) {
        return new ValidationTaskImpl(targetDir, exerciseId, validatorFactories, parent);
    }

    @Override
    public ParameterizableTask<SeleniumReport, MavenGroupReport> testSingle(String exerciseId, HierarchicalTask<?, ?> parent) {
        return new SeleniumTaskImpl(webApplicationService, httpClient, exerciseId, parent);
    }
}
