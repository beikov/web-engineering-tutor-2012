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
package at.ac.tuwien.big.testsuite.impl.exporter;

import at.ac.tuwien.big.testsuite.api.exporter.GroupValidationResultExporter;
import at.ac.tuwien.big.testsuite.api.model.GroupValidationResult;
import at.ac.tuwien.big.testsuite.impl.util.DomUtils;
import at.ac.tuwien.big.testsuite.impl.util.TestsuiteConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
public class FileExporterImpl implements GroupValidationResultExporter {

    private static final Logger LOG = Logger.getLogger(FileExporterImpl.class.getName());
    private static final List<String> IGNORED_DIRECTORIES = Arrays.asList("bin", "target");
    private static final List<String> IGNORED_FILE_SUFFIXES = Arrays.asList(".war");
    private static final Map<String, Collection<Pair>> exportFiles = new HashMap<>();

    @PostConstruct
    public void init() {
        // files for lab1
        Collection<Pair> filesToExportLab1 = new ArrayList<>();
        filesToExportLab1.add(new Pair("table.html", "table.html"));
        filesToExportLab1.add(new Pair("register.html", "register.html"));
        exportFiles.put(TestsuiteConstants.EX_ID_LAB1, filesToExportLab1);

        // files for lab2
        Collection<Pair> filesToExportLab2 = new ArrayList<>();
        filesToExportLab2.add(new Pair("target/exportedhtml/startup.html", "startup.html"));
        filesToExportLab2.add(new Pair("target/exportedhtml/first_step.html", "first_step.html"));
        filesToExportLab2.add(new Pair("target/exportedhtml/finish.html", "finish.html"));
        exportFiles.put(TestsuiteConstants.EX_ID_LAB2, filesToExportLab2);

        // files for lab3
        Collection<Pair> filesToExportLab3 = new ArrayList<>();
        filesToExportLab3.add(new Pair("target/exportedhtml/startup.html", "startup.html"));
        filesToExportLab3.add(new Pair("target/exportedhtml/first_step.html", "first_step.html"));
        filesToExportLab3.add(new Pair("target/exportedhtml/finish.html", "finish.html"));
        filesToExportLab3.add(new Pair("target/exportedhtml/register-new.html", "register-new.html"));
        filesToExportLab3.add(new Pair("target/exportedhtml/register-success.html", "register-success.html"));
        filesToExportLab3.add(new Pair("target/exportedhtml/register-fail-all.html", "register-fail-all.html"));
        filesToExportLab3.add(new Pair("target/exportedhtml/register-terms-and-conditions.html", "terms-and-conditions.html"));
        filesToExportLab3.add(new Pair("target/exportedhtml/login-new.html", "login-new.html"));
        filesToExportLab3.add(new Pair("target/exportedhtml/login-logged-out.html", "login-logged-out.html"));
        filesToExportLab3.add(new Pair("target/exportedhtml/login-failed.html", "login-failed.html"));
        exportFiles.put(TestsuiteConstants.EX_ID_LAB3, filesToExportLab3);
    }

    private static class Pair {

        private final String sourceFile;
        private final String destinationFile;

        public Pair(String sourceFile, String destinationFile) {
            this.sourceFile = sourceFile;
            this.destinationFile = destinationFile;
        }
    }

    @Override
    public void export(GroupValidationResult groupValidationResult, File baseDirectory, File targetDirectory, String exerciseId) {
        copy(baseDirectory, targetDirectory);

        Collection<Pair> filesToExport = exportFiles.get(exerciseId);

        if (filesToExport != null) {
            RuntimeException exception = null;

            for (Pair pair : filesToExport) {
                try {
                    File sourceFile = new File(baseDirectory, pair.sourceFile);
                    
                    if(sourceFile.exists()){
                        copyFile(new File(baseDirectory, pair.sourceFile), new File(targetDirectory, "original-" + pair.destinationFile));
                        exportRaw(new File(baseDirectory, pair.sourceFile), new File(targetDirectory, "raw-" + pair.destinationFile));
                    }
                } catch (RuntimeException ex) {
                    if (exception == null) {
                        exception = ex;
                    } else {
                        exception.addSuppressed(ex);
                    }
                }
            }

            if (exception != null) {
                throw exception;
            }
        }
    }

    private void exportRaw(File source, File target) {
        Document doc = DomUtils.createDocument(source);
        Element elem = doc.getDocumentElement();
        remove(DomUtils.listByXpath(elem, "//script"));
        remove(DomUtils.listByXpath(elem, "//style"));
        remove(DomUtils.listByXpath(elem, "//link"));
        removeAttrs(DomUtils.attributesByXpath(elem, "//*[@style]/@style"));
        removeAttrs(DomUtils.attributesByXpath(elem, "//img[@src]/@src"));
        DomUtils.write(doc, target);
    }

    private void remove(List<Element> elements) {
        if (elements != null && !elements.isEmpty()) {
            for (Element element : elements) {
                element.getParentNode().removeChild(element);
            }
        }
    }

    private void removeAttrs(List<Attr> elements) {
        if (elements != null && !elements.isEmpty()) {
            for (Attr element : elements) {
                element.getOwnerElement().removeAttributeNode(element);
            }
        }
    }

    private void copy(File baseDirectory, File targetDirectory) {
        for (File f : baseDirectory.listFiles()) {
            File newFile = new File(targetDirectory, f.getName());

            if (f.isDirectory()) {
                if (!isIgnored(IGNORED_DIRECTORIES, newFile)) {
                    newFile.mkdir();
                    copy(f, newFile);
                }
            } else if(!isIgnoredSuffix(IGNORED_FILE_SUFFIXES, f)){
                copyFile(f, newFile);
            }
        }
    }

    private boolean isIgnoredSuffix(List<String> ignoredSuffixes, File f) {
        for (String ignoredDirectory : ignoredSuffixes) {
            if (f.getName().endsWith(ignoredDirectory)) {
                return true;
            }
        }

        return false;
    }

    private boolean isIgnored(List<String> ignoredDirectories, File f) {
        for (String ignoredDirectory : ignoredDirectories) {
            if (f.getName().equals(ignoredDirectory)) {
                return true;
            }
        }

        return false;
    }

    private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }

    private void copyFile(File f, File newFile) {
        try (FileInputStream fis = new FileInputStream(f); FileOutputStream fos = new FileOutputStream(newFile)) {
            copyInputStream(fis, fos);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Could not copy streams", ex);
        }
    }
}
