package com.googlecode.blaisemath.style

import org.junit.Assert
import org.junit.Test
import java.awt.Point

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

class MarkersTest {
    @Test
    fun testGetAvailableMarkers() {
        val result = Markers.availableMarkers
        Assert.assertFalse(result.isEmpty())
        for (m in result) {
            val s = m.create(Point(), .5, 1f)
            Assert.assertNotNull(s)
        }
    }
}