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

import java.io.IOException;

/**
 * Base class for writers used to write a report
 * @author Florian Zoubek
 */
public abstract class ReportWriter {
    
    private static final String DEFAULT_INDENT_CHARACTERS = "\t";
    private static final int DEFAULT_SEPARATOR_CHARS_NUM = 100;
    
    protected String indentChars;
    protected int numSeparatorChars;

    public ReportWriter() {
        indentChars = DEFAULT_INDENT_CHARACTERS;
        numSeparatorChars = DEFAULT_SEPARATOR_CHARS_NUM;
    }
    
    /**
     *  writes the given string with the given intendation level
     * @param level the intendation level to use
     * @param str the string to write
     * @throws IOException if an error occurs
     */
    public abstract void writeLine(int level, String str) throws IOException;
    
    /**
     * writes a separator line constiting of the <code>sepChar</code>s
     * @param sepChar the characters used to create the separator
     * @throws IOException if an error occurs
     */
    public abstract void writeSeparator(char sepChar) throws IOException;
    
    /**
     * writes a new line
     * @throws IOException if an error occurs
     */
    public abstract void newLine() throws IOException;
    
    /* Getter and setter */
    
    public String getIndentChars() {
        return indentChars;
    }

    public void setIndentChars(String indentChars) {
        this.indentChars = indentChars;
    }

    public int getNumSeparatorChars() {
        return numSeparatorChars;
    }

    public void setNumSeparatorChars(int numSeparatorChars) {
        this.numSeparatorChars = numSeparatorChars;
    }
}
