package com.googlecode.blaisemath.style

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
*/

/**
 * Utilities for accessing values within styles.
 */
object StyleHelp {

    /** Get the first non-null color within a style by key, or default if none found. */
    fun firstColor(style: AttributeSet, def: Color?, vararg keys: String)
            = keys.asSequence().map { style[it] as? Color }.firstOrNull() ?: def

    /** Get the first non-null color within a style by key, or default if none found. */
    fun firstFloat(style: AttributeSet, def: Float?, vararg keys: String)
            = keys.asSequence().map { style[it] as? Float }.firstOrNull() ?: def

    /**
     * Gets subset of style starting with the given prefix, assuming a "dot notation".
     * If the prefix is "a.b" for instance, the result will include any parameters "a.b.x",
     * any parameters "a.x", and any parameters "x" as just "x".
     */
    fun cascadingStyle(style: AttributeSet, prefix: String, defStyle: AttributeSet) = AttributeSet().apply {
        addStylesIfAbsent(style, prefix)
        defStyle.getAllAttributes().onEach { putIfAbsent(it, defStyle[it]) }
    }

    /** Read a collection of styles defined within a properties file. Searches among provided prefixes only. */
    fun readStyles(props: Properties, vararg prefixes: String): Map<String, AttributeSet> {
        val res = mutableMapOf<String, AttributeSet>()
        for (key in props.stringPropertyNames()) {
            prefixes.filter { key.startsWith(it) }
                    .mapNotNull { readStyle(props.getProperty(key)) }
                    .firstOrNull()?.let { res[key] = it }
        }
        return res
    }

    /** Creates a style from its encoded string. See [AttributeSetCoder]. */
    fun readStyle(styleString: String) = AttributeSetCoder().decode(styleString)

    /**
     * Adds styles starting with the given prefix in [source], after stripping the prefix.
     * This might be used, for instance, to map "title.color" and "title.font-size" in [source] to "color" and "font-size".
     * This works recursively, so "title.first.color" and "title.first.font-size" would be added under a recursive call with prefix "title.first".
     */
    private fun AttributeSet.addStylesIfAbsent(source: AttributeSet, prefix: String) {
        // add prefix content
        source.getAllAttributes()
                .filter { it.startsWith("$prefix.") }
                .onEach { key ->
                    val suffix = key.substring(prefix.length + 1)
                    if (suffix.isNotEmpty() && !suffix.contains(".") && !contains(suffix)) {
                        put(suffix, source[key])
                    }
                }

        // add content from the parent prefix.
        if (prefix.contains(".")) {
            val parentPrefix = prefix.substring(0, prefix.lastIndexOf('.'))
            addStylesIfAbsent(source, parentPrefix)
        }
    }
}