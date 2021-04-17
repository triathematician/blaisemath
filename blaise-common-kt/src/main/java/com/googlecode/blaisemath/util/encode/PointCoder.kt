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
package com.googlecode.blaisemath.util.encode

import com.googlecode.blaisemath.util.kotlin.fine
import com.googlecode.blaisemath.util.kotlin.javaTrim
import java.awt.Point
import java.util.regex.Matcher
import java.util.regex.Pattern

/** Convert [Point] to/from strings of the form "(1,2)". */
object PointCoder : StringCoder<Point> {

    override fun encode(v: Point) = String.format("(%d,%d)", v.x, v.y)

    override fun decode(v: String): Point? {
        val m = Pattern.compile("\\((.*),(.*)\\)").matcher(v.toLowerCase().javaTrim())
        return if (m.matches()) {
            try {
                Point(m.groupAsInt(1), m.groupAsInt(2))
            } catch (x: NumberFormatException) {
                fine<PointCoder>("Not an integer", x)
                null
            }
        } else {
            fine<PointCoder>("Not a valid point: $v")
            null
        }
    }

}

internal fun Matcher.groupAsInt(num: Int) = group(num).trim { it <= ' ' }.toInt()
internal fun Matcher.groupAsDouble(num: Int) = group(num).trim { it <= ' ' }.toDouble()