package com.googlecode.blaisemath.encode

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.geom.Rectangle2D
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Pattern

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
 * Adapter converting Rectangle2D to/from strings, of the form "rectangle2d(x,y,wid,ht)". Requires non-null values.
 *
 * @author Elisha Peterson
 */
class Rectangle2DCoder : StringEncoder<Rectangle2D?>, StringDecoder<Rectangle2D?> {
    override fun encode(v: Rectangle2D?): String? {
        Objects.requireNonNull(v)
        return String.format("rectangle2d(%f,%f,%f,%f)", v.getX(), v.getY(), v.getWidth(), v.getHeight())
    }

    override fun decode(v: String?): Rectangle2D? {
        Objects.requireNonNull(v)
        val m = Pattern.compile("rectangle2d\\s*\\((.*),(.*),(.*),(.*)\\)").matcher(v.toLowerCase().trim { it <= ' ' })
        return if (m.matches()) {
            try {
                val x = m.group(1).toDouble()
                val y = m.group(2).toDouble()
                val w = m.group(3).toDouble()
                val h = m.group(4).toDouble()
                Rectangle2D.Double(x, y, w, h)
            } catch (x: NumberFormatException) {
                LOG.log(Level.FINEST, "Not a double", x)
                null
            }
        } else {
            LOG.log(Level.FINEST, "Not a valid rectangle", v)
            null
        }
    }

    companion object {
        private val LOG = Logger.getLogger(Rectangle2DCoder::class.java.name)
    }
}