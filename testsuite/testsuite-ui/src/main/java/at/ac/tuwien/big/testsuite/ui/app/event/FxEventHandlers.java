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
package at.ac.tuwien.big.testsuite.ui.app.event;

import at.ac.tuwien.big.testsuite.api.task.EventHandler;
import javafx.application.Platform;

/**
 *
 * @author Christian
 */
public class FxEventHandlers {

    public static <T> EventHandler<T> runLater(final EventHandler<T> handler) {
        return new EventHandler<T>() {
            @Override
            public void handle(final T event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        handler.handle(event);
                    }
                });
            }
        };
    }
}
