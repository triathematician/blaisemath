package com.googlecode.blaisemath.internal

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.beans.*
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

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
*/ /**
 * Some general utilities for working with Java types.
 *
 * @author Elisha Peterson
 */
object Reflection {
    private val LOG = Logger.getLogger(Reflection::class.java.name)
    private val FAIL_NEW_MSG: String? = "Failed to invoke constructor"
    private val FAIL_INVOKE_MSG: String? = "Failed to invoke read method"
    private val NO_READ_MSG: String? = "No read method available"
    private val NO_WRITE_MSG: String? = "No write method available"
    //region BeanInfo UTILITIES
    /**
     * Retrieves the BeanInfo for a Class
     * @param cls class
     * @return bean info
     */
    fun beanInfo(cls: Class<*>?): BeanInfo? {
        var beanInfo: BeanInfo? = null
        try {
            beanInfo = Introspector.getBeanInfo(cls)
        } catch (ex: IntrospectionException) {
            LOG.log(Level.WARNING, "Error in bean introspection for class $cls", ex)
        }
        return beanInfo
    }

    /**
     * Return an [PropertyDescriptor] for the specified object property.
     * @param cls the object class
     * @param propName the object's property
     * @return the descriptor
     * @throws IllegalArgumentException if there is no property with that name in the bean's class
     */
    fun propertyDescriptor(cls: Class<*>?, propName: String?): PropertyDescriptor? {
        for (pd in beanInfo(cls).getPropertyDescriptors()) {
            if (pd.name == propName) {
                return pd
            }
        }
        throw IllegalArgumentException("Unable to find property $propName in the class $cls")
    }

    /**
     * Return an [IndexedPropertyDescriptor] for the specified object property.
     * @param cls the object class
     * @param propName the object's property
     * @return the indexed descriptor
     * @throws IllegalArgumentException if there is no indexed property with that name in the bean's class
     */
    fun indexedPropertyDescriptor(cls: Class<*>?, propName: String?): IndexedPropertyDescriptor? {
        for (pd in beanInfo(cls).getPropertyDescriptors()) {
            if (pd.name == propName && pd is IndexedPropertyDescriptor) {
                return pd as IndexedPropertyDescriptor
            }
        }
        throw IllegalArgumentException("Unable to find property $propName in the class $cls")
    }

    /**
     * Execute read method by property descriptor. Returns null if there's an error, or if the read method returns null.
     * @param parent object
     * @param pd property descriptor
     * @return result
     */
    fun tryInvokeRead(parent: Any?, pd: PropertyDescriptor?): Any? {
        Objects.requireNonNull(parent)
        if (pd.getReadMethod() == null) {
            LOG.log(Level.FINE, NO_READ_MSG)
            return null
        }
        try {
            return pd.getReadMethod().invoke(parent)
        } catch (ex: IllegalAccessException) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex)
        } catch (ex: InvocationTargetException) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex)
        } catch (ex: IllegalArgumentException) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex)
        }
        return null
    }

    /**
     * Execute write method by property descriptor. Returns null if there's an error, or if the read method returns null.
     * @param parent object
     * @param pd property descriptor
     * @param val value to write
     * @return true if successfully written
     */
    fun tryInvokeWrite(parent: Any?, pd: PropertyDescriptor?, `val`: Any?): Boolean {
        Objects.requireNonNull(parent)
        if (pd.getWriteMethod() == null) {
            LOG.log(Level.FINE, NO_WRITE_MSG)
            return false
        }
        try {
            pd.getWriteMethod().invoke(parent, `val`)
            return true
        } catch (ex: IllegalAccessException) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex)
        } catch (ex: IllegalArgumentException) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex)
        } catch (ex: InvocationTargetException) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex)
        }
        return false
    }

    /**
     * Execute read method by indexed property descriptor. Returns null if there's an error, or if the read method returns null.
     * @param parent object
     * @param pd property descriptor
     * @param index index of property to write
     * @return result
     */
    fun tryInvokeIndexedRead(parent: Any?, pd: IndexedPropertyDescriptor?, index: Int): Any? {
        Objects.requireNonNull(parent)
        if (pd.getIndexedReadMethod() == null) {
            LOG.log(Level.FINE, NO_READ_MSG)
            return null
        }
        try {
            return pd.getIndexedReadMethod().invoke(parent, index)
        } catch (ex: IllegalAccessException) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex)
        } catch (ex: IllegalArgumentException) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex)
        } catch (ex: InvocationTargetException) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex)
        }
        return null
    }

    /**
     * Execute write method by indexed property descriptor. Returns null if there's an error, or if the read method returns null.
     * @param parent object
     * @param pd property descriptor
     * @param index index of property
     * @param value value for index
     */
    fun tryInvokeIndexedWrite(parent: Any?, pd: IndexedPropertyDescriptor?, index: Int, value: Any?) {
        Objects.requireNonNull(parent)
        if (pd.getIndexedWriteMethod() == null) {
            LOG.log(Level.FINE, NO_WRITE_MSG)
            return
        }
        try {
            pd.getIndexedWriteMethod().invoke(parent, index, value)
        } catch (ex: IllegalAccessException) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex)
        } catch (ex: IllegalArgumentException) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex)
        } catch (ex: InvocationTargetException) {
            LOG.log(Level.FINE, FAIL_INVOKE_MSG, ex)
        }
    }
    //endregion
    //region METHOD LOOKUPS
    /**
     * Utility method to lookup a public constructor, returning an empty optional if not found or security issue.
     * @param type class type
     * @param paramTypes method parameters
     * @return constructor if found
     */
    fun constructor(type: Class<*>?, vararg paramTypes: Class<*>?): Optional<Constructor<*>?>? {
        try {
            val c = type.getConstructor(*paramTypes)
            if (Modifier.isPublic(c.modifiers)) {
                return Optional.of(c)
            }
        } catch (ex: NoSuchMethodException) {
            LOG.log(Level.FINEST, "Not found", ex)
        } catch (ex: SecurityException) {
            LOG.log(Level.FINEST, "Not found", ex)
        }
        return Optional.empty()
    }

    /**
     * Utility method to lookup a public static method, returning an empty optional if not found or security issue.
     * @param type class type
     * @param name method name
     * @param paramTypes method parameters
     * @return method if found
     */
    fun staticMethod(type: Class<*>?, name: String?, vararg paramTypes: Class<*>?): Optional<Method?>? {
        try {
            val m = type.getMethod(name, *paramTypes)
            if (Modifier.isStatic(m.modifiers) && Modifier.isPublic(m.modifiers)) {
                return Optional.of(m)
            }
        } catch (ex: NoSuchMethodException) {
            LOG.log(Level.FINEST, "Not found", ex)
        } catch (ex: SecurityException) {
            LOG.log(Level.FINEST, "Not found", ex)
        }
        return Optional.empty()
    }

    /**
     * Utility method to lookup a public static method by one of several possible names, returning an empty optional if
     * not found or security issue.
     * @param type class type
     * @param names possible names
     * @param paramTypes method parameters
     * @return method if found
     */
    fun staticMethod(type: Class<*>?, names: Array<String?>?, vararg paramTypes: Class<*>?): Optional<Method?>? {
        for (n in names) {
            val m = staticMethod(type, n, *paramTypes)
            if (m.isPresent()) {
                return m
            }
        }
        return Optional.empty()
    }
    //endregion
    //region METHOD INVOCATION
    /**
     * Executes default constructor to return object of given type, if possible.
     * @param <T> type
     * @param cls type to construct
     * @return new instance, or null if unable to instantiate
    </T> */
    fun <T> tryInvokeNew(cls: Class<T?>?): T? {
        var con: Constructor<T?>? = null
        try {
            con = cls.getDeclaredConstructor()
        } catch (ex: NoSuchMethodException) {
            LOG.log(Level.FINE, "There is no no-arg constructor for $cls", ex)
        } catch (ex: SecurityException) {
            LOG.log(Level.FINE, "Unable to get no-arg constructor for $cls", ex)
        }
        if (con == null && Number::class.java.isAssignableFrom(cls)) {
            return if (cls == Int::class.java) 0 as Int as T else if (cls == Double::class.java) 0.0 as Double as T else if (cls == Float::class.java) 0f as Float as T else if (cls == Long::class.java) 0L as Long as T else if (cls == Short::class.java) 0 as Short as Short as T else if (cls == Byte::class.java) 0 as Byte as Byte as T else if (cls == Char::class.java) 0 as Char as Char as T else if (cls == Boolean::class.java) java.lang.Boolean.TRUE as T else null
        }
        if (con != null) {
            try {
                return con.newInstance()
            } catch (ex: InstantiationException) {
                LOG.log(Level.FINE, FAIL_NEW_MSG, ex)
            } catch (ex: IllegalAccessException) {
                LOG.log(Level.FINE, FAIL_NEW_MSG, ex)
            } catch (ex: IllegalArgumentException) {
                LOG.log(Level.FINE, FAIL_NEW_MSG, ex)
            } catch (ex: InvocationTargetException) {
                LOG.log(Level.FINE, FAIL_NEW_MSG, ex)
            }
        }
        return null
    }

    /**
     * Execute read method by property name. Returns null if there's an error, if no read method, or the read method returns null.
     * @param parent object
     * @param property name of property
     * @return result or read method, where null may indicate a failure to read
     */
    fun tryInvokeRead(parent: Any?, property: String?): Any? {
        Objects.requireNonNull(parent)
        return tryInvokeRead(parent, propertyDescriptor(parent.javaClass, property))
    }

    /**
     * Execute write method by property descriptor. Returns null if there's an error, or if the read method returns null.
     * @param parent object
     * @param property name of property
     * @param val value to write
     * @return true if successfully written
     */
    fun tryInvokeWrite(parent: Any?, property: String?, `val`: Any?): Boolean {
        Objects.requireNonNull(parent)
        return tryInvokeWrite(parent, propertyDescriptor(parent.javaClass, property), `val`)
    } //endregion
}