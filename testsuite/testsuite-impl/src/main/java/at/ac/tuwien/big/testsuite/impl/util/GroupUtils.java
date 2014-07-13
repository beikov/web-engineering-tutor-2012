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
package at.ac.tuwien.big.testsuite.impl.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Christian
 */
public class GroupUtils {

    private static final Pattern DIR_NAME_PATTERN = Pattern.compile(".*(-|_|\\s)AG ([1-9][0-9]*)-.+");

    public static Map<String, File> getMavenStyleGroups(File baseDirectory) {
        return getGroups(baseDirectory, true);
    }

    public static Map<String, File> getGroups(File baseDirectory) {
        return getGroups(baseDirectory, false);
    }

    private static Map<String, File> getGroups(File baseDirectory, boolean mavenStyle) {
        final File[] files = baseDirectory.listFiles();
        final Map<String, File> groupDirectoryMap = new HashMap<>(files.length);

        for (int i = 0; i < files.length; i++) {
            final File groupBaseDir = files[i];
            final Matcher matcher = DIR_NAME_PATTERN.matcher(groupBaseDir.getName());

            if (groupBaseDir.isDirectory() && matcher.matches()) {
                groupDirectoryMap.put(matcher.group(2), searchForContentDir(groupBaseDir, groupBaseDir, mavenStyle));
            } else {
                throw new IllegalArgumentException("Group directory name '" + groupBaseDir.getName() + "' is invalid.\nIt must match UE1-AG_XX_YY where XX is the group number and YY may be any other string!");
            }
        }

        return groupDirectoryMap;
    }

    private static File searchForContentDir(File groupBaseDir, File baseDir, boolean mavenStyle) {
        if (isContentDir(baseDir, mavenStyle)) {
            return baseDir;
        } else {
            for (File f : baseDir.listFiles()) {
                if (f.isDirectory() && !f.getName().toUpperCase().contains("MACOSX")) {
                    File temp = searchForContentDir(groupBaseDir, f, mavenStyle);

                    if (temp != null) {
                        return temp;
                    }
                }
            }
        }

        return groupBaseDir == baseDir ? baseDir : null;
    }

    private static boolean isContentDir(File baseDir, boolean mavenStyle) {
        if (mavenStyle) {
            for (File f : baseDir.listFiles()) {
                if (f.getName().equals("pom.xml")) {
                    return true;
                }
            }
        } else {
            for (File f : baseDir.listFiles()) {
                if (f.getName().toLowerCase().endsWith(".html")) {
                    return true;
                }
            }
        }

        return false;
    }
}
