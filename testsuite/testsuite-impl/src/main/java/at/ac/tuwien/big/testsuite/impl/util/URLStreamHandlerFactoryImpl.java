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

import at.ac.tuwien.big.testsuite.api.exporter.ExporterFactory;
import at.ac.tuwien.big.testsuite.api.task.EventHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 * @author Christian
 */
public class URLStreamHandlerFactoryImpl implements URLStreamHandlerFactory {

    private static final Logger LOG = Logger.getLogger(URLStreamHandlerFactoryImpl.class.getName());
    private static final URLStreamHandlerFactoryImpl INSTANCE = getInstance();
    private final ConcurrentMap<String, MonitoredURLStreamHandler> handlers = new ConcurrentHashMap<>();
    // special case the gopher protocol, disabled by default
    private static final String GOPHER = "gopher";
    private static final String ENABLE_GOPHER_PROP = "jdk.net.registerGopherProtocol";
    private static final boolean enableGopher = AccessController.doPrivileged(
            new PrivilegedAction<Boolean>() {
        @Override
        public Boolean run() {
            String prop = System.getProperty(ENABLE_GOPHER_PROP);
            return prop == null ? false
                    : (prop.equalsIgnoreCase("false") ? false : true);
        }
    });
    // package name of the JDK implementation protocol handlers
    private static final String JDK_PACKAGE_PREFIX = "sun.net.www.protocol";
    private static final String protocolPathProp = "java.protocol.handler.pkgs";
    private final ConcurrentMap<String, String> hostMapping = new ConcurrentHashMap<>();
    private final List<EventHandler<URL>> eventHandlers = new CopyOnWriteArrayList<>();
    private final URLMapper urlMapper = new URLMapper() {
        @Override
        public URL map(URL url) {
            String host = hostMapping.get(url.getHost());

            if (host != null) {
                try {
                    return new URL(url.toString().replace(url.getHost(), host));
                } catch (MalformedURLException ex) {
                    LOG.log(Level.SEVERE, "URL host mapping from '" + url.getHost() + "' to '" + host + "' failed", ex);
                }
            }
            
            for(EventHandler<URL> eventHandler : eventHandlers) {
                try {
                    eventHandler.handle(url);
                } catch(Exception ex){
                    LOG.log(Level.SEVERE, "URL event handler threw an exception on url: " + url, ex);
                }
            }

            return url;
        }
    };

    private URLStreamHandlerFactoryImpl() {
    }

    public static URLStreamHandlerFactoryImpl getInstance() {
        if (INSTANCE == null) {
            try {
                // TODO: Once we have time we can think of security a bit more
                
//                File policy = File.createTempFile("security", ".policy");
//                URL policyFile = URLStreamHandlerFactoryImpl.class.getResource("/security.policy");
//                copyToFile(policyFile.openStream(), policy);
//                // This is a hack to add a variable to resolve in the policy file
//                if("jar".equals(policyFile.getProtocol())) {
//                    policyFile = new URL(policyFile.getPath());
//                }
//                
//                String policyFilePath = policyFile.getPath();
//                int codeBaseEndIndex = policyFilePath.lastIndexOf('!');
//                File codeBaseFile = null;
//                String codeBasePath = null;
//                
//                if(codeBaseEndIndex > 0 && (codeBaseFile = new File(policyFilePath.substring(0, codeBaseEndIndex))).exists()) {
//                    codeBasePath = codeBaseFile.getAbsolutePath();
//                } else if((codeBaseFile = new File(policyFilePath)).exists()) {
//                    codeBasePath = codeBaseFile.getParentFile().getAbsolutePath();
//                    
//                    if(!codeBasePath.endsWith("/")) {
//                        codeBasePath = codeBasePath + "/-";
//                    } else {
//                        codeBasePath = codeBasePath + "-";
//                    }
//                } else {
//                    throw new RuntimeException("Could not determine code base path for the application!");
//                }
//                
//                
//                System.setProperty("testsuite.base", codeBasePath);
//                System.setProperty("java.security.policy", policy.getAbsolutePath());
//                // Initialize policy
//                Policy p = Policy.getPolicy();
//                System.setSecurityManager(new SecurityManager());
                URLStreamHandlerFactoryImpl instance = new URLStreamHandlerFactoryImpl();
                URL.setURLStreamHandlerFactory(instance);
                return instance;
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        return INSTANCE;
    }

    private static void copyToFile(InputStream in, File f) throws Exception {
        try (OutputStream out = new FileOutputStream(f)) {
            copyInputStream(in, out);
        }
    }

    private static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int len;

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
    }

    public void addURLMapping(String host, String mapToHost) {
        hostMapping.put(host, mapToHost);
    }
    
    public void addUnhandeledURLEvenetHandler(EventHandler<URL> handler) {
        eventHandlers.add(handler);
    }

    /**
     * Mostly copied from {@link java.net.URL} with the addition of wrapping the
     * {@link URLStreamHandler}s.
     *
     * @param protocol
     * @return
     */
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        MonitoredURLStreamHandler handler = handlers.get(protocol);

        // Try java protocol handler
        if (handler == null) {
            String packagePrefixList = null;

            packagePrefixList = AccessController.doPrivileged(new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getProperty(protocolPathProp, "");
                }
            });
            if (packagePrefixList != "") {
                packagePrefixList += "|";
            }

            // REMIND: decide whether to allow the "null" class prefix
            // or not.
            packagePrefixList += JDK_PACKAGE_PREFIX;

            StringTokenizer packagePrefixIter =
                    new StringTokenizer(packagePrefixList, "|");

            while (handler == null
                    && packagePrefixIter.hasMoreTokens()) {

                String packagePrefix =
                        packagePrefixIter.nextToken().trim();

                // do not try to instantiate the JDK gopher handler
                // unless the system property had been explicitly set
                if (protocol.equalsIgnoreCase(GOPHER)
                        && packagePrefix.equals(JDK_PACKAGE_PREFIX)
                        && !enableGopher) {
                    continue;
                }
                try {
                    String clsName = packagePrefix + "." + protocol
                            + ".Handler";
                    Class cls = null;
                    try {
                        cls = Class.forName(clsName);
                    } catch (ClassNotFoundException e) {
                        ClassLoader cl = ClassLoader.getSystemClassLoader();
                        if (cl != null) {
                            cls = cl.loadClass(clsName);
                        }
                    }
                    if (cls != null) {
                        handler =
                                new MonitoredURLStreamHandler((URLStreamHandler) cls.newInstance(), urlMapper);
                    }
                } catch (Exception e) {
                    // any number of exceptions can get thrown here
                }
            }
        }

        if (handler != null) {
            MonitoredURLStreamHandler handler2 = null;

            try {
                handler2 = handlers.putIfAbsent(protocol, handler);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Could not initialize monitored url stream handler for handler class " + handler.getClass().getName(), ex);
            }

            if (handler2 != null) {
                handler = handler2;
            }
        }

        return handler;
    }
}
