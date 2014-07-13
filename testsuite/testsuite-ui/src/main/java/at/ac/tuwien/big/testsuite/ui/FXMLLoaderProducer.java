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

import at.ac.tuwien.big.testsuite.ui.app.controller.CdiController;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;

import javax.enterprise.inject.*;
import javax.inject.Inject;

/**
 *
 * @author Christian
 */
public class FXMLLoaderProducer {

    @Inject
    private Instance<Object> instance;
    private final ConcurrentMap<Class<?>, Object> cache = new ConcurrentHashMap<>();

    @Produces
    public FXMLLoader createLoader() {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(new Callback<Class<?>, Object>() {
            @Override
            public Object call(Class<?> param) {
                Object o = cache.get(param);

                // This is a hack to get the real instance instead of a proxy
                // since javafx can't handle the proxy
                if (o == null) {
                    o = instance.select(param).get();

                    if (o instanceof CdiController) {
                        o = ((CdiController) o).getInstance();
                    }

                    Object temp = cache.putIfAbsent(param, o);

                    if (temp != null) {
                        o = temp;
                    }
                }

                return o;
            }
        });
        return loader;
    }
}
