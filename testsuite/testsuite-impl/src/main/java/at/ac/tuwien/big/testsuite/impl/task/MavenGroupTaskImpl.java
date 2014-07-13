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

import at.ac.tuwien.big.testsuite.api.model.MavenGroupReport;
import at.ac.tuwien.big.testsuite.api.task.HierarchicalTask;
import at.ac.tuwien.big.testsuite.api.task.LeafTask;
import at.ac.tuwien.big.testsuite.impl.model.DefaultMavenGroupReport;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;

/**
 *
 * @author Christian
 */
public class MavenGroupTaskImpl extends LeafTask<MavenGroupReport> {

    private static final int CONCURRENT_MAVEN_TASK_RUNS = 2;
    private static final BlockingQueue<Object> tokens = new LinkedBlockingQueue<>();
    private final String groupName;
    private final File groupBaseDir;
    private final File groupTargetBaseDir;

    static{
        for (int i = 0; i < CONCURRENT_MAVEN_TASK_RUNS; i++) {
            tokens.add(new Object());
        }
    }

    public MavenGroupTaskImpl(String groupName, File groupBaseDir, File groupTargetBaseDir) {
        this.groupName = groupName;
        this.groupBaseDir = groupBaseDir;
        this.groupTargetBaseDir = groupTargetBaseDir;
    }

    public MavenGroupTaskImpl(String groupName, File groupBaseDir, File groupTargetBaseDir, HierarchicalTask<?, ?> parent) {
        super(parent);
        this.groupName = groupName;
        this.groupBaseDir = groupBaseDir;
        this.groupTargetBaseDir = groupTargetBaseDir;
    }

    @Override
    protected MavenGroupReport call() throws Exception {

        Object token = tokens.take();

        try {
            final StringBuilder sb = new StringBuilder();
            InvocationRequest request = new DefaultInvocationRequest();
            request.setPomFile(new File(groupBaseDir, "pom.xml"));
            request.setGoals(Arrays.asList("clean package"));
            InvocationOutputHandler handler = new InvocationOutputHandler() {
                @Override
                public void consumeLine(String string) {
                    sb.append(string);
                }
            };
            request.setOutputHandler(handler);
            request.setErrorHandler(handler);

            File warFile = null;
            Invoker invoker = new DefaultInvoker();

            try {
                InvocationResult result = invoker.execute(request);

                if (result.getExitCode() != 0 && result.getExecutionException() != null) {
                    throw result.getExecutionException();
                }

                for (File targetEntry : new File(groupBaseDir, "target").listFiles()) {
                    if (targetEntry.getName().endsWith(".war")) {
                        warFile = targetEntry;
                        break;
                    }
                }
            } catch (Exception ex) {
                sb.append('\n');
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ex.printStackTrace(new PrintStream(baos));
                sb.append(baos.toString());
            }

            sb.append("\nUnkown maven build error occurred");
            return new DefaultMavenGroupReport(groupName, sb.toString(), groupBaseDir, warFile);
        } finally {
            tokens.put(token);
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public File getGroupBaseDir() {
        return groupBaseDir;
    }

    public File getGroupTargetBaseDir() {
        return groupTargetBaseDir;
    }
}
