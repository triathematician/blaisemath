package com.googlecode.blaisemath.palette

import com.google.common.collect.Maps
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before
import java.awt.Color
import java.util.*

/*
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
 * A mutable palette backed by a key-value map.
 * @author Elisha Peterson
 */
class MapPalette : MutablePalette() {
    private var name: String? = null
    private var colors: MutableMap<String?, Color?>? = Maps.newLinkedHashMap()

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    override fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getColors(): MutableMap<String?, Color?>? {
        return colors
    }

    fun setColors(colors: MutableMap<String?, Color?>?) {
        this.colors = Maps.newLinkedHashMap(Objects.requireNonNull(colors))
    }

    //</editor-fold>
    override fun remove(key: String?): Color? {
        return colors.remove(key)
    }

    override fun set(key: String?, value: Color?) {
        colors[key] = value
    }

    override fun colors(): MutableCollection<String?>? {
        return colors.keys
    }

    override fun color(id: String?): Color? {
        return colors.get(id)
    }

    companion object {
        fun create(colors: MutableMap<String?, Color?>?): MapPalette? {
            val res = MapPalette()
            res.setColors(colors)
            return res
        }
    }
}