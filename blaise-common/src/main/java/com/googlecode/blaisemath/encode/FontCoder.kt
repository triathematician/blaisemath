package com.googlecode.blaisemath.encode

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Font

/*
* #%L
* BlaiseCommon
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
 * Adapter converting fonts to/from strings. Requires non-null values.
 *
 * @see Font.decode
 * @author Elisha Peterson
 */
class FontCoder : StringEncoder<Font?>, StringDecoder<Font?> {
    override fun encode(c: Font?): String? {
        val styStr = if (c.isPlain()) "PLAIN" else if (c.isBold() && c.isItalic()) "BOLDITALIC" else if (c.isBold()) "BOLD" else "ITALIC"
        return String.format("%s-%s-%s", c.getFamily(), styStr, "" + c.getSize())
    }

    override fun decode(v: String?): Font? {
        return Font.decode(v)
    }
}