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
import java.awt.Rectangle
import java.util.regex.Pattern

/**
 * Adapter converting Rectangle to/from strings, of the form "rectangle(x,y,wid,ht)". Requires non-null values.
 */
object RectangleCoder : StringCoder<Rectangle> {

    override fun encode(v: Rectangle) = String.format("rectangle(%d,%d,%d,%d)", v.x, v.y, v.width, v.height)

    override fun decode(v: String): Rectangle? {
        val m = Pattern.compile("rectangle\\s*\\((.*),(.*),(.*),(.*)\\)").matcher(v.toLowerCase().trim { it <= ' ' })
        return if (m.matches()) {
            try {
                Rectangle(m.groupAsInt(1), m.groupAsInt(2), m.groupAsInt(3), m.groupAsInt(4))
            } catch (x: NumberFormatException) {
                fine<RectangleCoder>("Not an integer", x)
                null
            }
        } else {
            fine<RectangleCoder>("Not a valid rectangle: $v")
            null
        }
    }

}