package com.googlecode.blaisemath.palette

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
 * A color palette that can be edited.
 * @author Elisha Peterson
 */
abstract class MutablePalette : Palette() {
    /**
     * Get the name of the palette.
     * @return name
     */
    abstract fun getName(): String?

    /**
     * Remove a key from the palette.
     * @param key to remove
     * @return the color removed, if present, or null
     */
    abstract fun remove(key: String?): Color?

    /**
     * Set or update a color in the palette.
     * @param key color's key
     * @param value color
     */
    abstract operator fun set(key: String?, value: Color?)

    /**
     * Sets a color and returns this.
     * @param key color's key
     * @param value color
     * @return this
     */
    fun and(key: String?, value: Color?): MutablePalette? {
        set(key, value)
        return this
    }
}