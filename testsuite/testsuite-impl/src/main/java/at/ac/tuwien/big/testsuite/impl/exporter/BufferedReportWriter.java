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

import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author Florian Zoubek 
 */
public class BufferedReportWriter extends ReportWriter{
    
    private BufferedWriter writer;

    /**
     * Creates a new <code>BufferedReportWriter</code> that writes with the given BufferedWriter
     * @param writer the writer used to write
     * @throws IllegalArgumentException if writer is null
     */
    public BufferedReportWriter(BufferedWriter writer) throws IllegalArgumentException{
        if(writer == null){
            throw new IllegalArgumentException();
        }
        this.writer = writer;
    }

    @Override
    public void writeLine(int level, String str) throws IOException {
        String[] lines = str.split("\n");

        for (String line : lines) {
            for (int i = 0; i < level; i++) {
                writer.write(indentChars);
            }

            writer.write(line);
            writer.newLine();
        }
    }

    @Override
    public void writeSeparator(char sepChar) throws IOException {
        for (int i = 0; i < numSeparatorChars; i++) {
            writer.write(sepChar);
        }

        writer.newLine();
    }

    @Override
    public void newLine() throws IOException{
        writer.newLine();
    }
    
}
