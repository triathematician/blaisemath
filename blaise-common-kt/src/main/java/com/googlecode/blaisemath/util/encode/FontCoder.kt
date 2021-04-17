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
*/
package com.googlecode.blaisemath.util.encode

import java.awt.Font

/** Adapter converting fonts to/from strings. Requires non-null values. */
object FontCoder : StringCoder<Font> {

    override fun encode(obj: Font): String {
        val styStr = when {
            obj.isPlain -> "PLAIN"
            obj.isBold && obj.isItalic -> "BOLDITALIC"
            obj.isBold -> "BOLD"
            else -> "ITALIC"
        }
        return String.format("%s-%s-%s", obj.family, styStr, "" + obj.size)
    }

    override fun decode(v: String) = Font.decode(v)!!
}