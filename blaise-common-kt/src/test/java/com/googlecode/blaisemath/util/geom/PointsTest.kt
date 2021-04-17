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

package com.googlecode.blaisemath.util.geom

import org.junit.Assert
import org.junit.Test
import java.awt.Point

class PointsTest {
    @Test
    fun testFormatPoint() {
        Assert.assertEquals("(0, 0)", Point().format(0))
        Assert.assertEquals("(0.0, 0.0)", Point().format( 1))
        Assert.assertEquals("(1.02, -3.00)", point2(1.02, -3).format( 2))
    }
}