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

import at.ac.tuwien.big.testsuite.api.service.ApplicationSettingsService;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Florian Zoubek
 */
@ApplicationScoped
public class ApplicationSettingsServiceImpl implements ApplicationSettingsService, Serializable {

    private static final String SETTINGS_FILE = "we_testsuite.properties";
    private static final String SETTINGS_DIR = System.getProperty("java.io.tmpdir");
    private File settingsFile;
    private Properties properties = null;

    public ApplicationSettingsServiceImpl() {
        settingsFile = new File(SETTINGS_DIR, SETTINGS_FILE);
    }

    @PostConstruct
    public void init() {
        settingsFile = new File(SETTINGS_DIR, SETTINGS_FILE);
        try {
            properties = new Properties();
            if (!settingsFile.exists()) {
                settingsFile.createNewFile();
            }
            properties.load(new BufferedInputStream(new FileInputStream(settingsFile)));
        } catch (IOException ex) {
            Logger.getLogger(ApplicationSettingsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            properties.store(new BufferedOutputStream(new FileOutputStream(settingsFile)), "");
        } catch (IOException ex) {
            Logger.getLogger(ApplicationSettingsServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String get(String key) {
        return properties.getProperty(key);
    }

    @Override
    public void set(String key, String value) {
        properties.setProperty(key, value);
    }
}
