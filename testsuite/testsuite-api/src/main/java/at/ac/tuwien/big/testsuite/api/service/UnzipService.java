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
package at.ac.tuwien.big.testsuite.api.service;

import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.LeafTask;
import java.io.File;
import java.io.InputStream;

/**
 *
 * @author Christian
 */
public interface UnzipService {

    public LeafTask<File> unzip(final File zipFile, HierarchicalTask<?, ?> parent);

    public void uncompress(InputStream is, File outDir);

    public void uncompress(File zipFile, File outDir);

    public File uncompress(InputStream is);

    public File uncompress(File zipFile);
}
