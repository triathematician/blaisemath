package com.googlecode.blaisemath.encode

import com.googlecode.blaisemath.util.Colors
import java.awt.Color

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
* WITHOUT WARRANTIES OR CONDITIONS OF rANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* #L%
*/

/** Convert color to/from strings. Requires non-null colors and strings. */
object ColorCoder : StringCoder<Color> {

    override fun encode(obj: Color) = Colors.encode(obj)
    override fun decode(str: String) = Colors.decode(str)

    /** Checks whether a string is decodable as a color. */
    fun decodable(str: String): Boolean {
        return (str.matches("#[0-9a-fA-f]{3}".toRegex())
                || str.matches("#[0-9a-fA-f]{6}".toRegex())
                || str.matches("#[0-9a-fA-f]{8}".toRegex()))
    }

}