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

import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 *
 * @author Christian, Florian Zoubek
 */
public class ApplicationStarter {

    @Inject
    private FXMLLoader fxmlLoader;

    public void launchJavaFXApplication(@Observes @StartupScene Stage stage) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("fxml/ui-main.fxml")) {
            Parent rootNode = (Parent) fxmlLoader.load(is);

            Scene scene = new Scene(rootNode, 800, 200);
            scene.getStylesheets().add("/styles/ui-styles.css");

            stage.setTitle("Web Engineering Testsuite");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot load FXML file", ex);
        }
    }
}
