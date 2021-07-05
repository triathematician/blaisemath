package com.googlecode.blaisemath.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.google.common.collect.Maps
import com.googlecode.blaisemath.encode.ColorCoder
import com.googlecode.blaisemath.palette.MapPalette
import com.googlecode.blaisemath.palette.Palette
import com.googlecode.blaisemath.style.AttributeSet
import com.googlecode.blaisemath.style.AttributeSetCoder
import com.googlecode.blaisemath.util.Colors
import java.awt.Color
import java.io.IOException

/*-
* #%L
* blaise-json
* --
* Copyright (C) 2019 - 2021 Elisha Peterson
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
 * Deserializes an [AttributeSet] from a string.
 * @author Elisha Peterson
 */
class PaletteDeserializer : JsonDeserializer<Palette?>() {
    @Throws(IOException::class)
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Palette? {
        val `val` = p.readValueAs(String::class.java)
        return toPalette(AttributeSetCoder().decode(`val`))
    }

    companion object {
        /**
         * Convert attribute set to palette, by restricting to just values that are colors or color strings.
         * @param attr attribute set
         * @return palette
         */
        fun toPalette(attr: AttributeSet?): Palette? {
            val cols: MutableMap<String?, Color?>? = Maps.newLinkedHashMap()
            attr.getAttributeMap().forEach { (key: String?, `val`: Any?) ->
                if (`val` is String) {
                    `val` = (`val` as String).trim { it <= ' ' }
                }
                if (`val` is Color) {
                    cols[key] = `val` as Color
                } else if (`val` is String && ColorCoder.decodable(`val` as String)) {
                    cols[key] = Colors.decode(`val` as String)
                }
            }
            return MapPalette.create(cols)
        }
    }
}