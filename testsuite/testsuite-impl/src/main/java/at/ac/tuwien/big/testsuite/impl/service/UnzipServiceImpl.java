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

import at.ac.tuwien.big.testsuite.api.service.UnzipService;
import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.LeafTask;
import at.ac.tuwien.big.testsuite.impl.task.UnzipTaskImpl;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author Christian
 */
@ApplicationScoped
public class UnzipServiceImpl implements UnzipService, Serializable {

    private static final Logger LOG = Logger.getLogger(UnzipServiceImpl.class.getName());
    private static final String[] IGNORED_EXTENSIONS = {".ogv", ".pdf"};
    private static final String[] IGNORED_DIRECTORIES = {File.separatorChar + ".", File.separatorChar + "__MACOSX"};

    @Override
    public LeafTask<File> unzip(final File zipFile, HierarchicalTask<?, ?> parent) {
        return new UnzipTaskImpl(zipFile, IGNORED_DIRECTORIES, IGNORED_EXTENSIONS, parent);
    }

    @Override
    public File uncompress(File zipFile) {
        try {
            return uncompress(new FileInputStream(zipFile));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Could not read zip file", ex);
        }
    }

    @Override
    public void uncompress(File zipFile, File outDir) {
        try {
            uncompress(new FileInputStream(zipFile), outDir);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("Could not read zip file", ex);
        }
    }

    @Override
    public File uncompress(InputStream is) {
        File temp;

        try {
            temp = createTempDirectory();
        } catch (IOException ex) {
            throw new RuntimeException("Could not create temp directory", ex);
        }

        uncompress(is, temp);
        return temp;
    }

    @Override
    public void uncompress(InputStream is, File outDir) {
        try (ZipInputStream zip = new ZipInputStream(is)) {
            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                String escapedEntryName = escapeFileName(entry.getName());
                File entryFile = new File(outDir, escapedEntryName);

                if (entry.isDirectory()) {
                    entryFile.mkdir();
                } else {
                    if (entryFile.getAbsolutePath().indexOf(File.separatorChar, outDir.getAbsolutePath().length()) > -1) {
                        // Only create dirs if file is not in the root dir
                        entryFile.getParentFile().mkdirs();
                    }

                    try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(entryFile))) {
                        copyInputStream(zip, outputStream);
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not uncompress", ex);
        }
    }

    private static File createTempDirectory() throws IOException {
        final File temp = File.createTempFile("temp", Long.toString(System.currentTimeMillis()));

        if (!temp.delete()) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!temp.mkdir()) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        return temp;
    }

    private static String escapeFileName(String name) {
        return name.replaceAll("[:*?|<>]", "_");
    }

    private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
    }
}
