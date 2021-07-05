package com.googlecode.blaisemath.encode

import com.google.common.base.Strings
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Insets
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
 * Adapter converting insets to/from strings of the form "insets(top,left,bottom,right)". Requires non-null values.
 *
 * @author Elisha Peterson
 */
class InsetsCoder : StringEncoder<Insets?>, StringDecoder<Insets?> {
    override fun encode(v: Insets?): String? {
        Objects.requireNonNull(v)
        return String.format("insets(%d,%d,%d,%d)", v.top, v.left, v.bottom, v.right)
    }

    override fun decode(v: String?): Insets? {
        if (Strings.isNullOrEmpty(v)) {
            return null
        }
        val m = Pattern.compile("insets\\s*\\((.*),(.*),(.*),(.*)\\)").matcher(v.toLowerCase().trim { it <= ' ' })
        return if (m.matches()) {
            try {
                val t = m.group(1).toInt()
                val l = m.group(2).toInt()
                val b = m.group(3).toInt()
                val r = m.group(4).toInt()
                Insets(t, l, b, r)
            } catch (x: NumberFormatException) {
                LOG.log(Level.FINEST, "Not an integer", x)
                null
            }
        } else {
            LOG.log(Level.FINEST, "Not a valid insets", v)
            null
        }
    }

    companion object {
        private val LOG = Logger.getLogger(InsetsCoder::class.java.name)
    }
}