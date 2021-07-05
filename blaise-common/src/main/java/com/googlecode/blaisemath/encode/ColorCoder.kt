package com.googlecode.blaisemath.encode

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.Colors
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
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
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* #L%
*/ /**
 * Converts colors to/from strings. Requires non-null colors and strings.
 *
 * @author Elisha Peterson
 */
class ColorCoder : StringEncoder<Color?>, StringDecoder<Color?> {
    override fun encode(obj: Color?): String? {
        return Colors.encode(obj)
    }

    override fun decode(str: String?): Color? {
        return Colors.decode(str)
    }

    companion object {
        /**
         * Checks whether a string is decodable as a color.
         * @param str to test
         * @return true if matches
         */
        fun decodable(str: String?): Boolean {
            return (str.matches("#[0-9a-fA-f]{3}")
                    || str.matches("#[0-9a-fA-f]{6}")
                    || str.matches("#[0-9a-fA-f]{8}"))
        }
    }
}