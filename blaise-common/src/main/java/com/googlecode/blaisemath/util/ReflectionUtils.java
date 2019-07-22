package com.googlecode.blaisemath.util;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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

import java.beans.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Some general utilities for working with Java types.
 *
 * @author Elisha Peterson
 */
public class ReflectionUtils {

    private static final Logger LOG = Logger.getLogger(ReflectionUtils.class.getName());

    private static final String FAIL_NEW_MSG = "Failed to invoke constructor";
    private static final String FAIL_INVOKE_MSG = "Failed to invoke read method";
    private static final String NO_READ_MSG = "No read method available";
    private static final String NO_WRITE_MSG = "No write method available";

    // utility class
    private ReflectionUtils() {
    }

    /**
     * Retrieves the BeanInfo for a Class
     * @param cls class
     * @return bean info
     */
    public static BeanInfo getBeanInfo(Class<?> cls) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(cls);
        } catch (IntrospectionException ex) {
            LOG.log(Level.WARNING, "Error in bean introspection for class " + cls, ex);
        }
        return beanInfo;
    }

    /**
     * Return an {@link IndexedPropertyDescriptor} for the specified object property.
     * @param cls the object class
     * @param propName the object's property
     * @return the indexed descriptor
     * @throws IllegalArgumentException if there is no indexed property with that name in the bean's class
     */
    public static IndexedPropertyDescriptor indexedPropertyDescriptor(Class<?> cls, String propName) {
        BeanInfo info = ReflectionUtils.getBeanInfo(cls);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            if (pd.getName().equals(propName) && pd instanceof IndexedPropertyDescriptor) {
                return (IndexedPropertyDescriptor) pd;
            }
        }
        throw new IllegalArgumentException("Unable to find property " + propName
                + " in the class " + cls);
    }
    
    /**
     * Utility method to lookup a public static method, returning an empty optional if not found.
     * @param type class type
     * @param name method name
     * @param paramTypes method parameters
     * @return method if found
     */
    public static Optional<Method> findStaticMethod(Class<?> type, String name, Class... paramTypes) {
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

    public static <T> T tryInvokeNew(Class<T> cls) {
        Constructor<T> con = null;
        try {
            con = cls.getDeclaredConstructor();
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.FINE, "There is no no-arg constructor for " + cls, ex);
        } catch (SecurityException ex) {
            LOG.log(Level.FINE, "Unable to get no-arg constructor for " + cls, ex);
        }
        if (con == null && Number.class.isAssignableFrom(cls)) {
            return cls == Integer.class ? (T) new Integer(0)
                    : cls == Double.class ? (T) new Double(0)
                    : cls == Float.class ? (T) new Float(0)
                    : cls == Long.class ? (T) new Long(0)
                    : cls == Short.class ? (T) new Short((short) 0)
                    : cls == Byte.class ? (T) new Byte((byte) 0)
                    : cls == Character.class ? (T) new Character((char) 0)
                    : cls == Boolean.class ? (T) Boolean.TRUE
                    : null;
        }
        if (con != null) {
            try {
                return con.newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LOG.log(Level.FINE, FAIL_NEW_MSG, ex);
            }
        }
        return null;
    }

    public static Object tryInvokeRead(Object parent, PropertyDescriptor pd) {
        if (parent == null) {
            throw new IllegalArgumentException();
        }
        if (pd.getReadMethod() == null) {
            LOG.log(Level.FINE, NO_READ_MSG);
            return null;
        }
        try {
            return pd.getReadMethod().invoke(parent);
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException ex) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex);
        }
        return null;
    }

    public static boolean tryInvokeWrite(Object parent, PropertyDescriptor pd, Object val) {
        if (parent == null) {
            throw new IllegalArgumentException();
        }
        if (pd.getWriteMethod() == null) {
            LOG.log(Level.FINE, NO_WRITE_MSG);
            return false;
        }
        try {
            pd.getWriteMethod().invoke(parent, val);
            return true;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex);
        }
        return false;
    }

    public static Object tryInvokeIndexedRead(Object parent, IndexedPropertyDescriptor pd, int index) {
        if (parent == null) {
            throw new IllegalArgumentException();
        }
        if (pd.getIndexedReadMethod() == null) {
            LOG.log(Level.FINE, NO_READ_MSG);
            return null;
        }
        try {
            return pd.getIndexedReadMethod().invoke(parent, index);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex);
        }
        return null;
    }

    public static boolean tryInvokeIndexedWrite(Object parent, IndexedPropertyDescriptor pd, int index, Object value) {
        if (parent == null) {
            throw new IllegalArgumentException();
        }
        if (pd.getIndexedWriteMethod() == null) {
            LOG.log(Level.FINE, NO_WRITE_MSG);
            return false;
        }
        try {
            pd.getIndexedWriteMethod().invoke(parent, index, value);
            return true;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex);
        }
        return false;
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
    public static Optional<Constructor> findConstructor(Class<?> type, Class... paramTypes) {
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
