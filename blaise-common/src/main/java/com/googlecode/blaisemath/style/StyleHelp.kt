package com.googlecode.blaisemath.style

import com.google.common.collect.Maps
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Color
import java.util.*

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
 * Utilities for accessing values within styles.
 * @author Elisha Peterson
 */
object StyleHelp {
    /**
     * Get the first non-null color within a style, or default if none found.
     * @param style style to search
     * @param def default color
     * @param keys keys to lookup
     * @return first non-null color, or default
     */
    fun firstColor(style: AttributeSet?, def: Color?, vararg keys: String?): Color? {
        for (k in keys) {
            val c = style.getColor(k)
            if (c != null) {
                return c
            }
        }
        return def
    }

    /**
     * Get the first non-null color within a style, or default if none found.
     * @param style style to search
     * @param def default color
     * @param keys keys to lookup
     * @return first non-null color, or default
     */
    fun firstFloat(style: AttributeSet?, def: Float?, vararg keys: String?): Float? {
        for (k in keys) {
            val c = style.getFloat(k)
            if (c != null) {
                return c
            }
        }
        return def
    }

    /**
     * Gets subset of style starting with the given prefix, assuming a "dot notation".
     * If the prefix is "a.b" for instance, the result will include any parameters "a.b.x",
     * any parameters "a.x", and any parameters "x" as just "x".
     *
     * @param style base style object, with general parameters
     * @param prefix style prefix to lookup
     * @param defStyle default style parameters
     * @return constructed style
     */
    fun cascadingStyle(style: AttributeSet?, prefix: String?, defStyle: AttributeSet?): AttributeSet? {
        val res = AttributeSet()
        addStylesIfAbsent(res, style, prefix)
        for (k in defStyle.getAllAttributes()) {
            if (!res.contains(k)) {
                res.put(k, defStyle.get(k))
            }
        }
        return res
    }

    /**
     * Read a collection of styles defined within a properties file. Searches among provided prefixes only.
     * @param props properties to search
     * @param prefixes prefixes to search with
     * @return indexed collection of styles
     */
    fun readStyles(props: Properties?, vararg prefixes: String?): MutableMap<String?, AttributeSet?>? {
        val res: MutableMap<String?, AttributeSet?>? = Maps.newLinkedHashMap()
        for (key in props.stringPropertyNames()) {
            for (p in prefixes) {
                if (!key.startsWith(p)) {
                    continue
                }
                val sty = readStyle(props.getProperty(key))
                if (sty != null) {
                    res[key] = sty
                    break
                }
            }
        }
        return res
    }

    fun readStyle(property: String?): AttributeSet? {
        return AttributeSetCoder().decode(property)
    }

    private fun addStylesIfAbsent(result: AttributeSet?, style: AttributeSet?, prefix: String?) {
        // add prefix content
        for (k in style.getAllAttributes()) {
            if (k.startsWith("$prefix.")) {
                val suffix = k.substring(prefix.length + 1)
                if (suffix.length > 0 && !suffix.contains(".") && !result.contains(suffix)) {
                    result.put(suffix, style.get(k))
                }
            }
        }

        // add parent content
        if (prefix.contains(".")) {
            val parentPrefix = prefix.substring(0, prefix.lastIndexOf('.'))
            addStylesIfAbsent(result, style, parentPrefix)
        }
    }
}