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
package at.ac.tuwien.big.testsuite.ui.app;

import at.ac.tuwien.big.testsuite.ui.StartupScene;
import java.io.*;
import java.util.logging.LogManager;

import javafx.stage.Stage;

import javax.enterprise.event.Observes;

/**
 *
 * @author Christian, Florian Zoubek
 */
public class LoggerStarter {

    private static final String LOGGING_PROPERTIES = "conf/logging.properties";

    public void launchJavaFXApplication(@Observes @StartupScene Stage stage) {
        try {
            if (new File(LOGGING_PROPERTIES).exists()) {
                System.setProperty("java.util.logging.config.file", LOGGING_PROPERTIES);
                LogManager.getLogManager().readConfiguration();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Could not initialize logger", ex);
        }
    }
}
