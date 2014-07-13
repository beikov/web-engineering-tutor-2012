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

import at.ac.tuwien.big.testsuite.api.service.WebApplicationService;
import at.ac.tuwien.big.testsuite.api.service.UnzipService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoader;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.valves.StuckThreadDetectionValve;

/**
 *
 * @author Florian Zoubek
 */
@ApplicationScoped
public class WebApplicationServiceImpl implements Serializable, WebApplicationService {

    private static final Logger LOG = Logger.getLogger(WebApplicationServiceImpl.class.getName());
    private final ConcurrentMap<String, StandardContext> webAppContexts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Collection<Throwable>> applicationException = new ConcurrentHashMap<>();
    private String serverUrl;
    private String serverHost;
    private int serverPort;
    private Tomcat server;
    @Inject
    private UnzipService unzipService;

    @PostConstruct
    public void init() {
        try {
            serverHost = "localhost";
            serverPort = 8080;
            server = new Tomcat();
            server.setPort(8080);
            server.setBaseDir(System.getProperty("java.io.tmpdir"));
            server.enableNaming();
            server.getHost();
            final Pattern logPattern = Pattern.compile(".+id=([0-9]+).+");
            final ConcurrentHashMap<Long, Thread> activeThreads = new ConcurrentHashMap<>();
            final StuckThreadDetectionValve stuckThreadDetection = new StuckThreadDetectionValve(){

                @Override
                public void invoke(Request request, Response response) throws IOException, ServletException {
                    Long key = Long.valueOf(Thread.currentThread().getId());
                    
                    try{
                        activeThreads.put(key, Thread.currentThread());
                        super.invoke(request, response);
                    }finally{
                        activeThreads.remove(key);
                    }
                }
                
            };
            stuckThreadDetection.setThreshold(15);
            Logger.getLogger(StuckThreadDetectionValve.class.getName()).setFilter(new Filter() {

                @Override
                public boolean isLoggable(LogRecord record) {
                    Matcher matcher = null;
                    
                    if(record.getLevel() == Level.WARNING && (matcher = logPattern.matcher(record.getMessage())).matches()) {
                        Thread t = activeThreads.get(Long.valueOf(matcher.group(1)));
                        
                        if(t != null) {
                            t.stop();
                            LOG.log(Level.SEVERE, "Killed Thread because it was stuck for too long\n{0}", record.getMessage());
                            return false;
                        }
                    }
                    
                    return true;
                }
            });
            server.getEngine().getPipeline().addValve(stuckThreadDetection);
            server.start();
            serverUrl = "http://" + serverHost + ":" + serverPort + "/";
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            server = null;
        }
    }

    @PreDestroy
    public void destroy() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public String deployWar(File warFile) {
        return deployWebapp(unzipService.uncompress(warFile));
    }

    @Override
    public String deployWar(File warFile, String contextPath) {
        return deployWebapp(unzipService.uncompress(warFile), contextPath);
    }

    @Override
    public String deployWar(InputStream warFileStream) {
        return deployWebapp(unzipService.uncompress(warFileStream));
    }

    @Override
    public String deployWar(InputStream warFileStream, String contextPath) {
        return deployWebapp(unzipService.uncompress(warFileStream), contextPath);
    }

    @Override
    public String deployWebapp(File docRoot) {
        StandardContext context = new StandardContext();
        String contextPath = null;

        do {
            contextPath = UUID.randomUUID().toString();
        } while (webAppContexts.putIfAbsent(contextPath, context) != null);

        return deployWebapp(docRoot, contextPath, context, false);
    }

    @Override
    public String deployWebapp(File docRoot, String contextPath) {
        return deployWebapp(docRoot, contextPath, new StandardContext(), true);
    }

    @Override
    public String getServerBase(String contextPath) {
        return serverUrl + contextPath;
    }

    @Override
    public String getWelcomeFileUrl(String contextPath) {
        StandardContext context = webAppContexts.get(contextPath);
        String[] welcomeFiles = context.getWelcomeFiles();

        if (welcomeFiles != null && welcomeFiles.length != 0) {
            for (String welcomeFile : welcomeFiles) {
                if (new File(context.getRealPath(welcomeFile)).exists()) {
                    return serverUrl + contextPath + (welcomeFile.startsWith("/") ? welcomeFile : "/" + welcomeFile);
                } else {
                    String servletMapping = welcomeFile;

                    if (servletMapping.startsWith("/")) {
                        servletMapping = servletMapping.substring(1);
                    }
                    if (servletMapping.endsWith("*")) {
                        servletMapping = servletMapping.substring(0, servletMapping.length() - 1);
                    }
                    if (servletMapping.endsWith("/")) {
                        servletMapping = servletMapping.substring(0, servletMapping.length() - 1);
                    }

                    Map<String, ? extends ServletRegistration> servletRegistrations = context.getServletContext().getServletRegistrations();

                    if (servletRegistrations != null && !servletRegistrations.isEmpty()) {
                        for (Map.Entry<String, ? extends ServletRegistration> servletRegistration : servletRegistrations.entrySet()) {
                            ServletRegistration registration = servletRegistration.getValue();

                            if (registration != null && !registration.getClassName().startsWith("org.apache") && !registration.getMappings().isEmpty()) {
                                for (String mapping : registration.getMappings()) {
                                    if (mapping.startsWith("/")) {
                                        mapping = mapping.substring(1);
                                    }
                                    if (mapping.endsWith("*")) {
                                        mapping = mapping.substring(0, mapping.length() - 1);
                                        
                                        if(servletMapping.startsWith(mapping)) {
                                            mapping = servletMapping;
                                        }
                                    }
                                    if (mapping.endsWith("/")) {
                                        mapping = mapping.substring(0, mapping.length() - 1);
                                    }
                                    if (mapping.equals(servletMapping)) {
                                        return serverUrl + contextPath + "/" + mapping;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    public String getUserServletUrl(String contextPath) {
        StandardContext context = webAppContexts.get(contextPath);
        Map<String, ? extends ServletRegistration> servletRegistrations = context.getServletContext().getServletRegistrations();

        if (servletRegistrations != null && !servletRegistrations.isEmpty()) {
            for (Map.Entry<String, ? extends ServletRegistration> servletRegistration : servletRegistrations.entrySet()) {
                ServletRegistration registration = servletRegistration.getValue();

                if (registration != null && !registration.getClassName().startsWith("org.apache") && !registration.getMappings().isEmpty()) {
                    for (String mapping : registration.getMappings()) {
                        if (mapping.endsWith("*")) {
                            mapping = mapping.substring(0, mapping.length() - 1);
                        }
                        if (!mapping.contains("*")) {
                            return serverUrl + contextPath + (mapping.startsWith("/") ? mapping : "/" + mapping);
                        }
                    }
                }
            }
        }

        return serverUrl + contextPath;
    }

    private String deployWebapp(File docRoot, String contextPath, StandardContext context, boolean jndi) {
        try {
            File contextFile = new File(docRoot, "META-INF/context.xml");

            context.setName("/" + contextPath);
            context.setPath("/" + contextPath);
            context.setUnpackWAR(true);
            context.setUseNaming(jndi);
            context.setDocBase(docRoot.getAbsolutePath());

            if (jndi && contextFile.exists()) {
                context.setConfigFile(contextFile.toURI().toURL());
            }

            context.addLifecycleListener(new Tomcat.DefaultWebXmlListener());

            ContextConfig ctxCfg = new ContextConfig();
            context.addLifecycleListener(ctxCfg);

            ctxCfg.setDefaultWebXml(server.noDefaultWebXmlPath());
            final Collection<Throwable> exceptions = new ConcurrentLinkedQueue<>();
            final String baseLoggerName = "org.apache.catalina.core.ContainerBase.[Tomcat].[localhost].[" + contextPath + "]";
            final String baseLoggerName2 = "org.apache.catalina.core.ContainerBase.[Tomcat].[localhost].[/" + contextPath + "]";
            Filter deploymentExceptionFilter = new Filter() {
                @Override
                public boolean isLoggable(LogRecord record) {
                    if (record.getThrown() != null) {
                        exceptions.add(record.getThrown());
                        return false;
                    }

                    return true;
                }
            };
            
            Logger.getLogger(baseLoggerName).setFilter(deploymentExceptionFilter);
            Logger.getLogger(baseLoggerName2).setFilter(deploymentExceptionFilter);

            final Collection<Throwable> applicationExceptions = new ConcurrentLinkedQueue<>();
            final Filter applicationExceptionsFilter = new Filter() {
                @Override
                public boolean isLoggable(LogRecord record) {
                    if (record.getThrown() != null) {
                        applicationExceptions.add(record.getThrown());
                        return false;
                    }

                    return true;
                }
            };

            server.getHost().addChild(context);

            if (!exceptions.isEmpty()) {
                Throwable t = new RuntimeException("Could not start application '" + contextPath + "'");

                for (Throwable ex : exceptions) {
                    t.addSuppressed(ex);
                }

                throw t;
            }

            Map<String, ? extends ServletRegistration> servletRegistrations = context.getServletContext().getServletRegistrations();

            if (servletRegistrations != null && !servletRegistrations.isEmpty()) {
                for (Map.Entry<String, ? extends ServletRegistration> servletRegistration : servletRegistrations.entrySet()) {
                    ServletRegistration registration = servletRegistration.getValue();
                    Logger.getLogger(baseLoggerName + ".[" + registration.getName() + "]").setFilter(applicationExceptionsFilter);
                    Logger.getLogger(baseLoggerName2 + ".[" + registration.getName() + "]").setFilter(applicationExceptionsFilter);
                }
            }
            
            // Maybe do some security stuff
            // WebappClassLoader webappClassLoader = (WebappClassLoader)((WebappLoader) context.getLoader()).getClassLoader();

            webAppContexts.put(contextPath, context);
            applicationException.put(contextPath, applicationExceptions);
            return contextPath;
        } catch (Throwable ex) {
            server.getHost().removeChild(context);

            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public Collection<Throwable> undeploy(String contextPath) {
        StandardContext context = null;
        Collection<Throwable> applicationExceptions = null;
        if (contextPath != null) {
            context = webAppContexts.remove(contextPath);
            applicationExceptions = applicationException.remove(contextPath);
        }

        if (context != null) {
            server.getHost().removeChild(context);
        }

        return applicationExceptions;
    }
}
