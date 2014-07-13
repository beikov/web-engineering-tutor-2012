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
package at.ac.tuwien.big.testsuite.impl;

import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import javax.enterprise.context.ApplicationScoped;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.junit.Before;

/**
 *
 * @author Christian
 */
public abstract class BaseTest implements Serializable {

    static {
        CdiContainerLoader.getCdiContainer().boot();
        CdiContainerLoader.getCdiContainer().getContextControl().startContext(ApplicationScoped.class);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                CdiContainerLoader.getCdiContainer().shutdown();
            }
        });
    }

    @Before
    public void init() {
        BeanProvider.injectFields(this);
    }

    protected File getFile(String fileName) {
        URL url = getClass().getClassLoader().getResource(fileName);

        try {
            return new File(url.toURI());
        } catch (URISyntaxException ex) {
            return null;
        }
    }
}
