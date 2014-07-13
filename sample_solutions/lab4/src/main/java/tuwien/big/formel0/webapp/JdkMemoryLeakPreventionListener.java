/*
 * Copyright 2013 Christian.
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
package tuwien.big.formel0.webapp;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * This listener actually makes redeployment within IDEs usable!
 *
 * The root cause for the memory leak is that jboss logging, dependency of
 * hibernate, implements a {@link java.util.logging.Level}. When an instance of
 * that class is made, {@link java.util.logging.Level} saves a reference to that
 * instance. This is where the instance and therefore the whole web application
 * leaks. On redeployments the class loader for the webapp is still reachable
 * via the custom level contained in {@link java.util.logging.Level#known}.
 *
 * The bug was already reported and is scheduled for Java 9. Also see the bug
 * report: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6543126
 *
 * @author Christian Beikov
 * @see java.util.logging.Level
 */
@WebListener
public class JdkMemoryLeakPreventionListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        // Do some memory leak prevention
        Field knownField = null;
        Boolean accessible = null;

        try {
            knownField = Level.class.getDeclaredField("known");
            accessible = knownField.isAccessible();
            knownField.setAccessible(true);
            List<Level> known = (List<Level>) knownField.get(null);
            Iterator<Level> knownIter = known.iterator();
            ClassLoader webappClassLoader = getClass().getClassLoader();

            while (knownIter.hasNext()) {
                Level level = knownIter.next();

                if (webappClassLoader.equals(level.getClass().getClassLoader())) {
                    knownIter.remove();
                }
            }
        } catch (Exception ex) {
            // Nothing we can do about this :(
        } finally {
            if (knownField != null && accessible != null) {
                knownField.setAccessible(accessible);
            }
        }
    }
}
