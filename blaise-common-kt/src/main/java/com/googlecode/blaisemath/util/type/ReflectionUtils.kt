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
package com.googlecode.blaisemath.util.type

import com.googlecode.blaisemath.util.kotlin.fine
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * Some general utilities for working with Java types.
 */
object ReflectionUtils {
    /**
     * Lookup a public static method, returning an empty optional if not found.
     * @param type class type
     * @param name method name
     * @param paramTypes method parameters
     * @return method if found
     */
    fun findStaticMethod(type: Class<*>, name: String, vararg paramTypes: Class<*>): Method? {
        try {
            val m = type.getMethod(name, *paramTypes)
            if (Modifier.isStatic(m.modifiers) && Modifier.isPublic(m.modifiers)) {
                return m
            }
        } catch (ex: NoSuchMethodException) {
            fine<ReflectionUtils>("Not found", ex)
        } catch (ex: SecurityException) {
            fine<ReflectionUtils>("Not found", ex)
        }
        return null
    }

    /**
     * Lookup a public static method by one of several possible names.
     * @param type class type
     * @param names possible names
     * @param paramTypes method parameters
     * @return method if found
     */
    fun findStaticMethod(type: Class<*>, names: Array<String>, vararg paramTypes: Class<*>): Method? {
        for (n in names) {
            findStaticMethod(type, n, *paramTypes)?.let { return it }
        }
        return null
    }

    /**
     * Lookup a constructor.
     * @param type class type
     * @param paramTypes method parameters
     * @return constructor if found
     */
    fun <X> findConstructor(type: Class<X>, vararg paramTypes: Class<*>): Constructor<X>? {
        try {
            val c = type.getConstructor(*paramTypes)
            if (Modifier.isPublic(c.modifiers)) return c
        } catch (ex: NoSuchMethodException) {
            fine<ReflectionUtils>("Not found", ex)
        } catch (ex: SecurityException) {
            fine<ReflectionUtils>("Not found", ex)
        }
        return null
    }
}