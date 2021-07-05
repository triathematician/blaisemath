package com.googlecode.blaisemath.json

import org.junit.Assert
import org.junit.Test
import java.awt.geom.Rectangle2D

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
*/   class RectProxyTest {
    @Test
    fun testToRange() {
        val rp = Rectangle2DProxy()
        rp.x = 1.0
        rp.y = 2.0
        rp.width = 3.0
        rp.height = 3.0
        Assert.assertEquals(Rectangle2D.Double(1, 2, 3, 3), rp.toRectangle())
    }

    @Test
    fun testCreate() {
        val rp = Rectangle2DProxy(Rectangle2D.Double())
        Assert.assertEquals(0.0, rp.x, 1e-6)
        Assert.assertEquals(0.0, rp.y, 1e-6)
        Assert.assertEquals(0.0, rp.width, 1e-6)
        Assert.assertEquals(0.0, rp.height, 1e-6)
    }
}