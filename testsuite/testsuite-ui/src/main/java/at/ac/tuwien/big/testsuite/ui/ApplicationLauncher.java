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
package at.ac.tuwien.big.testsuite.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.enterprise.util.AnnotationLiteral;
import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.core.api.provider.BeanProvider;

public class ApplicationLauncher extends Application {

    private static final Logger LOG = Logger.getLogger(ApplicationLauncher.class.getName());

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        CdiContainer container = CdiContainerLoader.getCdiContainer();

        try {
            // initialize container
            container.boot();
            // make the application parameters injectable
            BeanProvider.getContextualReference(ApplicationParametersProvider.class).setParameters(getParameters());
            // JavaFX thread is ready, fire a startup event
            container.getBeanManager().fireEvent(stage, new AnnotationLiteral<StartupScene>() {
            });

        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, "Could not start application", ex);
            throw ex;
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        CdiContainerLoader.getCdiContainer().shutdown();
    }
}
