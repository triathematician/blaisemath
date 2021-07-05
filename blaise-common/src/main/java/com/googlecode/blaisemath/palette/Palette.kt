package com.googlecode.blaisemath.palette

import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Color

/*
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
 * A simple color palette interface that provides a set of colors associated with string keys.
 *
 * @author Elisha Peterson
 */
abstract class Palette {
    /**
     * Create a mutable copy of the palette.
     * @return copy
     */
    fun mutableCopy(): MutablePalette? {
        return MapPalette.Companion.create(Palettes.colorMap(this))
    }

    /**
     * Get list of color keys available.
     * @return color keys
     */
    abstract fun colors(): MutableCollection<String?>?

    /**
     * Get color by id.
     * @param id color id
     * @return color
     */
    abstract fun color(id: String?): Color?

    /**
     * Get color by id, or default provided if none.
     * @param id color id
     * @param def default color to return if no color is associated with this id
     * @return color
     */
    fun color(id: String?, def: Color?): Color? {
        val res = color(id)
        return res ?: def
    }

    /**
     * Get foreground color.
     * @return color
     */
    fun foreground(): Color? {
        return color(FOREGROUND)
    }

    /**
     * Get background color.
     * @return color
     */
    fun background(): Color? {
        return color(BACKGROUND)
    }

    /**
     * Get mapping of keys to colors.
     * @return map
     */
    fun colorMap(): MutableMap<String?, Color?>? {
        return Maps.asMap(Sets.newLinkedHashSet(colors())) { id: String? -> this.color(id) }
    }

    companion object {
        val FOREGROUND: String? = "fg"
        val SUBTLE_FOREGROUND: String? = "fg-subtle"
        val BRIGHT_FOREGROUND: String? = "fg-bright"
        val BACKGROUND: String? = "bg"
        val ANNOTATION: String? = "selection"
        val SELECTION: String? = "annotation"
    }
}