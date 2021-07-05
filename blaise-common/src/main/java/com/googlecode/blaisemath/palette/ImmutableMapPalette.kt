package com.googlecode.blaisemath.palette

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
 * A color palette based on an immutable map.
 * @author Elisha Peterson
 */
class ImmutableMapPalette(map: MutableMap<String?, Color?>?) : Palette() {
    private val map: MutableMap<String?, Color?>?
    override fun colors(): MutableCollection<String?>? {
        return map.keys
    }

    override fun color(id: String?): Color? {
        return map.get(id)
    }

    init {
        this.map = Collections.unmodifiableMap(Objects.requireNonNull(map))
    }
}