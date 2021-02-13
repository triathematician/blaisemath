package com.googlecode.blaisemath.util.type;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2021 Elisha Peterson
 * --
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
 * #L%
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Some general utilities for working with Java types.
 * @author Elisha Peterson
 */
public class ReflectionUtils {

    private static final Logger LOG = Logger.getLogger(ReflectionUtils.class.getName());
    
    private ReflectionUtils() {
    }
    
    /**
     * Utility method to lookup a public static method, returning an empty optional if not found.
     * @param type class type
     * @param name method name
     * @param paramTypes method parameters
     * @return method if found
     */
    public static Optional<Method> findStaticMethod(Class type, String name, Class... paramTypes) {
        try {
            Method m = type.getMethod(name, paramTypes);
            if (Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers())) {
                return Optional.of(m);
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            LOG.log(Level.FINEST, "Not found", ex);
        }
        return Optional.empty();
    }
    
    /**
     * Utility method to lookup a public static method by one of several possible names, returning an empty optional if
     * not found.
     * @param type class type
     * @param names possible names
     * @param paramTypes method parameters
     * @return method if found
     */
    public static Optional<Method> findStaticMethod(Class type, String[] names, Class... paramTypes) {
        for (String n : names) {
            Optional<Method> m = findStaticMethod(type, n, paramTypes);
            if (m.isPresent()) {
                return m;
            }
        }
        return Optional.empty();
    }
    
    /**
     * Utility method to lookup a constructor, returning an empty optional if not found.
     * @param type class type
     * @param paramTypes method parameters
     * @return constructor if found
     */
    public static Optional<Constructor> findConstructor(Class type, Class... paramTypes) {
        try {
            Constructor c = type.getConstructor(paramTypes); 
            if (Modifier.isPublic(c.getModifiers())) {
                return Optional.of(c);
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            LOG.log(Level.FINEST, "Not found", ex);
        }
        return Optional.empty();
    }
    
}
