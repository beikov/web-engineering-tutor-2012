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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian
 */
public class MonitoredURLStreamHandler extends URLStreamHandler {

    private static final Logger LOG = Logger.getLogger(MonitoredURLStreamHandler.class.getName());
    private final URLStreamHandler delegate;
    private final URLMapper urlMapper;
    private final Method openConnection1;
    private final Method openConnection2;
    private final Method getDefaultPort;
    private final Method equals;
    private final Method hashCode;
    private final Method sameFile;
    private final Method getHostAddress;
    private final Method hostsEqual;
    private final Method toExternalForm;

    public MonitoredURLStreamHandler(URLStreamHandler delegate, URLMapper urlMapper) throws Exception {
        this.delegate = delegate;
        this.urlMapper = urlMapper;
        openConnection1 = findMethod(delegate, "openConnection", URL.class);
        openConnection2 = findMethod(delegate, "openConnection", URL.class, Proxy.class);
        getDefaultPort = findMethod(delegate, "getDefaultPort");
        equals = findMethod(delegate, "equals", URL.class, URL.class);
        hashCode = findMethod(delegate, "hashCode", URL.class);
        sameFile = findMethod(delegate, "sameFile", URL.class, URL.class);
        getHostAddress = findMethod(delegate, "getHostAddress", URL.class);
        hostsEqual = findMethod(delegate, "hostsEqual", URL.class, URL.class);
        toExternalForm = findMethod(delegate, "toExternalForm", URL.class);
    }

    private static Method findMethod(Object object, String method, Class<?>... argumentTypes) {
        Method m = null;
        
        for (Class<?> clazz = object.getClass(); !Object.class.equals(clazz); clazz = clazz.getSuperclass()) {
            try {
                m = clazz.getDeclaredMethod(method, argumentTypes);
                break;
            } catch (NoSuchMethodException ex) {
                continue;
            }
        }
        
        if(m != null) {
            m.setAccessible(true);
        }

        return m;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        try {
            return (URLConnection) openConnection1.invoke(delegate, urlMapper.map(u));
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof IOException) {
                throw (IOException) ex.getTargetException();
            } else if (ex.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) ex.getTargetException();
            } else {
                throw new RuntimeException(ex.getTargetException());
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected URLConnection openConnection(URL u, Proxy p) throws IOException {
        try {
            return (URLConnection) openConnection2.invoke(delegate, urlMapper.map(u), p);
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof IOException) {
                throw (IOException) ex.getTargetException();
            } else if (ex.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) ex.getTargetException();
            } else {
                throw new RuntimeException(ex.getTargetException());
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private <T> T invoke(Method method, Object... args) {
        try {
            return (T) method.invoke(delegate, args);
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) ex.getTargetException();
            } else {
                throw new RuntimeException(ex.getTargetException());
            }
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected int getDefaultPort() {
        return invoke(getDefaultPort);
    }

    @Override
    protected boolean equals(URL u1, URL u2) {
        return invoke(equals, urlMapper.map(u1), urlMapper.map(u2));
    }

    @Override
    protected int hashCode(URL u) {
        return invoke(hashCode, u);
    }

    @Override
    protected boolean sameFile(URL u1, URL u2) {
        return invoke(sameFile, urlMapper.map(u1), urlMapper.map(u2));
    }

    @Override
    protected synchronized InetAddress getHostAddress(URL u) {
        return invoke(getHostAddress, urlMapper.map(u));
    }

    @Override
    protected boolean hostsEqual(URL u1, URL u2) {
        return invoke(hostsEqual, urlMapper.map(u1), urlMapper.map(u2));
    }

    @Override
    protected String toExternalForm(URL u) {
        return invoke(toExternalForm, u);
    }
}
