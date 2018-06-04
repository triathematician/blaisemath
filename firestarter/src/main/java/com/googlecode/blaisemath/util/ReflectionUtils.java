/**
 * ReflectionUtils.java Created Sep 20, 2014
 */
package com.googlecode.blaisemath.util;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility functions for working with reflection API, e.g. for catching
 * potential exceptions when invoking methods.
 *
 * @author Elisha
 */
public class ReflectionUtils {

    private static final String FAIL_NEW_MSG = "Failed to invoke constructor";
    private static final String FAIL_INVOKE_MSG = "Failed to invoke read method";
    private static final String NO_READ_MSG = "No read method available";
    private static final String NO_WRITE_MSG = "No writemethod available";

    // utility class
    private ReflectionUtils() {
    }

    /**
     * Retrieves the BeanInfo for a Class
     *
     * @param cls
     * @return
     */
    public static BeanInfo getBeanInfo(Class<?> cls) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(cls);
        } catch (IntrospectionException ex) {
            Logger.getLogger(ReflectionUtils.class.getName())
                    .log(Level.WARNING, "Error in bean introspection for class " + cls, ex);
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

    public static <T> T tryInvokeNew(Class<T> cls) {
        Constructor<T> con = null;
        try {
            con = cls.getDeclaredConstructor();
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    "There is no no-arg constructor for " + cls, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    "Unable to get no-arg constructor for " + cls, ex);
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
            } catch (InstantiationException ex) {
                Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                        FAIL_NEW_MSG, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                        FAIL_NEW_MSG, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                        FAIL_NEW_MSG, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                        FAIL_NEW_MSG, ex);
            }
        }
        return null;
    }

    public static Object tryInvokeRead(Object parent, PropertyDescriptor pd) {
        if (parent == null) {
            throw new IllegalArgumentException();
        }
        if (pd.getReadMethod() == null) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    NO_READ_MSG);
            return null;
        }
        try {
            return pd.getReadMethod().invoke(parent);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    FAIL_INVOKE_MSG, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    FAIL_INVOKE_MSG, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    FAIL_INVOKE_MSG, ex);
        }
        return null;
    }

    public static boolean tryInvokeWrite(Object parent, PropertyDescriptor pd, Object val) {
        if (parent == null) {
            throw new IllegalArgumentException();
        }
        if (pd.getWriteMethod() == null) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    NO_WRITE_MSG);
            return false;
        }
        try {
            pd.getWriteMethod().invoke(parent, val);
            return true;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    FAIL_INVOKE_MSG, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    FAIL_INVOKE_MSG, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    FAIL_INVOKE_MSG, ex);
        }
        return false;
    }

    public static Object tryInvokeIndexedRead(Object parent, IndexedPropertyDescriptor pd, int index) {
        if (parent == null) {
            throw new IllegalArgumentException();
        }
        if (pd.getIndexedReadMethod() == null) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    NO_READ_MSG);
            return null;
        }
        try {
            return pd.getIndexedReadMethod().invoke(parent, index);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    FAIL_INVOKE_MSG, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    FAIL_INVOKE_MSG, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    FAIL_INVOKE_MSG, ex);
        }
        return null;
    }

    public static boolean tryInvokeIndexedWrite(Object parent, IndexedPropertyDescriptor pd, int index, Object value) {
        if (parent == null) {
            throw new IllegalArgumentException();
        }
        if (pd.getIndexedWriteMethod() == null) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    NO_WRITE_MSG);
            return false;
        }
        try {
            pd.getIndexedWriteMethod().invoke(parent, index, value);
            return true;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    FAIL_INVOKE_MSG, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    FAIL_INVOKE_MSG, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.FINE,
                    FAIL_INVOKE_MSG, ex);
        }
        return false;
    }
}
