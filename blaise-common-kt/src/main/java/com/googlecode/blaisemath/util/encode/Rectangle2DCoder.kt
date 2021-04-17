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
import com.googlecode.blaisemath.util.geom.Rectangle2
import java.awt.geom.Rectangle2D
import java.util.regex.Pattern

/**
 * Convert [Rectangle2D] to/from strings, of the form "rectangle2d(x,y,wid,ht)". Requires non-null values.
 */
object Rectangle2DCoder : StringCoder<Rectangle2D> {

    override fun encode(v: Rectangle2D) = String.format("rectangle2d(%f,%f,%f,%f)", v.x, v.y, v.width, v.height)

    override fun decode(v: String): Rectangle2D? {
        val m = Pattern.compile("rectangle2d\\s*\\((.*),(.*),(.*),(.*)\\)").matcher(v.toLowerCase().trim { it <= ' ' })
        return if (m.matches()) {
            try {
                Rectangle2(m.groupAsDouble(1), m.groupAsDouble(2), m.groupAsDouble(3), m.groupAsDouble(4))
            } catch (x: NumberFormatException) {
                fine<Rectangle2DCoder>("Not a double", x)
                null
            }
        } else {
            fine<Rectangle2DCoder>("Not a valid rectangle: $v")
            null
        }
    }

}