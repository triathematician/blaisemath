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
import com.googlecode.blaisemath.util.geom.Point2
import java.awt.geom.Point2D
import java.util.regex.Pattern

/**
 * Adapter converting Point2D to/from strings of the form "(1.1,2)". Requires non-null values.
 */
object Point2DCoder : StringCoder<Point2D> {
    override fun encode(obj: Point2D) = String.format("(%f,%f)", obj.x, obj.y)

    override fun decode(obj: String): Point2D? {
        val m = Pattern.compile("\\((.*),(.*)\\)").matcher(obj.toLowerCase().trim { it <= ' ' })
        return if (m.matches()) {
            try {
                Point2(m.groupAsDouble(1), m.groupAsDouble(2))
            } catch (x: NumberFormatException) {
                fine<Point2DCoder>("Not a double", x)
                null
            }
        } else {
            fine<Point2DCoder>("Not a valid point: $obj")
            null
        }
    }

}