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
package at.ac.tuwien.big.testsuite.impl.task;

import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.LeafTask;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

/**
 *
 * @author Christian
 */
public class UnzipTaskImpl extends LeafTask<File> {

    private final File zipFile;
    private final String[] ignoredDirectories;
    private final String[] ignoredExtensions;

    public UnzipTaskImpl(File zipFile, String[] ignoredDirectories, String[] ignoredExtensions) {
        this.zipFile = zipFile;
        this.ignoredDirectories = ignoredDirectories;
        this.ignoredExtensions = ignoredExtensions;
    }

    public UnzipTaskImpl(File zipFile, String[] ignoredDirectories, String[] ignoredExtensions, HierarchicalTask<?, ?> parent) {
        super(parent);
        this.zipFile = zipFile;
        this.ignoredDirectories = ignoredDirectories;
        this.ignoredExtensions = ignoredExtensions;
    }

    @Override
    protected File call() throws Exception {
        final File baseDirectory = createTempDirectory();
        Collection<Exception> exceptions = new CopyOnWriteArrayList<>();
        unzip(zipFile, baseDirectory, true, exceptions);
        return baseDirectory;
    }

    private void unzip(File zipFile, File baseDirectory, boolean extractContainedZipFiles, Collection<Exception> exceptions) {
        try (ZipFile zip = new ZipFile(zipFile)) {
            addTotalWork(zip.size());
            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String escapedEntryName = escapeFileName(entry.getName());
                File entryFile = new File(baseDirectory, escapedEntryName);

                if (entry.isDirectory()) {
                    if (!contains(entryFile.getAbsolutePath(), ignoredDirectories)) {
                        entryFile.mkdir();
                    }
                } else if (!endsWith(entryFile.getName(), ignoredExtensions) && !contains(entryFile.getAbsolutePath(), ignoredDirectories)) {
                    if (entryFile.getAbsolutePath().indexOf(File.separatorChar, baseDirectory.getAbsolutePath().length()) > -1) {
                        // Only create dirs if file is not in the root dir
                        entryFile.getParentFile().mkdirs();
                    }

                    try (InputStream is = zip.getInputStream(entry); OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(entryFile))) {
                        copyInputStream(is, outputStream);
                    }

                    if (extractContainedZipFiles && (escapedEntryName.toLowerCase().endsWith(".zip") || escapedEntryName.toLowerCase().endsWith(".war"))) {
                        String zipFileName = escapedEntryName;
                        File zipFileBaseDir = new File(baseDirectory, zipFileName.substring(0, zipFileName.length() - 4));
                        zipFileBaseDir.mkdir();

                        unzip(entryFile, zipFileBaseDir, false, exceptions);
                        entryFile.delete();
                    } else if (extractContainedZipFiles && (escapedEntryName.toLowerCase().endsWith(".tar") || escapedEntryName.toLowerCase().endsWith(".gz"))) {
                        String zipFileName = escapedEntryName;
                        int offset = escapedEntryName.toLowerCase().endsWith(".tar") ? 4 : 0;
                        offset = escapedEntryName.toLowerCase().endsWith(".tar.gz") ? 7 : offset;
                        File zipFileBaseDir = new File(baseDirectory, zipFileName.substring(0, zipFileName.length() - offset));
                        zipFileBaseDir.mkdir();

                        untar(entryFile, zipFileBaseDir, exceptions, escapedEntryName.toLowerCase().endsWith(".tar.gz"));
                        entryFile.delete();
                    } else if (extractContainedZipFiles && escapedEntryName.toLowerCase().endsWith(".rar")) {
                        String zipFileName = escapedEntryName;
                        File zipFileBaseDir = new File(baseDirectory, zipFileName.substring(0, zipFileName.length() - 4));
                        zipFileBaseDir.mkdir();

                        unrar(entryFile, zipFileBaseDir, exceptions);
                        entryFile.delete();
                    }
                }

                addDoneWork(1);
            }
        } catch (Exception ex) {
            exceptions.add(ex);
        }
    }

    private void untar(File tarFile, File baseDirectory, Collection<Exception> exceptions, boolean gzipped) {
        try (FileInputStream fis = new FileInputStream(tarFile); TarArchiveInputStream tis = new TarArchiveInputStream(gzipped ? new GzipCompressorInputStream(fis) : fis)) {
            TarArchiveEntry entry;

            while ((entry = tis.getNextTarEntry()) != null) {
                String escapedEntryName = escapeFileName(entry.getName());
                File entryFile = new File(baseDirectory, escapedEntryName);

                if (entry.isDirectory()) {
                    if (!contains(entryFile.getAbsolutePath(), ignoredDirectories)) {
                        entryFile.mkdir();
                    }
                } else if (!endsWith(entryFile.getName(), ignoredExtensions) && !contains(entryFile.getAbsolutePath(), ignoredDirectories)) {
                    if (entryFile.getAbsolutePath().indexOf(File.separatorChar, baseDirectory.getAbsolutePath().length()) > -1) {
                        // Only create dirs if file is not in the root dir
                        entryFile.getParentFile().mkdirs();
                    }

                    try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(entryFile))) {
                        copyInputStream(tis, outputStream);
                    }
                }

                addTotalWork(1);
                addDoneWork(1);
            }
        } catch (Exception ex) {
            exceptions.add(ex);
        }
    }

    private void unrar(File rarFile, File baseDirectory, Collection<Exception> exceptions) {
        try (Archive archive = new Archive(rarFile)) {
            FileHeader entry;

            while ((entry = archive.nextFileHeader()) != null) {
                String escapedEntryName = escapeFileName(entry.getFileNameString().trim());
                File entryFile = new File(baseDirectory, escapedEntryName);

                if (entry.isDirectory()) {
                    if (!contains(entryFile.getAbsolutePath(), ignoredDirectories)) {
                        entryFile.mkdir();
                    }
                } else if (!endsWith(entryFile.getName(), ignoredExtensions) && !contains(entryFile.getAbsolutePath(), ignoredDirectories)) {
                    if (entryFile.getAbsolutePath().indexOf(File.separatorChar, baseDirectory.getAbsolutePath().length()) > -1) {
                        // Only create dirs if file is not in the root dir
                        entryFile.getParentFile().mkdirs();
                    }

                    try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(entryFile))) {
                        archive.extractFile(entry, outputStream);
                    }
                }

                addTotalWork(1);
                addDoneWork(1);
            }
        } catch (Exception ex) {
            exceptions.add(ex);
        }
    }

    private static String escapeFileName(String name) {
        return name.replaceAll("[:*?|<>]", "_");
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

    private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
    }

    private static boolean endsWith(String string, String... suffixes) {
        for (int i = 0; i < suffixes.length; i++) {
            if (string.endsWith(suffixes[i])) {
                return true;
            }
        }

        return false;
    }

    private static boolean contains(String string, String... substrings) {
        for (int i = 0; i < substrings.length; i++) {
            if (string.contains(substrings[i])) {
                return true;
            }
        }

        return false;
    }

    public File getZipFile() {
        return zipFile;
    }

    public String[] getIgnoredDirectories() {
        return ignoredDirectories;
    }

    public String[] getIgnoredExtensions() {
        return ignoredExtensions;
    }
}
